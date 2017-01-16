package uk.co.onsdigital.discovery.metadata.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A possible option for a dimension, such as <em>Male</em> or <em>Female</em> for the dimension <em>Sex</em>.
 */
public class DimensionOption implements Comparable<DimensionOption> {
    private final String id;
    private final String name;
    private final SortedSet<DimensionOption> options = new TreeSet<>();

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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Set<DimensionOption> getOptions() {
        return options;
    }

    public void addOption(DimensionOption option) {
        this.options.add(option);
    }

    @Override
    public int compareTo(DimensionOption that) {
        return ComparisonChain.start()
                .compare(this.id, that.id, String.CASE_INSENSITIVE_ORDER)
                .compare(this.name, that.name, String.CASE_INSENSITIVE_ORDER)
                .compare(this.options, that.options, Ordering.natural().lexicographical())
                .result();
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
                ", options=" + options +
                '}';
    }
}
