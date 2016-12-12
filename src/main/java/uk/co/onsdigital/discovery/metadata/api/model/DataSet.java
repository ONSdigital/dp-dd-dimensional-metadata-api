package uk.co.onsdigital.discovery.metadata.api.model;

import java.util.Set;

/**
 * Represents metadata about a particular dataset.
 */
public class DataSet {

    private String id;
    private String title;
    private String url;
    private String description;
    private Set<Dimension> dimensions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DataSet{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", description=" + description +
                '}';
    }
}
