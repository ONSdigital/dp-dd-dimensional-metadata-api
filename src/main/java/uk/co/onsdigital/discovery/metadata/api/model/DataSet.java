package uk.co.onsdigital.discovery.metadata.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Represents metadata about a particular dataset.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataSet {

    private String id;
    private String s3URL;
    private String title;
    private String url;
    private Metadata metadata = new Metadata();
    private Set<Dimension> dimensions = Collections.emptySet();
    private String dimensionsUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getS3URL() {
        return s3URL;
    }

    public void setS3URL(String s3URL) {
        this.s3URL = s3URL;
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

    public Set<Dimension> getDimensions() {
        return dimensions;
    }

    public void setDimensions(Set<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public String toString() {
        return "DataSet{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", s3URL='" + s3URL + '\'' +
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
