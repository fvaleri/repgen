/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import it.fvaleri.repgen.utility.CommonUtils;

/**
 * Dynamic data holder for a JRXML template.
 * Each data instance contains PDF page text or XLS row data.
 */
public class Data {
    private Long id;
    private String name;
    private String refId;
    private List<Text> texts;
    private List<String> cells;

    public Data() {
        this.texts = new ArrayList<>();
        this.cells = new ArrayList<>();
    }

    // needed to retrieve the field in jrxml
    public Data getData() {
        return this;
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

    public String getRefId() {
        return this.refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public List<Text> getTexts() {
        return texts;
    }

    public void setTexts(List<Text> texts) {
        this.texts = texts;
    }

    public String getText(String name) {
        Optional<Text> text = texts.stream().filter(t -> t.getName().equals(name)).findAny();
        if (text.isPresent()) {
            return text.get().getValue();
        }
        return null;
    }

    public List<String> getCells() {
        return cells;
    }

    public void setCells(List<String> cells) {
        this.cells = cells;
    }

    public String getCell(int i) {
        return cells.get(i);
    }

    public Date getCellDt(int i) throws Exception {
        return CommonUtils.parseDate(cells.get(i));
    }

    public BigDecimal getCellBd(int i) throws Exception {
        return new BigDecimal(cells.get(i));
    }

    @Override
    public String toString() {
        return "{" + " id='" + getId() + "'" + ", name='" + getName() + "'" + ", refId='" + getRefId() + "'"
                + ", texts='" + getTexts() + "'" + ", cells='" + getCells() + "'" + "}";
    }
}
