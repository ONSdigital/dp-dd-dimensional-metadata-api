package uk.co.onsdigital.discovery.metadata.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import uk.co.onsdigital.discovery.model.*;

import java.util.Comparator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

/**
 * A dimension option.
 */
public class DimensionOption implements Comparable<DimensionOption> {

    private UUID id;
    private String code;
    private String name;
    private HierarchyLevelType levelType;
    private SortedSet<DimensionOption> children;

    public DimensionOption(UUID id, String code, String name, HierarchyLevelType levelType) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.levelType = levelType;
        this.children = null;
    }

    public DimensionOption(UUID id, String code, String name) {
        this(id, code, name, null);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getCode() { return code; }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public HierarchyLevelType getLevelType() {
        return levelType;
    }

    @JsonProperty("options")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<DimensionOption> getChildren() {
        return children;
    }

    /**
     * Adds a new child option to this dimension option, initialising the children set if necessary.
     *
     * @param childOption the child option to add to this node.
     * @return {@code true} if the child did not already exist in the set of children, otherwise {@code false}.
     */
    public boolean addChild(DimensionOption childOption) {
        if (children == null) {
            children = new TreeSet<>();
        }
        return children.add(childOption);
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty(defaultValue = "false")
    public boolean isEmpty() {
        return id == null;
    }

    /**
     * Compares dimension options by hierarchy level type (if present) and then alphabetically by name.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final DimensionOption that) {
        return ComparisonChain.start()
                .compare(this.levelType, that.levelType, LevelTypeComparator.INSTANCE)
                .compare(this.name, that.name, Ordering.from(String.CASE_INSENSITIVE_ORDER).nullsLast())
                .compare(this.code, that.code, Ordering.natural().nullsLast())
                .result();
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof DimensionOption && this.compareTo((DimensionOption) that) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, code, levelType);
    }

    @Override
    public String toString() {
        return "DimensionOption{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", levelType=" + levelType +
                ", children=" + children +
                '}';
    }

    /**
     * Orders level types by level and then alphabetically by name (case-insensitive).
     */
    private enum LevelTypeComparator implements Comparator<HierarchyLevelType> {
        INSTANCE;

        @Override
        public int compare(HierarchyLevelType h1, HierarchyLevelType h2) {
            if (h1 == null || h2 == null) {
                return Ordering.arbitrary().nullsFirst().compare(h1, h2);
            }
            return ComparisonChain.start()
                    .compare(h1.getLevel(), h2.getLevel(), Ordering.natural().nullsFirst())
                    .compare(h1.getName(), h2.getName(), Ordering.from(String.CASE_INSENSITIVE_ORDER).nullsFirst())
                    .result();
        }
    }
}
