package uk.co.onsdigital.discovery.metadata.api.model;

import java.util.Objects;

/**
 * A possible option for a dimension, such as <em>Male</em> or <em>Female</em> for the dimension <em>Sex</em>.
 */
public class DimensionOption implements Comparable<DimensionOption> {
    private final String id;
    private final String name;

    public DimensionOption(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(DimensionOption that) {
        return this.name.compareTo(that.name);
    }

    @Override
    public boolean equals(Object that) {
        return this == that || that instanceof DimensionOption && this.compareTo((DimensionOption) that) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "DimensionOption{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
