package uk.co.onsdigital.discovery.metadata.api.dao;

import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.ConceptSystemNotFoundException;
import uk.co.onsdigital.discovery.model.ConceptSystem;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;

import java.util.List;
import java.util.Set;

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
     * Return the concept systems (dimensions) that are referenced in the given dataset.
     *
     * @param dataSetId the id of the dataset to find variables for.
     * @return the list of all concept systems that are referenced in the given dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    Set<ConceptSystem> findConceptSystemsInDataSet(String dataSetId) throws DataSetNotFoundException;

    /**
     * Find a given concept system that is referenced in a given dataset.
     *
     * @param dataSetId the id of the dataset.
     * @param conceptSystem the name of the concept system.
     * @return the matching concept system if it exists and is referenced in the given dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     * @throws ConceptSystemNotFoundException if the concept system does not exist or is not referenced in the given dataset.
     */
    ConceptSystem findConceptSystemByDataSetAndConceptSystemName(String dataSetId, String conceptSystem)
            throws DataSetNotFoundException, ConceptSystemNotFoundException;
}
