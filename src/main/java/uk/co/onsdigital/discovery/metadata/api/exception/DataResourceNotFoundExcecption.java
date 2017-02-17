package uk.co.onsdigital.discovery.metadata.api.exception;

/**
 * Indicates that a dataset was not found.
 */
public class DataResourceNotFoundExcecption extends NotFoundException {
    public DataResourceNotFoundExcecption(String message) {
        super(message);
    }
}
