/*
 * Copyright Federico Valeri.
 * License: Apache License 2.0 (see the file LICENSE).
 */
package it.fvaleri.repgen.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequest extends RuntimeException {
    private static final Logger LOG = LoggerFactory.getLogger(BadRequest.class);

    public BadRequest(String message) {
        super(message);
        LOG.error(message);
    }
}
