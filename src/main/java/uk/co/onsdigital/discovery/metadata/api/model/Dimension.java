package uk.co.onsdigital.discovery.metadata.api.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by neil on 12/12/2016.
 */
public class Dimension {
    private String id;
    private String name;
    private Set<DimensionOption> options = new HashSet<DimensionOption>();

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

    public Set<DimensionOption> getOptions() {
        return options;
    }

    public void setOptions(Set<DimensionOption> options) {
        this.options = options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Dimension dimension = (Dimension) o;

        return options != null ? options.equals(dimension.options) : dimension.options == null;
    }

    @Override
    public int hashCode() {
        return options != null ? options.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Dimension{" +
                "options=" + options +
                '}';
    }
}
