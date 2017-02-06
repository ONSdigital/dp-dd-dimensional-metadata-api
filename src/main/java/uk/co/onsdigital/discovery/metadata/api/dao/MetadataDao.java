package uk.co.onsdigital.discovery.metadata.api.dao;

import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.model.Dimension;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;

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
}
