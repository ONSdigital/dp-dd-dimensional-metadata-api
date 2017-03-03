package uk.co.onsdigital.discovery.metadata.api.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.co.onsdigital.discovery.metadata.api.controller.MetadataController;
import uk.co.onsdigital.discovery.metadata.api.exception.DataResourceNotFoundExcecption;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.model.*;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the MetadataDao using JPA.
 */
@Repository
public class MetadataDaoImpl implements MetadataDao {
    private final EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(MetadataDaoImpl.class);


    public MetadataDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public long countDataSets() {
        return entityManager.createNamedQuery("DimensionalDataSet.count", Long.class).getSingleResult();
    }

    @Override
    public long countDataResources() {
        return entityManager.createNamedQuery("DataResource.count", Long.class).getSingleResult();
    }

    @Override
    public List<DataSet> findLegacyDataSetsPage(int pageNumber, int pageSize) {
        final int firstPageOffset = (pageNumber - 1) * pageSize;
        return entityManager.createNamedQuery("DataSet.findAll", DataSet.class)
                .setFirstResult(firstPageOffset).setMaxResults(pageSize).getResultList();
    }


    @Override
    public List<DataResource> findDataResourcesPage(int pageNumber, int pageSize) {
        final int firstPageOffset = (pageNumber - 1) * pageSize;
        return entityManager.createNamedQuery("DataResource.findAll", DataResource.class)
                .setFirstResult(firstPageOffset).setMaxResults(pageSize).getResultList();
    }

    public DataResource findDataResource(String dataResourceId) throws DataResourceNotFoundExcecption {
        final DataResource dataResource = entityManager.find(DataResource.class, dataResourceId);
        if (dataResource == null) {
            String errorMessage = "No such dataResource: " + dataResourceId;
            logger.error(errorMessage);
            throw new DataResourceNotFoundExcecption(errorMessage);
        }
        return dataResource;
    }

    @Override
    public DataSet findDataSetByUuid(String dataSetId) throws DataSetNotFoundException {
        final DataSet dataSet = entityManager.find(DataSet.class, UUID.fromString(dataSetId));
        if (dataSet == null) {
            String errorMessage = "No such dataset with uuid: " + dataSetId;
            logger.error(errorMessage);
            throw new DataSetNotFoundException(errorMessage);
        }
        return dataSet;
    }

    @Override
    public DataSet findDataSetByEditionAndVersion(String dataResourceId, String edition, int version) throws DataSetNotFoundException {
        try {
            final DataSet dataSet =
                    entityManager.createNamedQuery(DataSet.FIND_BY_EDITION_VERSION, DataSet.class)
                            .setParameter(DataSet.DATA_RESOURCE_PARAM, dataResourceId)
                            .setParameter(DataSet.EDITION_PARAM, edition)
                            .setParameter(DataSet.VERSION_PARAM, version)
                            .getSingleResult();
            return dataSet;
        } catch (NoResultException e) {
            String errorMessage = "No such dataset with dataResourceId/edition/version: " + String.join("/", new String[]{dataResourceId, edition, Integer.toString(version)});
            logger.error(errorMessage);
            throw new DataSetNotFoundException(errorMessage);        }
    }

    @Override
    public List<Dimension> findDimensionsForDataSet(String dataSetId) throws DataSetNotFoundException {
        return findDataSetByUuid(dataSetId).getDimensions();
    }

    @Override
    public List<Dimension> findDimensionsForDataSet(String dataResourceId, String edition, int version) throws DataSetNotFoundException {
        return findDataSetByEditionAndVersion(dataResourceId, edition, version).getDimensions();
    }

    @Override
    public List<Hierarchy> listAllHierarchies() {
        return entityManager.createNamedQuery(Hierarchy.FIND_ALL, Hierarchy.class).getResultList();
    }

    @Override
    public List<HierarchyEntry> findAllEntriesInHierarchy(String hierarchyId) {
        return entityManager.createNamedQuery(HierarchyEntry.FIND_BY_HIERARCHY_ID, HierarchyEntry.class)
                .setParameter(HierarchyEntry.HIERARCHY_ID_PARAM, hierarchyId)
                .getResultList();
    }
}
