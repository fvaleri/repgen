/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.fvaleri.repgen.domain.Report;
import it.fvaleri.repgen.domain.Request;
import it.fvaleri.repgen.domain.Search;
import it.fvaleri.repgen.domain.Wrapper;
import it.fvaleri.repgen.service.ReportService;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Wrapper postReport(@RequestBody Request request) {
        return new Wrapper("reportId", reportService.createRequest(request));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Search getReports() {
        return reportService.searchReports(new Search());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Report getReport(@PathVariable Long id) {
        return reportService.searchReport(id);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Search searchReports(@RequestBody Search search) {
        return reportService.searchReports(search);
    }
}
