package uk.co.onsdigital.discovery.metadata.api.dao;

import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.VariableNotFoundException;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;
import uk.co.onsdigital.discovery.model.Variable;

import java.util.List;

/**
 * Data Access Object for retrieving metadata from the data discovery database.
 */
public interface MetadataDao {
    /**
     * Find all datasets present in the database.
     *
     * @return a list of all available datasets.
     */
    List<DimensionalDataSet> findAllDataSets();

    /**
     * Find a particular dataset by id.
     * @param dataSetId the dataset id.
     * @return the matching dataset if found.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    DimensionalDataSet findDataSetById(String dataSetId) throws DataSetNotFoundException;

    /**
     * Return the variables that are referenced in the given dataset.
     *
     * @param dataSetId the id of the dataset to find variables for.
     * @return the list of all variables that are referenced in the given dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    List<Variable> findVariablesInDataSet(String dataSetId) throws DataSetNotFoundException;

    /**
     * Find a given variable that is referenced in a given dataset.
     *
     * @param dataSetId the id of the dataset.
     * @param variableId the id of the variable.
     * @return the matching variable if it exists and is referenced in the given dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     * @throws Variable if the variable does not exist or is not referenced in the given dataset.
     */
    Variable findVariableByDataSetAndVariableId(String dataSetId, String variableId)
            throws DataSetNotFoundException, VariableNotFoundException;
}
