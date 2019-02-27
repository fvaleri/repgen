/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.fvaleri.repgen.domain.Wrapper;
import it.fvaleri.repgen.service.SchedulerService;

@RestController
@RequestMapping("/api/scheduler")
@CrossOrigin(origins = "*")
public class SchedulerController {
    @Autowired
    private SchedulerService schedulerService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Wrapper getStatus() {
        return new Wrapper("status", schedulerService.status());
    }

    @RequestMapping(value = "/{cmd}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Wrapper runCommand(@PathVariable String cmd) {
        return new Wrapper("status", schedulerService.runCommand(cmd));
    }
}
