/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.domain;

import java.util.Date;
import java.util.List;

import it.fvaleri.repgen.utility.CommonUtils;

public class Search {
    private Long id;
    private String name;
    private Date from;
    private Date to;
    private String direction = "next";
    private Long lastSeen = 0L;

    private Long pageSize = 10L;
    private Boolean contAttached = false;
    private List<Report> reports;
    private Long firstId;
    private Long lastId;
    private String message;

    public Search() {
    }

    public Search(Long id) {
        if (id != null) {
            this.id = id;
            this.contAttached = true;
        }
    }

    public Search(Long id, String name, String from, String to, String direction, Long lastSeen) {
        if (id != null) {
            this.id = id;
        }
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (from != null) {
            this.from = CommonUtils.parseDate(from);
        }
        if (to != null) {
            this.to = CommonUtils.parseDate(to);
        }
        if (direction != null) {
            this.direction = direction;
        }
        if (lastSeen != null) {
            this.lastSeen = lastSeen;
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public String getDirection() {
        return direction;
    }

    public Long getLastSeen() {
        return lastSeen;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Boolean isContAttached() {
        return contAttached;
    }

    public void setContAttached(Boolean contAttached) {
        this.contAttached = contAttached;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public Long getFirstId() {
        return firstId;
    }

    public void setFirstId(Long firstId) {
        this.firstId = firstId;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getShortMessage() {
        return CommonUtils.shorten(message, 40);
    }

    @Override
    public String toString() {
        return "{" + " id='" + getId() + "'" + ", name='" + getName() + "'" + ", from='" + getFrom() + "'" + ", to='"
                + getTo() + "'" + ", direction='" + getDirection() + "'" + ", lastSeen='" + getLastSeen() + "'"
                + ", pageSize='" + getPageSize() + "'" + ", contAttached='" + isContAttached() + "'" + ", reports='"
                + getReports() + "'" + ", firstId='" + getFirstId() + "'" + ", lastId='" + getLastId() + "'"
                + ", message='" + getMessage() + "'" + "}";
    }
}
