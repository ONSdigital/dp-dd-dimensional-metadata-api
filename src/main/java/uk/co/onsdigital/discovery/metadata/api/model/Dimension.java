package uk.co.onsdigital.discovery.metadata.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents metadata about a dimension of a dataset.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Dimension implements Comparable<Dimension> {
    private String id;
    private String name;
    private String url;
    private Set<DimensionOption> options = new HashSet<>();

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

    public Set<DimensionOption> getOptions() {
        return options;
    }

    public void setOptions(Set<DimensionOption> options) {
        this.options = options;
    }

    @Override
    public int compareTo(Dimension that) {
        return this.name.compareTo(that.name);
    }

    @Override
    public boolean equals(Object that) {
        return this == that || that instanceof Dimension && this.compareTo((Dimension) that) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Dimension{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", options=" + options +
                '}';
    }
}
