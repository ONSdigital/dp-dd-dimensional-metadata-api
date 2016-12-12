package uk.co.onsdigital.discovery.metadata.api.exception;

/**
 * Indicates that a resource that was requested was not found in the database.
 */
public abstract class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}
