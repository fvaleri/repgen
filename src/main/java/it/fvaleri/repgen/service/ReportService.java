/*
 * Copyright 2018 Federico Valeri.
 * Licensed under the Apache License 2.0 (see LICENSE file).
 */
package it.fvaleri.repgen.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.fvaleri.repgen.Configuration;
import it.fvaleri.repgen.domain.Report;
import it.fvaleri.repgen.domain.Request;
import it.fvaleri.repgen.domain.Search;
import it.fvaleri.repgen.exception.BadRequest;
import it.fvaleri.repgen.exception.InternalError;
import it.fvaleri.repgen.exception.NotFound;
import it.fvaleri.repgen.repository.ReportRepository;
import it.fvaleri.repgen.utility.ReportCompiler;
import it.fvaleri.repgen.utility.ReportCompiler.ReportType;
import it.fvaleri.repgen.utility.CommonUtils;
import it.fvaleri.repgen.utility.JasperReportCompiler;

/**
 * Report generation from pre-defined model and template.
 * Singleton; rollback only on unchecked exceptions.
 */
@Service
public class ReportService {
    private static final Logger LOG = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private Configuration configuration;
    @Autowired
    @Qualifier("jdbc")
    private ReportRepository reportRepository;

    private ReportCompiler reportCompiler;
    private List<String> activeModels;

    @PostConstruct
    public void setUp() {
        this.reportCompiler = new JasperReportCompiler.Builder()
            .withLocale(Locale.forLanguageTag(configuration.getLocale()))
            .withTemplateHome(configuration.getTemplateHome())
            .withInMemoryComp(configuration.isInMemoryComp())
            .build();
        this.activeModels = reportRepository.loadActiveModels();
        LOG.info("Report service OK");
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Long createRequest(Request request) {
        LOG.trace("Request {}", request);
        if (request == null || request.getModel() == null || !activeModels.contains(request.getModel().getName())
                || !request.hasTexts() && !request.hasCells()) {
            throw new BadRequest("Invalid request");
        }

        if (request.hasTexts()) {
            return reportRepository.savePdfRequest(request);
        } else if (request.hasCells()) {
            return reportRepository.saveXlsRequest(request);
        } else {
            throw new BadRequest("No data found");
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void buildReport(Report report) {
        if (report == null || report.getModel() == null || !activeModels.contains(report.getModel().getName())) {
            throw new InternalError("Invalid report");
        }

        try {
            String modelName = report.getModel().getName();
            if (modelName != null && activeModels.contains(modelName)) {
                LOG.debug("Report {} build", report.getId());
                report.setStart(new Date());
                reportRepository.buildStart(report);
                ReportType reportType = report.getModel().getReportType();
                LinkedHashMap<String, Object> reps = new LinkedHashMap<>();
                if (reportType == ReportType.PDF) {
                    reps.put(report.getModel().getName(), reportRepository.loadPdfDataset(report));
                } else if (reportType == ReportType.XLS) {
                    reps.put(report.getModel().getName(), reportRepository.loadXlsDataset(report));
                }

                byte[] content = reportCompiler.compile(reportType, reps);
                report.setContent(content);
                report.setFilename(CommonUtils.sha256(content));
                report.setEnd(new Date());
                reportRepository.buildSuccess(report);
                LOG.debug("Report {} done", report.getId());
                if (report.getContent() == null) {
                    LOG.warn("Report {} empty", report.getId());
                }
            }
        } catch (Exception e) {
            LOG.error("Report {} error - {}", report.getId(), e.getMessage());
            report.setEnd(new Date());
            report.setMessage("ERROR");
            reportRepository.buildError(report);
        }
    }

    public Report searchReport(Long id) {
        List<Report> reports = searchReports(new Search(id)).getReports();
        return reports.get(0);
    }

    public Search searchReports(Search search) {
        LOG.debug("Search {}", search);
        List<Report> reports = reportRepository.findReports(search);
        search.setReports(CommonUtils.sortReports(reports));

        if (search.getReports() == null || search.getReports().size() == 0) {
            throw new NotFound("No report found");
        } else if (search.getReports().size() == 1) {
            LOG.debug("Found {} report", search.getReports().size());
        } else if (search.getReports().size() > 1) {
            LOG.debug("Found {} reports", search.getReports().size());
            search.setFirstId(search.getReports().get(0).getId());
            search.setLastId(search.getReports().get(search.getReports().size() - 1).getId());
        }
        return search;
    }
}
