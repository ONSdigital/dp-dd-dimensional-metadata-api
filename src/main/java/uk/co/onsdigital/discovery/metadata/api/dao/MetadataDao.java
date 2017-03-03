package uk.co.onsdigital.discovery.metadata.api.dao;

import uk.co.onsdigital.discovery.metadata.api.exception.DataResourceNotFoundExcecption;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.model.*;

import java.util.List;

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

    long countDataResources();

    /**
     * Find a page of available datasets present in the database.
     *
     * @param pageNumber the number of the page to get, starting from 1.
     * @param pageSize the number of results to include in a page.
     * @return a list of available datasets for the given pageNumber and pageSize.
     */
    List<DataSet> findLegacyDataSetsPage(int pageNumber, int pageSize);

    /**
     * Find a page of available dataresources (datasets on the front-end) present in the database.
     *
     * @param pageNumber the number of the page to get, starting from 1.
     * @param pageSize the number of results to include in a page.
     * @return a list of available dataresources for the given pageNumber and pageSize.
     */
    List<DataResource> findDataResourcesPage(int pageNumber, int pageSize);

    /**
     * Find a particular dataresource by its id and list all the data set editions and versions it has.
     * @param dataResourceId the dataresource id.
     * @return the matching dataset if found.
     * @throws DataResourceNotFoundExcecption if the dataset does not exist.
     */
    DataResource findDataResource(String dataResourceId) throws DataResourceNotFoundExcecption;

    /**
     * Find a particular dataset by UUID.
     * @param dataSetId the dataset id.
     * @return the matching dataset if found.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    DataSet findDataSetByUuid(String dataSetId) throws DataSetNotFoundException;

    /**
     * Find a particular dataset by edition and version.
     * @param edition the edition of the dataset.
     * @param version the version of the dataset.
     * @return the matching dataset if found.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    DataSet findDataSetByEditionAndVersion(String dataResourceId, String edition, int version) throws DataSetNotFoundException;

    /**
     * Load the dimensions for a given dataset based on UUID.
     *
     * @param dataSetId the id of the dataset to load the dimensions for.
     * @return the dimensions defined in the given dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    List<Dimension> findDimensionsForDataSet(String dataSetId) throws DataSetNotFoundException;

    /**
     * Load the dimensions for a given dataset based on edition and version.
     *
     * @param edition the edition of the dataset.
     * @param version the version of the dataset.
     * @return the dimensions defined in the given dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    List<Dimension> findDimensionsForDataSet(String dataResourceId, String edition, int version) throws DataSetNotFoundException;

    /**
     * List all hierarchies defined in the database.
     *
     * @return the list of all hierarchies defined in the database.
     */
    List<Hierarchy> listAllHierarchies();

    /**
     * List all entries in a hierarchy in display order.
     *
     * @param hierarchyId the id of the hierarchy.
     * @return the entries from the given hierarchy, or an empty list if it does not exist.
     */
    List<HierarchyEntry> findAllEntriesInHierarchy(String hierarchyId);
}
