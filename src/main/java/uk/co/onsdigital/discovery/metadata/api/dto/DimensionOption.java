package uk.co.onsdigital.discovery.metadata.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.onsdigital.discovery.model.HierarchyLevelType;

import java.util.Set;
import java.util.UUID;

/**
 * A dimension option.
 */
public class DimensionOption {

    private UUID id;
    private String code;
    private String name;
    private HierarchyLevelType levelType;
    private Set<DimensionOption> children;

    public DimensionOption(UUID id, String code, String name, HierarchyLevelType levelType, Set<DimensionOption> children) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.levelType = levelType;
        this.children = children;
    }

    public DimensionOption(UUID id, String code, String name) {
        this(id, code, name, null, null);
    }

    public DimensionOption(UUID id, String name) {
        this(id, null, name);
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
    public Set<DimensionOption> getChildren() {
        return children;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty(defaultValue = "false")
    public boolean isEmpty() {
        return id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DimensionOption that = (DimensionOption) o;

        if (code != null ? !code.equals(that.code) : that.code != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return levelType != null ? levelType.equals(that.levelType) : that.levelType == null;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (levelType != null ? levelType.hashCode() : 0);
        return result;
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
}
