package uk.co.onsdigital.discovery.metadata.api.service;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uk.co.onsdigital.discovery.metadata.api.dao.MetadataDao;
import uk.co.onsdigital.discovery.metadata.api.dto.*;
import uk.co.onsdigital.discovery.metadata.api.dto.common.DimensionMetadata;
import uk.co.onsdigital.discovery.metadata.api.dto.common.DimensionOption;
import uk.co.onsdigital.discovery.metadata.api.dto.legacy.DataSet;
import uk.co.onsdigital.discovery.metadata.api.dto.legacy.LegacyResultPage;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;
import uk.co.onsdigital.discovery.model.*;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

/**
 * Implementation of the {@link MetadataService}.
 */
@Service
public class MetadataServiceImpl implements MetadataService {
    private static final Logger logger = LoggerFactory.getLogger(MetadataServiceImpl.class);

    private final MetadataDao metadataDao;
    private final LegacyUrlBuilder legacyUrlBuilder;
    private final UrlBuilder urlBuilder;

    MetadataServiceImpl(MetadataDao metadataDao, UrlBuilder urlBuilder, LegacyUrlBuilder legacyUrlBuilder) {
        logger.info("Initialising metadata service. Base URL: {}", legacyUrlBuilder);

        this.metadataDao = metadataDao;
        this.legacyUrlBuilder = legacyUrlBuilder;
        this.urlBuilder = urlBuilder;
    }

