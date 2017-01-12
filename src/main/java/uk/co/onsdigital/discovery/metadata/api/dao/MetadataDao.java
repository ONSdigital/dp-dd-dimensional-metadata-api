package uk.co.onsdigital.discovery.metadata.api.dao;

import uk.co.onsdigital.discovery.metadata.api.exception.ConceptSystemNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.GeographicHierarchyNotFoundException;
import uk.co.onsdigital.discovery.model.ConceptSystem;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;
import uk.co.onsdigital.discovery.model.GeographicAreaHierarchy;

import java.util.List;
import java.util.Set;

/**
 * Data Access Object for retrieving metadata from the data discovery database.
 */
public interface MetadataDao {

    /**
     * Return the number of all datasets present in the database.
     *
     * @return the number of datasets in the database.
     */
    long countDataSets();

    /**
     * Find a page of available datasets present in the database.
     *
     * @param pageNumber the number of the page to get, starting from 1.
     * @param pageSize the number of results to include in a page.
     * @return a list of available datasets for the given pageNumber and pageSize.
     */
    List<DimensionalDataSet> findDataSetsPage(int pageNumber, int pageSize);

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

    /**
     * Find a given geographic hierarchy that is referenced in a given dataset.
     *
     * @param dataSetId the id of the dataset.
     * @param geographyId the name of the geographic hierarchy, such as {@literal 2013ADMIN}.
     * @return the matching geographic hierarchy if it exists and is referenced in the given dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     * @throws GeographicHierarchyNotFoundException if the geographic hierarchy does not exist or is not referenced in the given dataset.
     */
    GeographicAreaHierarchy findGeographyInDataSet(String dataSetId, String geographyId)
            throws DataSetNotFoundException, GeographicHierarchyNotFoundException;
}
