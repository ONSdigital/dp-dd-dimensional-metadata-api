package uk.co.onsdigital.discovery.metadata.api.exception;

/**
 * Indicates that a dimension was not found in the database.
 */
public class DimensionNotFoundException extends NotFoundException {
    public DimensionNotFoundException(String message) {
        super(message);
    }
}