    @Transactional(readOnly = true)
    public ResultPage<DataResourceResult> listAvailableDataResources(int pageNumber, int pageSize) {
        final long totalDataSets = metadataDao.countDataResources();
        final List<DataResource> dbDataSets = metadataDao.findDataResourcesPage(pageNumber, pageSize);
        final List<DataResourceResult> resultDataSets = new ArrayList<>(dbDataSets.size());

        for(DataResource dataResource: dbDataSets) {
            resultDataSets.add(convertDataResource(dataResource));
        }

        return new ResultPage<>(urlBuilder.datasetsPage(pageSize), resultDataSets, totalDataSets, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    public LegacyResultPage<DataSet> listAvailableVersions(int pageNumber, int pageSize) {
        final long totalDataSets = metadataDao.countDataSets();
        final List<DimensionalDataSet> dbDataSets = metadataDao.findLegacyDataSetsPage(pageNumber, pageSize);
        final List<DataSet> resultDataSets = new ArrayList<>(dbDataSets.size());

        for (DimensionalDataSet dbDataSet : dbDataSets) {
            resultDataSets.add(legacyConvertDataSet(dbDataSet, false));
        }

        return new LegacyResultPage<>(legacyUrlBuilder.datasetsPage(pageSize), resultDataSets, totalDataSets, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    public DataSet findDataSetByUuid(String dataSetUuid) throws DataSetNotFoundException {
        return legacyConvertDataSet(metadataDao.findDataSetByUuid(dataSetUuid), true);
    }

    public DataResourceResult findDataResource(String dataResourceId) throws DataSetNotFoundException {
        return convertDataResource(metadataDao.findDataResource(dataResourceId));
    }

    @Override
    @Transactional(readOnly = true)
    public DataSet findDataSetByEditionAndVersion(String dataResourceId, String edition, int version) throws DataSetNotFoundException {
        return convertDataSet(metadataDao.findDataSetByEditionAndVersion(dataResourceId, edition, version), true);
    }

    @Transactional(readOnly = true)
    public List<DimensionMetadata> listDimensionsForDataSetUuid(String datasetUuid) throws DataSetNotFoundException {
        return metadataDao.findDimensionsForDataSet(datasetUuid).stream()
                .map(d -> legacyConvertDimension(datasetUuid, d, DimensionViewType.LIST))
                .collect(toList());
    }

    @Override
    public List<DimensionMetadata> listDimensionsForDataSetEditionVersion(String dataResourceId, String edition, int version) throws DataSetNotFoundException {
        return metadataDao.findDimensionsForDataSet(dataResourceId, edition, version).stream()
                .map(d -> convertDimension(dataResourceId, edition, version, d, DimensionViewType.LIST))
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public DimensionMetadata findDimensionByIdWithDatasetUuid(String dataSetUuid, String dimensionId, DimensionViewType viewType) throws DataSetNotFoundException, DimensionNotFoundException {
        return legacyConvertDimension(dataSetUuid, findByName(metadataDao.findDimensionsForDataSet(dataSetUuid), dimensionId), viewType);
    }

    @Override
    public DimensionMetadata findDimensionByIdWithEditionVersion(String dataSetId, String edition, int version, String dimensionId, DimensionViewType viewType) throws DataSetNotFoundException, DimensionNotFoundException {
        return convertDimension(dataSetId, edition, version, findByName(metadataDao.findDimensionsForDataSet(dataSetId, edition, version), dimensionId), viewType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DimensionMetadata> listHierarchies() {
        return metadataDao.listAllHierarchies().stream().map(this::convertHierarchyToDimension).collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DimensionMetadata getHierarchy(String hierarchyId) throws DimensionNotFoundException {
        final List<HierarchyEntry> entries = metadataDao.findAllEntriesInHierarchy(hierarchyId);
        if (CollectionUtils.isEmpty(entries)) {
            throw new DimensionNotFoundException("No such hierarchy: " + hierarchyId);
        }

        final DimensionMetadata dimension = convertHierarchyToDimension(entries.get(0).getHierarchy());
        final List<DimensionOption> options = convertHierarchyEntries(entries);
        dimension.setOptions(options);
        return dimension;
    }

    /**
     * Converts a dataset from the database into an API {@link DataSet} object. This is the legacy display.
     *
     * @param dbDataSet the dataset loaded from the database.
     * @param includeDimensions whether to include the dimensions in the output.
     * @return the {@link DataSet} model populated with the metadata about the dataset.
     */
    private DataSet legacyConvertDataSet(final DimensionalDataSet dbDataSet, final boolean includeDimensions) {
        final DataSet dataSet = new DataSet();
        dataSet.setId(dbDataSet.getId().toString());
        dataSet.setTitle(dbDataSet.getTitle());
        dataSet.setS3URL(dbDataSet.getS3URL());
        dataSet.setMetadata(StringUtils.defaultIfEmpty(dbDataSet.getMetadata(), "{}"));
        dataSet.setUrl(legacyUrlBuilder.dataset(dataSet.getId()));
        dataSet.setDimensionsUrl(legacyUrlBuilder.dimensions(dataSet.getId()));

        if (includeDimensions) {
            final List<DimensionMetadata> dimensions = metadataDao.findDimensionsForDataSet(dataSet.getId()).stream()
                    .map(d -> legacyConvertDimension(dataSet.getId(), d, DimensionViewType.NONE))
                    .collect(toList());
            dataSet.setDimensions(dimensions);
        }

        return dataSet;
    }

    /**
     * Converts a dataset from the database into an API {@link DimensionalDataSetResult} object. This is the new version.
     * This includes the dataresource it belongs too and its edition and version.
     *
     * @param dbDataSet the dataset loaded from the database.
     * @param includeDimensions whether to include the dimensions in the output.
     * @return the {@link DimensionalDataSetResult} model populated with the metadata about the dataset.
     */
    private DimensionalDataSetResult convertDataSet(final DimensionalDataSet dbDataSet, final boolean includeDimensions) {
        final DimensionalDataSetResult ddSet = new DimensionalDataSetResult();
        ddSet.setId(dbDataSet.getId().toString());
        ddSet.setDatasetId(dbDataSet.getDataResource().getId());
        ddSet.setTitle(dbDataSet.getTitle());
        ddSet.setS3URL(dbDataSet.getS3URL());
        ddSet.setMetadata(defaultIfEmpty(dbDataSet.getMetadata(), "{}"));
        ddSet.setVersion(Integer.toString(dbDataSet.getMinorVersion()));
        ddSet.setEdition(dbDataSet.getMajorLabel());
        ddSet.setDatasetId(dbDataSet.getDataResource().getId());

        if (includeDimensions) {
            final List<DimensionMetadata> dimensions = metadataDao.findDimensionsForDataSet(ddSet.getId()).stream()
                    .map(d -> convertDimension(dbDataSet.getDataResource().getId(), dbDataSet.getMajorLabel(), dbDataSet.getMinorVersion(), d, DimensionViewType.NONE))
                    .collect(toList());
            ddSet.setDimensions(dimensions);
        }
        ddSet.setUrl(urlBuilder.dimensionalDataSet(ddSet.getDatasetId(), ddSet.getEdition(), dbDataSet.getMinorVersion()));
        ddSet.setDimensionsUrl(urlBuilder.dimensions(ddSet.getDatasetId(), ddSet.getEdition(), ddSet.getVersion()));
        return ddSet;
    }

    /**
     * Converts a dataresource from the database into an API {@link DataResourceResult} object. This includes all the
     * datasets that belong to it under the respective editions and versions of them.
     *
     * @param dataResource the dataresource loaded from the database.
     * @return the {@link DataResourceResult} model populated with the metadata about the dataset.
     */
    private DataResourceResult convertDataResource(final DataResource dataResource) {
        final DataResourceResult drResult = new DataResourceResult();
        drResult.setDatasetId(dataResource.getId());
        drResult.setMetadata(defaultIfEmpty(dataResource.getMetadata(), "{}"));
        drResult.setTitle(dataResource.getTitle());
        final Latest latest = new Latest();
        final List<DimensionalDataSet> dds = dataResource.getDimensionalDataSets();
        if (dds.size() == 0) {
            drResult.setLatest(null);
            drResult.setEditions(null);
        } else {
            final DimensionalDataSet latestDds = dds.get(0); // this will always be the latest as we order by major and minor in the model
            latest.setMetadata(StringUtils.defaultIfEmpty(latestDds.getMetadata(), "{}"));
            latest.setEdition(latestDds.getMajorLabel());
            latest.setVersion(Integer.toString(latestDds.getMinorVersion()));
            latest.setTitle(latestDds.getTitle());
            latest.setUrl(urlBuilder.dimensionalDataSet(drResult.getDatasetId(), latest.getEdition(), latestDds.getMinorVersion()));
            drResult.setLatest(latest);
            drResult.setEditions(extractEditionAndValuesFromDimensionalDataSets(dds));
        }
        return drResult;
    }

    // This is a terrible hack to list all the editions and dimensions.
    // TODO: Once the metadata-editor decides to edit dataresources, it will change the schema and from there we can change
    // this to reflect on new tables. As of now, the edition label is based on the major_label of the latest dataset that
    // has a common major_version between them.
    private List<Edition> extractEditionAndValuesFromDimensionalDataSets(List<DimensionalDataSet> dimensionalDataSets) {
        HashMap<Integer, Edition> editionMap = new LinkedHashMap<>();
        for(DimensionalDataSet dds: dimensionalDataSets) {
          Edition edition = editionMap.get(dds.getMajorVersion());
          if (edition == null) {
              edition = new Edition();
              edition.setId(Integer.toString(dds.getMajorVersion()));
              edition.setLabel(dds.getMajorLabel());
          }
          List<Integer> versions = ObjectUtils.defaultIfNull(edition.getVersions(), new LinkedList<>());
          versions.add(dds.getMinorVersion());
          edition.setVersions(versions);
          editionMap.put(dds.getMajorVersion(), edition);
        }
        return new LinkedList<>(editionMap.values());
    }

    private static Dimension findByName(Collection<Dimension> dimensions, String name) throws DimensionNotFoundException {
        if (dimensions == null) {
            throw new DimensionNotFoundException(name);
        }
        return dimensions.stream().filter(d -> name.equals(d.getName())).findAny().orElseThrow(() -> new DimensionNotFoundException(name));
    }

    private DimensionMetadata legacyConvertDimension(String dataSetId, Dimension dimension, DimensionViewType viewType) {
        return setDimensionWithUrl(dimension, viewType, legacyUrlBuilder.dimension(dataSetId, dimension.getName()));
    }

    private DimensionMetadata convertDimension(String dataResourceId, String edition, int version, Dimension dimension, DimensionViewType viewType) {
        return setDimensionWithUrl(dimension, viewType, urlBuilder.dimension(dataResourceId, edition, version, dimension.getName()));
    }

    private DimensionMetadata setDimensionWithUrl(Dimension dimension, DimensionViewType viewType, String url) {
        DimensionMetadata result = new DimensionMetadata();

        result.setId(dimension.getName());
        result.setName(dimension.getName());
        result.setUrl(url);

        result.setHierarchical(dimension.isHierarchical());
        result.setType(dimension.getType());
        result.setOptions(viewType.convertValues(dimension.getValues()));

        return result;
    }

    private DimensionMetadata convertHierarchyToDimension(Hierarchy hierarchy) {
        final DimensionMetadata dimension = new DimensionMetadata();
        dimension.setId(hierarchy.getId());
        dimension.setName(hierarchy.getName());
        dimension.setType(hierarchy.getType());
        dimension.setHierarchical(true);
        dimension.setUrl(legacyUrlBuilder.hierarchy(hierarchy.getId()));

        return dimension;
    }

    private List<DimensionOption> convertHierarchyEntries(Collection<HierarchyEntry> entries) {
        return DimensionViewType.HIERARCHY.convertValues(entries.stream().map(this::valueFromHierarchyEntry).collect(toList()));
    }

    private DimensionValue valueFromHierarchyEntry(final HierarchyEntry entry) {
        final DimensionValue value = new DimensionValue();
        value.setHierarchyEntry(entry);
        return value;
    }

}
