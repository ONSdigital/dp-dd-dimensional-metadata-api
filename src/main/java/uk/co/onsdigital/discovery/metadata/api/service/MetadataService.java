package uk.co.onsdigital.discovery.metadata.api.service;

import uk.co.onsdigital.discovery.metadata.api.dto.DataResourceResult;
import uk.co.onsdigital.discovery.metadata.api.dto.ResultPage;
import uk.co.onsdigital.discovery.metadata.api.dto.legacy.LegacyDataSet;
import uk.co.onsdigital.discovery.metadata.api.dto.common.DimensionMetadata;
import uk.co.onsdigital.discovery.metadata.api.dto.legacy.LegacyResultPage;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;

import java.util.List;

/**
 * The metadata service provides an API for retrieving available dataresources and datasets and querying them for available dimensions.
 */
public interface MetadataService {

    /**
     * Return a page of dataresources defined in the database.
     *
     * @param pageNumber the number of the page to return, starting at 1.
     * @param pageSize the number of datasets to include in each page.
     * @return all available dataresources.
     */
    ResultPage listAvailableDataResources(int pageNumber, int pageSize);

    /**
     * Return a page of datasets defined in the database. This is the **legacy** version.
     *
     * @param pageNumber the number of the page to return, starting at 1.
     * @param pageSize the number of datasets to include in each page.
     * @return all available datasets.
     */
    LegacyResultPage<LegacyDataSet> listAvailableVersions(int pageNumber, int pageSize);

    /**
     * Find a particular dataset by UUID.
     *
     * @param dataSetUuid the id of the dataset to retrieve.
     * @return the matching dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    LegacyDataSet findDataSetByUuid(String dataSetUuid) throws DataSetNotFoundException;

    /**
     * Find a particular dataresource by its ID and display its available editions and versions.
     *
     * @param dataResourceId the id of the dataresource to retrieve.
     * @return the matching dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    DataResourceResult findDataResource(String dataResourceId) throws DataSetNotFoundException;

    /**
     * Find a particular dataset by UUID.
     *
     * @param edition the major_label of dimensional_dataset.
     * @param version the version of a dimensional_dataset.
     * @return the matching dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    LegacyDataSet findDataSetByEditionAndVersion(String dataResourceId, String edition, int version) throws DataSetNotFoundException;

    /**
     * List the available dimensions for a particular dataset using its uuid.
     *
     * @param datasetUuid the uuid of the dataset version to list dimensions for.
     * @return the dimensions defined by that dataset.
     */
    List<DimensionMetadata> listDimensionsForDataSetUuid(String datasetUuid) throws DataSetNotFoundException;

    /**
     * List the available dimensions for a particular dataset using its edition and version.
     *
     * @param dataResourceId the dataResourced id
     * @param edition the major_label of dimensional_dataset.
     * @param version the version of a dimensional_dataset.
     * @return the dimensions defined by that dataset.
     */
    List<DimensionMetadata> listDimensionsForDataSetEditionVersion(String dataResourceId, String edition, int version) throws DataSetNotFoundException;

    /**
     * Find the definition of a dimension defined on a particular dataset based on a dimensional dataset UUID.
     * The particular options for the dimension will be filtered to only contain those that actually occur in the dataset.
     *
     * @param dataSetUuid the id of the dataset.
     * @param dimensionId the id of the dimension to query.
     * @param viewType the type of view to use for the dimension options.
     * @return the given dimension definition for the given dataset.
     * @throws DimensionNotFoundException if the dimension does not exist in this dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    DimensionMetadata findDimensionByIdWithDatasetUuid(String dataSetUuid, String dimensionId, DimensionViewType viewType) throws DataSetNotFoundException,
            DimensionNotFoundException;

    /**
     * Find the definition of a dimension defined on a particular dataset based on a dimensional dataset edition and version.
     * The particular options for the dimension will be filtered to only contain those that actually occur in the dataset.
     *
     * @param edition the major_label of dimensional_dataset.
     * @param version the version of a dimensional_dataset.
     * @param dimensionId the id of the dimension to query.
     * @param viewType the type of view to use for the dimension options.
     * @return the given dimension definition for the given dataset.
     * @throws DimensionNotFoundException if the dimension does not exist in this dataset.
     * @throws DataSetNotFoundException if the dataset does not exist.
     */
    DimensionMetadata findDimensionByIdWithEditionVersion(String datasetId, String edition, int version, String dimensionId, DimensionViewType viewType) throws DataSetNotFoundException,
            DimensionNotFoundException;

    /**
     * Lists all hierarchies defined in the database as pseudo-dimensions.
     *
     * @return the list of all hierarchies.
     */
    List<DimensionMetadata> listHierarchies();

    /**
     * Gets the full data for a hierarchy as a psuedo-dimension. The entries in the hierarchy will be included
     * as the options in the dimension.
     *
     * @param hierarchyId the id of the hierarchy.
     * @return the hierarchy as a dimension.
     * @throws DimensionNotFoundException if the hierarchy does not exist.
     */
    DimensionMetadata getHierarchy(String hierarchyId) throws DimensionNotFoundException;

}
