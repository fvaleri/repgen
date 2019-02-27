/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFound extends RuntimeException {
    private static final Logger LOG = LoggerFactory.getLogger(NotFound.class);

    public NotFound(String message) {
        super(message);
        LOG.info(message);
    }
}
