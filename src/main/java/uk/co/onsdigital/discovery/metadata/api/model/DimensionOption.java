package uk.co.onsdigital.discovery.metadata.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.onsdigital.discovery.model.HierarchyLevelType;

import java.util.List;
import java.util.UUID;

/**
 * A dimension option.
 */
public class DimensionOption {

    private UUID id;
    private String code;
    private String name;
    private HierarchyLevelType levelType;
    private List<DimensionOption> children;

    public DimensionOption(UUID id, String code, String name, HierarchyLevelType levelType, List<DimensionOption> children) {
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
    public List<DimensionOption> getChildren() {
        return children;
    }
}
