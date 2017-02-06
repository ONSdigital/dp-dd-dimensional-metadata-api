package uk.co.onsdigital.discovery.metadata.api.model;

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
        @NamedQuery(name = "Dimension.findByDataSetId", query = "SELECT d FROM Dimension d LEFT JOIN FETCH d.hierarchy WHERE d.dataSet.id = :dataSetId ORDER BY d.name")
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

    public Dimension() {
        // Default constructor for JPA
    }

    @VisibleForTesting
    public Dimension(DimensionalDataSet dataSet, String name, DimensionValue... values) {
        this.dataSet = dataSet;
        this.name = name;
        this.values = Arrays.asList(values);
    }

    public DimensionalDataSet getDataSet() {
        return dataSet;
    }

    public String getName() {
        return name;
    }

    public List<DimensionValue> getValues() {
        return values;
    }

    public Hierarchy getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(Hierarchy hierarchy) {
        this.hierarchy = hierarchy;
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
