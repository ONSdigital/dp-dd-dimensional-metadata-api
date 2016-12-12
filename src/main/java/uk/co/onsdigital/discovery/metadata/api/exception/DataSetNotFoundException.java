package uk.co.onsdigital.discovery.metadata.api.exception;

/**
 * Indicates that a dataset was not found.
 */
public class DataSetNotFoundException extends NotFoundException {
    public DataSetNotFoundException(String message) {
        super(message);
    }
}
