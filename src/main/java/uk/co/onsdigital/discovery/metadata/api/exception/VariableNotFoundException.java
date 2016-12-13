package uk.co.onsdigital.discovery.metadata.api.exception;

/**
 * Exception thrown by the {@link uk.co.onsdigital.discovery.metadata.api.dao.MetadataDao} to indicate that a variable
 * was not found in the database. A <em>Variable</em> in the data layer is equivalent to a <em>Dimension</em> at the API
 * layer.
 */
public class VariableNotFoundException extends DimensionNotFoundException {
    public VariableNotFoundException(String message) {
        super(message);
    }
}
