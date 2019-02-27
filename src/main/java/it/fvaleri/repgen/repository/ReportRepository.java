/*
 * Copyright 2018 Federico Valeri.
 * Licensed under the Apache License 2.0 (see LICENSE file).
 */
package it.fvaleri.repgen.repository;

import java.util.List;

import it.fvaleri.repgen.domain.Data;
import it.fvaleri.repgen.domain.Report;
import it.fvaleri.repgen.domain.Request;
import it.fvaleri.repgen.domain.Search;

public interface ReportRepository {
    List<String> loadActiveModels();

    Long savePdfRequest(Request request);

    Long saveXlsRequest(Request request);

    List<Report> findNextRequests(Integer num);

    List<Report> findReports(Search search);

    void buildStart(Report report);

    void buildSuccess(Report report);

    void buildError(Report report);

    List<Data> loadPdfDataset(Report report);

    List<Data> loadXlsDataset(Report report);
}
