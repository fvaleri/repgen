/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleDocxExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleRtfExporterConfiguration;
import net.sf.jasperreports.export.SimpleTextExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsExporterConfiguration;

/**
 * JasperReports implementation.
 */
public class JasperReportCompiler implements ReportCompiler {
    private static final Logger LOG = LoggerFactory.getLogger(JasperReportCompiler.class);

    private Locale locale;
    private String templateHome;
    private boolean inMemoryComp;

    private JasperReportCompiler() {
    }

    @Override
    public byte[] compile(ReportType type, Map<String, Object> reps) {
        return compile(type, null, reps, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private byte[] compile(ReportType type, Map<String, Object> pars, Map<String, Object> reps, List<String> subs) {
        try {
            LOG.trace("Starting new report compilation...");
            long start = System.nanoTime();

            // parameters setup
            if (pars == null) {
                pars = new HashMap<String, Object>();
            }
            pars.put(JRParameter.REPORT_LOCALE, locale);
            pars.put("templateHome", templateHome);
            if (!inMemoryComp) {
                // enable page virtualization
                pars.put(JRParameter.REPORT_VIRTUALIZER, new JRFileVirtualizer(100, templateHome));
            }

            // subreports pre compilation
            if (subs != null && subs.size() > 0) {
                for (Iterator<String> it = subs.iterator(); it.hasNext();) {
                    String subName = it.next();
                    File jasperFile = new File(templateHome + subName + ".jasper");
                    if (!jasperFile.exists()) {
                        LOG.trace("Pre compiling subrep {}", subName);
                        JasperDesign subJdes = JRXmlLoader.load(templateHome + subName + ".jrxml");
                        JasperCompileManager.compileReportToFile(subJdes, templateHome + subName + ".jasper");
                    }
                }
            }

            // reports compilation
            List<JasperPrint> printList = new ArrayList<JasperPrint>();
            if (reps != null && reps.size() > 0) {
                for (Map.Entry<String, Object> entry : reps.entrySet()) {
                    String reportName = entry.getKey();
                    LOG.trace("Compiling report {}", reportName);
                    JasperDesign jdes = JRXmlLoader.load(templateHome + reportName + ".jrxml");
                    JasperReport jrep = JasperCompileManager.compileReport(jdes);
                    JRDataSource jrds = (JRDataSource) new JRBeanCollectionDataSource((List) reps.get(reportName), false);
                    JasperPrint print = JasperFillManager.fillReport(jrep, pars, jrds);
                    printList.add(print);
                }
            } else {
                LOG.warn("Empty report map");
            }

            // final report export
            Exporter exporter = null;
            switch (type) {
                case PDF:
                    exporter = new JRPdfExporter();
                    exporter.setConfiguration(new SimplePdfExporterConfiguration());
                    break;
                case XLS:
                    exporter = new JRXlsExporter();
                    exporter.setConfiguration(new SimpleXlsExporterConfiguration());
                    break;
                case DOC:
                    exporter = new JRDocxExporter();
                    exporter.setConfiguration(new SimpleDocxExporterConfiguration());
                    break;
                case RTF:
                    exporter = new JRRtfExporter();
                    exporter.setConfiguration(new SimpleRtfExporterConfiguration());
                    break;
                case TXT:
                    exporter = new JRTextExporter();
                    exporter.setConfiguration(new SimpleTextExporterConfiguration());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown report type");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            exporter.setExporterInput(SimpleExporterInput.getInstance(printList));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            exporter.exportReport();

            double durationNs = System.nanoTime() - start;
            double durationMs = durationNs / 1_000_000f;
            DecimalFormat dc = new DecimalFormat("#0.000");
            LOG.trace("Report compilation took {} ms", dc.format(durationMs));

            byte[] report = out.toByteArray();
            out.close();

            return report;
        } catch (IOException | JRException e) {
            LOG.error("Report compilation error", e);
            throw new RuntimeException(e);
        }

    }

    public static class Builder implements ReportCompiler.Builder {
        private JasperReportCompiler compilerTmp;

        public Builder() {
            this.compilerTmp = new JasperReportCompiler();
            withLocale(Locale.getDefault());
            withTemplateHome(Builder.class.getClassLoader().getResource("templates").getFile());
            withInMemoryComp(true);
        }

        @Override
        public Builder withLocale(Locale locale) {
            compilerTmp.locale = locale;
            return this;
        }

        @Override
        public Builder withTemplateHome(String home) {
            String path = home.trim();
            if (path.charAt(path.length() - 1) != File.separatorChar) {
                path += File.separator;
            }
            compilerTmp.templateHome = path;
            return this;
        }

        @Override
        public Builder withInMemoryComp(boolean enable) {
            compilerTmp.inMemoryComp = enable;
            return this;
        }

        @Override
        public ReportCompiler build() {
            JasperReportCompiler compiler = new JasperReportCompiler();
            compiler.locale = compilerTmp.locale;
            compiler.templateHome = compilerTmp.templateHome;
            compiler.inMemoryComp = compilerTmp.inMemoryComp;
            return compiler;
        }
    }
}
