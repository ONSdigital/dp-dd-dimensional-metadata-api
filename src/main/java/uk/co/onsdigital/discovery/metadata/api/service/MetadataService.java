package uk.co.onsdigital.discovery.metadata.api.service;

import uk.co.onsdigital.discovery.metadata.api.dto.DataSet;
import uk.co.onsdigital.discovery.metadata.api.dto.DimensionMetadata;
import uk.co.onsdigital.discovery.metadata.api.dto.ResultPage;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;

import java.util.List;

/**
 * The metadata service provides an API for retrieving available datasets and querying them for available dimensions.
 */
public interface MetadataService {

    /**
     * Return a page of datasets defined in the database.
     *
     * @param pageNumber the number of the page to return, starting at 1.
     * @param pageSize the number of datasets to include in each page.
     * @return all available datasets.
     */
    ResultPage<DataSet> listAvailableDataSets(int pageNumber, int pageSize);

    /**
     * Find a particular dataset by id.
     *
     * @param dataSetId the id of the dataset to retrieve.
     * @return the matching dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    DataSet findDataSetById(String dataSetId) throws DataSetNotFoundException;

    /**
     * List the available dimensions for a particular dataset.
     *
     * @param dataSetId the id of the dataset to list dimensions for.
     * @return the dimensions defined by that dataset.
     */
    List<DimensionMetadata> listDimensionsForDataSet(String dataSetId) throws DataSetNotFoundException;

    /**
     * Find the definition of a dimension defined on a particular dataset. The particular options for the dimension will
     * be filtered to only contain those that actually occur in the dataset.
     *
     * @param dataSetId the id of the dataset.
     * @param dimensionId the id of the dimension to query.
     * @param viewType the type of view to use for the dimension options.
     * @return the given dimension definition for the given dataset.
     * @throws DimensionNotFoundException if the dimension does not exist in this dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    DimensionMetadata findDimensionById(String dataSetId, String dimensionId, DimensionViewType viewType) throws DataSetNotFoundException,
            DimensionNotFoundException;

}
