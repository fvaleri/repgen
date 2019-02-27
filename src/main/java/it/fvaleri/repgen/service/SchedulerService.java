/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import it.fvaleri.repgen.domain.Report;
import it.fvaleri.repgen.exception.InternalError;
import it.fvaleri.repgen.repository.ReportRepository;

/**
 * Multi-threaded scheduler for parallel report builds.
 * Singleton; rollback only on unchecked exceptions.
 */
@Service
public class SchedulerService implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    private ReportService reportService;
    @Autowired
    @Qualifier("jdbc")
    private ReportRepository reportRepository;

    private Thread schedulerThread;
    private volatile boolean active;
    private ForkJoinPool workerPool;
    private List<Long> taskIds;

    @PostConstruct
    public void setUp() {
        // using the system-level common thread pool (# thrs == # cores)
        // that leverages the new recursive fork-join API with work stealing
        this.workerPool = ForkJoinPool.commonPool();
        this.taskIds = new CopyOnWriteArrayList<>();
        startScheduler();
        LOG.info("Scheduler {}", status());
    }

    @PreDestroy
    public void tearDown() {
        workerPool.shutdownNow();
        stopScheduler();
        LOG.info("Scheduler {}", status());
    }

    public String runCommand(String cmd) {
        if (cmd != null && !cmd.isEmpty()) {
            try {
                switch (cmd) {
                    case "start":
                        startScheduler();
                        break;
                    case "stop":
                        stopScheduler();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                throw new InternalError(e);
            }
        }
        return status();
    }

    public String status() {
        if (active) {
            return "RUNNING";
        }
        return "STOPPED";
    }

    private void startScheduler() {
        if (!active) {
            LOG.info("Starting scheduler");
            active = true;
            schedulerThread = new Thread(this, "Scheduler");
            schedulerThread.start();
        }
    }

    private void stopScheduler() {
        if (active) {
            LOG.info("Stopping scheduler");
            active = false;
            schedulerThread.interrupt();
            schedulerThread = null;
        }
    }

    @Override
    public void run() {
        while (active) {
            try {

                Thread.sleep(5_000);
                // pull mode: search for new build request events
                List<Report> reports = reportRepository.findNextRequests(1_000);
                workerPool.execute(new BuildTask(reports));

                long totalHeapMb = Runtime.getRuntime().totalMemory() / 1_000_000;
                long freeHeapMb = Runtime.getRuntime().freeMemory() / 1_000_000;
                LOG.info("{} worker threads, {} build tasks (heap: {}/{} MB)", workerPool.getPoolSize(),
                        taskIds.size(), totalHeapMb - freeHeapMb, totalHeapMb);

            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                } else {
                    LOG.error("Scheduler error", e);
                }
            }
        }
    }

    private class BuildTask extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private static final int THRESHOLD = 100;
        private List<Report> reports;

        public BuildTask(List<Report> reports) {
            this.reports = reports;
        }

        @Override
        protected void compute() {
            if (reports.size() > THRESHOLD) {
                ForkJoinTask.invokeAll(subTasks());
            } else {
                processing(reports);
            }
        }

        private List<BuildTask> subTasks() {
            int size = reports.size();
            int pivot = size / 2;
            List<Report> head = reports.subList(0, pivot);
            List<Report> tail = reports.subList(pivot, size);
            List<BuildTask> subTasks = new ArrayList<>();
            subTasks.add(new BuildTask(head));
            subTasks.add(new BuildTask(tail));
            return subTasks;
        }

        private void processing(List<Report> reports) {
            List<Report> todo = reports.stream()
                .filter(r -> !taskIds.contains(r.getId()))
                .collect(toList());
            todo.forEach(r -> taskIds.add(r.getId()));
            todo.stream()
                .forEach(r -> {
                    reportService.buildReport(r);
                    taskIds.remove(r.getId());
                });
        }

        private void writeObject(ObjectOutputStream stream)
                throws IOException {
            stream.defaultWriteObject();
        }

        private void readObject(ObjectInputStream stream)
                throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
        }
    }
}
