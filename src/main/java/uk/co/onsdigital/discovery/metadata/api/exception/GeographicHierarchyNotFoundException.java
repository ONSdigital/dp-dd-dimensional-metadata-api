package uk.co.onsdigital.discovery.metadata.api.exception;

/**
 * Exception thrown by the {@link uk.co.onsdigital.discovery.metadata.api.dao.MetadataDao} to indicate that a geographic
 * hierarchy was not found in the database. A <em>GeographicHierarchy</em> in the data layer is exposed as (one type of)
 * <em>Dimension</em> at the API layer.
 */
public class GeographicHierarchyNotFoundException extends DimensionNotFoundException {
    public GeographicHierarchyNotFoundException(String message) {
        super(message);
    }
}
