package uk.co.onsdigital.discovery.metadata.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.annotations.VisibleForTesting;
import uk.co.onsdigital.discovery.model.DimensionValue;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;
import uk.co.onsdigital.discovery.model.Hierarchy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a dimension in a dataset.
 */
@Entity
@Table(name = "dimension")
@IdClass(Dimension.DimensionPK.class)
@NamedQueries({
        @NamedQuery(name = "Dimension.findByDataSetId", query = "SELECT d FROM Dimension d LEFT JOIN FETCH d.hierarchy WHERE d.dataSet.id = :dataSetId")
})
public class Dimension {

    @Id
    @ManyToOne
    @JoinColumn(name = "dimensional_data_set_id", columnDefinition = "uuid")
    private DimensionalDataSet dataSet;

    @Id
    @Column(name = "name")
    private String name;

    @OneToMany
    @JoinColumns({
            @JoinColumn(name = "dimensional_data_set_id", referencedColumnName = "dimensional_data_set_id", columnDefinition = "uuid", insertable = false, updatable = false),
            @JoinColumn(name = "name", referencedColumnName = "name", insertable = false, updatable = false)
    })
    private List<DimensionValue> values;

    @ManyToOne
    @JoinColumn(name = "hierarchy_id", referencedColumnName = "id", columnDefinition = "uuid", insertable = false, updatable = false)
    private Hierarchy hierarchy;

    @Transient
    private String url;

    @Transient
    private List<DimensionOption> options;

    public Dimension() {
        // Default constructor for JPA
    }

    @VisibleForTesting
    public Dimension(DimensionalDataSet dataSet, String name, DimensionValue... values) {
        this.dataSet = dataSet;
        this.name = name;
        this.values = Arrays.asList(values);
    }

    @JsonIgnore
    public DimensionalDataSet getDataSet() {
        return dataSet;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public List<DimensionValue> getValues() {
        return values;
    }

    @JsonIgnore
    public Hierarchy getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(Hierarchy hierarchy) {
        this.hierarchy = hierarchy;
    }

    public boolean isHierarchical() {
        return hierarchy != null;
    }

    public String getType() {
        return hierarchy == null ? "standard" : hierarchy.getType();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<DimensionOption> getOptions() {
        return options;
    }

    public void setOptions(List<DimensionOption> options) {
        this.options = options;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Composite primary key class.
     */
    static class DimensionPK implements Serializable {
        private static final long serialVersionUID = 1L;
        private UUID dataSet;
        private String name;

        @Override
        public boolean equals(Object that) {
            return this == that || that instanceof DimensionPK && Objects.equals(this.name, ((DimensionPK) that).name)
                    && Objects.equals(this.dataSet, ((DimensionPK) that).dataSet);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dataSet, name);
        }
    }
}
