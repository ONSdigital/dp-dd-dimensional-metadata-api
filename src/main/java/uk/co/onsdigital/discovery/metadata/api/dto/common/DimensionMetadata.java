package uk.co.onsdigital.discovery.metadata.api.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Metadata about a dimension in a dataset.
 */
public class DimensionMetadata {

    private String id;
    private String name;
    private String url;
    private String type;
    private boolean hierarchical;
    private List<DimensionOption> options;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isHierarchical() {
        return hierarchical;
    }

    public void setHierarchical(boolean hierarchical) {
        this.hierarchical = hierarchical;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<DimensionOption> getOptions() {
        return options;
    }

    public void setOptions(List<DimensionOption> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "DimensionMetadata{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", hierarchical=" + hierarchical +
                ", url='" + url + '\'' +
                ", options=" + options +
                '}';
    }
}
