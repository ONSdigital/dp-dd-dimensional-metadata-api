package uk.co.onsdigital.discovery.metadata.api.service;

import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.model.DataSet;
import uk.co.onsdigital.discovery.metadata.api.model.Dimension;

import java.util.Set;

/**
 * The metadata service provides an API for retrieving available datasets and querying them for available dimensions.
 */
public interface MetadataService {

    /**
     * List all datasets defined in the database.
     *
     * @return all available datasets.
     */
    Set<DataSet> listAvailableDataSets();

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
    Set<Dimension> listDimensionsForDataSet(String dataSetId);

    /**
     * Find the definition of a dimension defined on a particular dataset. The particular options for the dimension will
     * be filtered to only contain those that actually occur in the dataset.
     *
     * @param dataSetId the id of the dataset.
     * @param dimensionId the id of the dimension to query.
     * @return the given dimension definition for the given dataset.
     * @throws DimensionNotFoundException if the dimension does not exist in this dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    Dimension findDimensionById(String dataSetId, String dimensionId) throws DataSetNotFoundException,
            DimensionNotFoundException;

}
