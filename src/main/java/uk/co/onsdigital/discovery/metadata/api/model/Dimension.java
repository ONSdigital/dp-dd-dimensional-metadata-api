package uk.co.onsdigital.discovery.metadata.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import uk.co.onsdigital.discovery.model.DimensionValue;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
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
    @Column(name = "name")
    private String name;

    @OneToMany
    @JoinColumns({
            @JoinColumn(name = "dimensional_data_set_id", referencedColumnName = "dimensional_data_set_id", columnDefinition = "uuid", insertable = false, updatable = false),
            @JoinColumn(name = "name", referencedColumnName = "name", insertable = false, updatable = false)
    })
    private List<DimensionValue> values;

    @Transient
    private String url;

    @Transient
    private List<DimensionOption> options;

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
