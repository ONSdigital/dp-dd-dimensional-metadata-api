package uk.co.onsdigital.discovery.metadata.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Indicates that a resource that was requested was not found in the database.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public abstract class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}
