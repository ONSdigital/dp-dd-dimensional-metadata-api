package uk.co.onsdigital.discovery.metadata.api.dao;

import org.springframework.stereotype.Repository;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.model.Dimension;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the MetadataDao using JPA.
 */
@Repository
public class MetadataDaoImpl implements MetadataDao {
    private final EntityManager entityManager;

    public MetadataDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public long countDataSets() {
        return entityManager.createNamedQuery("DimensionalDataSet.count", Long.class).getSingleResult();
    }

    @Override
    public List<DimensionalDataSet> findDataSetsPage(int pageNumber, int pageSize) {
        final int firstPageOffset = (pageNumber - 1) * pageSize;
        return entityManager.createNamedQuery("DimensionalDataSet.findAll", DimensionalDataSet.class)
                .setFirstResult(firstPageOffset).setMaxResults(pageSize).getResultList();
    }

    @Override
    public DimensionalDataSet findDataSetById(String dataSetId) throws DataSetNotFoundException {
        final DimensionalDataSet dataSet = entityManager.find(DimensionalDataSet.class, UUID.fromString(dataSetId));
        if (dataSet == null) {
            throw new DataSetNotFoundException("No such dataset: " + dataSetId);
        }
        return dataSet;
    }

    @Override
    public List<Dimension> findDimensionsForDataSet(String dataSetId) throws DataSetNotFoundException {
        return entityManager.createNamedQuery("Dimension.findByDataSetId", Dimension.class)
                .setParameter("dataSetId", UUID.fromString(dataSetId))
                .getResultList();
    }
}
