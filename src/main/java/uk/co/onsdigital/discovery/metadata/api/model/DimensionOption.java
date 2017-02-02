package uk.co.onsdigital.discovery.metadata.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.onsdigital.discovery.model.HierarchyLevelType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;

/**
 * A dimension option.
 */
@Entity
@Table(name = "dimension_option")
public class DimensionOption {
    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "dimensional_data_set_id", referencedColumnName = "dimensional_data_set_id", columnDefinition = "uuid", insertable = false, updatable = false),
            @JoinColumn(name = "dimension_name", referencedColumnName = "dimension_name", insertable = false, updatable = false)
    })
    private Dimension dimension;

    @Column(name = "dimension_value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id", insertable = false, updatable = false)
    private DimensionOption parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("dimension.name")
    private List<DimensionOption> children;

    @ManyToOne
    @JoinColumn(name = "level_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    private HierarchyLevelType levelType;

    public UUID getId() {
        return id;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public String getValue() {
        return value;
    }

    @JsonIgnore
    public DimensionOption getParent() {
        return parent;
    }

    @JsonProperty("options")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<DimensionOption> getChildren() {
        return children;
    }

    public String getLevelType() {
        return levelType != null ? levelType.getName() : null;
    }
}
