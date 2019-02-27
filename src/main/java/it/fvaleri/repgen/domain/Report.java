/*
 * Copyright 2018 Federico Valeri.
 * Licensed under the Apache License 2.0 (see LICENSE file).
 */
package it.fvaleri.repgen.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.fvaleri.repgen.utility.CommonUtils;

public class Report {
    private Long id;
    private Model model;
    private String refId;
    private Date start;
    private Date end;
    private String filename;
    private byte[] content;
    private String message;

    public Report(Model model) {
        this.model = model;
    }

    public Report(Long id) {
        this.id = id;
    }

    // needed to retrieve this object as field in jrxml
    @JsonIgnore
    public Report getReport() {
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Date getStart() {
        return start;
    }

    public String getBuildStart() {
        return CommonUtils.formatDate(start);
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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getFilename() {
        return filename;
    }

    public String getFullFilename() {
        if (filename == null || filename.isEmpty()) {
            return "-";
        }
        String shortName = CommonUtils.shorten(filename, 10);
        return shortName + "." + model.getType();
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getShortMessage() {
        return CommonUtils.shorten(message, 25);
    }

    @Override
    public String toString() {
        return "{" + " id='" + getId() + "'" + ", model='" + getModel() + "'" + "}";
    }
}
