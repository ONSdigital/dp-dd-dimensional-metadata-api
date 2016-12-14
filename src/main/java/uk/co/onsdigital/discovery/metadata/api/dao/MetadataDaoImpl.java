package uk.co.onsdigital.discovery.metadata.api.dao;

import org.springframework.stereotype.Repository;
import uk.co.onsdigital.discovery.metadata.api.exception.ConceptSystemNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.model.ConceptSystem;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
    public List<DimensionalDataSet> findAllDataSets() {
        return entityManager.createNamedQuery("DimensionalDataSet.findAll", DimensionalDataSet.class).getResultList();
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
    public Set<ConceptSystem> findConceptSystemsInDataSet(String dataSetId) throws DataSetNotFoundException {
        final DimensionalDataSet dataSet = findDataSetById(dataSetId);
        final Set<ConceptSystem> conceptSystems = dataSet.getReferencedConceptSystems();
        return conceptSystems == null ? Collections.emptySet() : conceptSystems;
    }

    @Override
    public ConceptSystem findConceptSystemByDataSetAndConceptSystemName(String dataSetId, String conceptSystemName)
            throws DataSetNotFoundException, ConceptSystemNotFoundException {

        return findConceptSystemsInDataSet(dataSetId)
                .stream()
                .filter(c -> conceptSystemName.equals(c.getConceptSystem()))
                .findAny()
                .orElseThrow(() -> new ConceptSystemNotFoundException("No such concept system in dataset: " + conceptSystemName));
    }
}
