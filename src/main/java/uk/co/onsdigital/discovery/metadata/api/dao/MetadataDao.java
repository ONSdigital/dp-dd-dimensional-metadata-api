package uk.co.onsdigital.discovery.metadata.api.dao;

import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.model.Dimension;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;
import uk.co.onsdigital.discovery.model.Hierarchy;
import uk.co.onsdigital.discovery.model.HierarchyEntry;

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
     * Load the dimensions for a given dataset.
     *
     * @param dataSetId the id of the dataset to load the dimensions for.
     * @return the dimensions defined in the given dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    List<Dimension> findDimensionsForDataSet(String dataSetId) throws DataSetNotFoundException;

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
