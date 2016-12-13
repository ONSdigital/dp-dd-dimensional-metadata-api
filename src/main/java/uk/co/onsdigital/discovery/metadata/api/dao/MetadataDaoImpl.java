package uk.co.onsdigital.discovery.metadata.api.dao;

import org.springframework.stereotype.Repository;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.VariableNotFoundException;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;
import uk.co.onsdigital.discovery.model.Variable;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    public List<Variable> findVariablesInDataSet(String dataSetId) throws DataSetNotFoundException {
        final DimensionalDataSet dataSet = findDataSetById(dataSetId);
        final Collection<Variable> variables = dataSet.getReferencedVariables();
        return variables == null ? Collections.emptyList() : new ArrayList<>(variables);
    }

    @Override
    public Variable findVariableByDataSetAndVariableId(String dataSetId, String variableId)
            throws DataSetNotFoundException, VariableNotFoundException {
        final Long variableIdLong = Long.valueOf(variableId);
        for (Variable variable : findVariablesInDataSet(dataSetId)) {
            if (variableIdLong.equals(variable.getVariableId())) {
                return variable;
            }
        }
        throw new VariableNotFoundException("No such variable in dataset: " + variableId);
    }
}
