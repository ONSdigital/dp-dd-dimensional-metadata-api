package uk.co.onsdigital.discovery.metadata.api.model;

/**
 * Created by neil on 12/12/2016.
 */
public class Metadata {
    private final String description;

    public Metadata(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
