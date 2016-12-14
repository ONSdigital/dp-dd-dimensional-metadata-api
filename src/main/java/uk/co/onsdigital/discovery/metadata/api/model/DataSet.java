package uk.co.onsdigital.discovery.metadata.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Represents metadata about a particular dataset.
 */
public class DataSet {

    private String id;
    private String title;
    private String url;
    private Metadata metadata = new Metadata();
    private String dimensionsUrl;

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

    @JsonIgnore
    public void setDescription(String description) {
        metadata.setDescription(description);
    }

    public String getDimensionsUrl() {
        return dimensionsUrl;
    }

    public void setDimensionsUrl(String dimensionsUrl) {
        this.dimensionsUrl = dimensionsUrl;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Metadata {
        private String description;
        private List<String> taxonomies;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getTaxonomies() {
            return taxonomies;
        }

        public void setTaxonomies(List<String> taxonomies) {
            this.taxonomies = taxonomies;
        }

        @Override
        public String toString() {
            return "Metadata{" +
                    "description='" + description + '\'' +
                    ", taxonomies=" + taxonomies +
                    '}';
        }
    }
}
