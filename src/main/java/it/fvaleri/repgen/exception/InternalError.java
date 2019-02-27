/*
 * Copyright 2018 Federico Valeri.
 * Licensed under the Apache License 2.0 (see LICENSE file).
 */
package it.fvaleri.repgen.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalError extends RuntimeException {
    private static final Logger LOG = LoggerFactory.getLogger(InternalError.class);

    public InternalError(Throwable cause) {
        super(cause);
        LOG.error("Internal error", cause);
    }

    public InternalError(String message) {
        super(message);
        LOG.error(message);
    }
}
