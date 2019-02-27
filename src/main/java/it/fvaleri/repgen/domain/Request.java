/*
 * Copyright 2018 Federico Valeri.
 * Licensed under the Apache License 2.0 (see LICENSE file).
 */
package it.fvaleri.repgen.domain;

import java.util.List;

/**
 * New report build request.
 */
public class Request {
    private String name;
    private Model model;
    private List<Data> dataset;

    public Request() {
    }

    public Request(String name, Model model, List<Data> dataset) {
        this.name = name;
        this.model = model;
        this.dataset = dataset;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Model getModel() {
        return this.model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public List<Data> getDataset() {
        return this.dataset;
    }

    public void setDataset(List<Data> dataset) {
        this.dataset = dataset;
    }

    public boolean hasTexts() {
        if (dataset == null || dataset.get(0) == null || dataset.get(0).getTexts() == null) {
            return false;
        }
        return true;
    }

    public int getTextsSize() {
        return dataset.get(0).getTexts().size();
    }

    public boolean hasCells() {
        if (dataset == null || dataset.get(0) == null || dataset.get(0).getCells() == null) {
            return false;
        }
        return true;
    }

    public int getMaxRowSize() {
        return dataset.parallelStream().mapToInt(d -> d.getCells().size()).max().orElse(0);
    }

    @Override
    public String toString() {
        return "{" + " name='" + getName() + "'" + ", model='" + getModel() + "'" + ", dataset='" + getDataset() + "'"
                + "}";
    }
}