package uk.co.onsdigital.discovery.metadata.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;
import uk.co.onsdigital.discovery.model.Hierarchy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents a dimension in a dataset.
 */
@Entity
@Table(name = "dimension")
@IdClass(Dimension.DimensionPK.class)
@NamedQueries({
        @NamedQuery(name = "Dimension.findByDataSetId", query = "SELECT d FROM Dimension d WHERE d.dataSet.id = :dataSetId")
})
public class Dimension {

    @Id
    @ManyToOne
    @JoinColumn(name = "dimensional_data_set_id", columnDefinition = "uuid")
    private DimensionalDataSet dataSet;

    @Id
    @Column(name = "dimension_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "hierarchy_id", referencedColumnName = "id")
    private Hierarchy hierarchy;

    @OneToMany(mappedBy = "dimension")
    private List<DimensionOption> options;

    @Transient
    private String url;

    public DimensionalDataSet getDataSet() {
        return dataSet;
    }

    public String getName() {
        return name;
    }

    public Hierarchy getHierarchy() {
        return hierarchy;
    }

    @JsonIgnore
    public List<DimensionOption> getOptions() {
        return options;
    }

    /**
     * Returns just the top-level dimension options according to whatever hierarchy is defined for this dimension.
     * If the dimension is not hierarchical then all options are returned.
     *
     * @return the top-level options in this dimension.
     */
    @JsonProperty("options")
    public List<DimensionOption> getTopLevelOptions() {
        return options == null ? null : options.stream().filter(o -> o.getParent() == null).collect(Collectors.toList());
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
