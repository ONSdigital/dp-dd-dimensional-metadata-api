package uk.co.onsdigital.discovery.metadata.api.model;

import java.util.Set;

/**
 * JSON model of a data set.
 */
public class DataSet {

    private String id;
    private String title;
    private String url;
    private Metadata metadata;
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

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "DataSet{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
