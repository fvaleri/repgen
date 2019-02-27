/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.domain;

import java.util.Date;

import it.fvaleri.repgen.utility.ReportCompiler.ReportType;

/**
 * A model is associated to a template and can be disabled.
 */
public class Model {
    private Long id;
    private String name;
    private ReportType reportType;
    private Date start;
    private Date end;

    public Model() {
    }

    public Model(Long id, String name, ReportType reportType) {
        this.id = id;
        this.name = name;
        if (reportType != null) {
            this.reportType = reportType;
        } else {
            this.reportType = ReportType.TXT;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public String getType() {
        return reportType.toString();
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "{" + " id='" + getId() + "'" + ", name='" + getName() + "'" + ", reportType='" + getReportType() + "'"
                + ", start='" + getStart() + "'" + ", end='" + getEnd() + "'" + "}";
    }
}
