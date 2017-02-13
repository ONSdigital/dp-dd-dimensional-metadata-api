package uk.co.onsdigital.discovery.metadata.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uk.co.onsdigital.discovery.metadata.api.dao.MetadataDao;
import uk.co.onsdigital.discovery.metadata.api.legacy.dto.DataSet;
import uk.co.onsdigital.discovery.metadata.api.legacy.dto.DimensionMetadata;
import uk.co.onsdigital.discovery.metadata.api.legacy.dto.DimensionOption;
import uk.co.onsdigital.discovery.metadata.api.legacy.dto.ResultPage;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;
import uk.co.onsdigital.discovery.model.Dimension;
import uk.co.onsdigital.discovery.model.DimensionValue;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;
import uk.co.onsdigital.discovery.model.Hierarchy;
import uk.co.onsdigital.discovery.model.HierarchyEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Implementation of the {@link MetadataService}.
 */
@Service
public class MetadataServiceImpl implements MetadataService {
    private static final Logger logger = LoggerFactory.getLogger(MetadataServiceImpl.class);

    private final MetadataDao metadataDao;
    private final LegacyUrlBuilder legacyUrlBuilder;

    MetadataServiceImpl(MetadataDao metadataDao, LegacyUrlBuilder legacyUrlBuilder) {
        logger.info("Initialising metadata service. Base URL: {}", legacyUrlBuilder);

        this.metadataDao = metadataDao;
        this.legacyUrlBuilder = legacyUrlBuilder;
    }

    @Transactional(readOnly = true)
    public ResultPage<DataSet> listAvailableDataSets(int pageNumber, int pageSize) {
        final long totalDataSets = metadataDao.countDataSets();
        final List<DimensionalDataSet> dbDataSets = metadataDao.findDataSetsPage(pageNumber, pageSize);
        final List<DataSet> resultDataSets = new ArrayList<>(dbDataSets.size());

        for (DimensionalDataSet dbDataSet : dbDataSets) {
            resultDataSets.add(convertDataSet(dbDataSet, false));
        }

        return new ResultPage<>(legacyUrlBuilder.datasetsPage(pageSize), resultDataSets, totalDataSets, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    public DataSet findDataSetById(String dataSetId) throws DataSetNotFoundException {
        return convertDataSet(metadataDao.findDataSetById(dataSetId), true);
    }

    @Transactional(readOnly = true)
    public List<DimensionMetadata> listDimensionsForDataSet(String dataSetId) throws DataSetNotFoundException {
        return metadataDao.findDimensionsForDataSet(dataSetId).stream()
                .map(d -> convertDimension(dataSetId, d, DimensionViewType.LIST))
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public DimensionMetadata findDimensionById(String dataSetId, String dimensionId, DimensionViewType viewType) throws DataSetNotFoundException, DimensionNotFoundException {
        return convertDimension(dataSetId, findByName(metadataDao.findDimensionsForDataSet(dataSetId), dimensionId), viewType);
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
     * Converts a dataset from the database into an API {@link DataSet} object.
     *
     * @param dbDataSet the dataset loaded from the database.
     * @param includeDimensions whether to include the dimensions in the output.
     * @return the {@link DataSet} model populated with the metadata about the dataset.
     */
    private DataSet convertDataSet(final DimensionalDataSet dbDataSet, final boolean includeDimensions) {
        final DataSet dataSet = new DataSet();
        dataSet.setId(dbDataSet.getId().toString());
        dataSet.setTitle(dbDataSet.getTitle());
        dataSet.setS3URL(dbDataSet.getS3URL());
        dataSet.setMetadata(dbDataSet.getMetadata());
        dataSet.setUrl(legacyUrlBuilder.dataset(dataSet.getId()));
        dataSet.setDimensionsUrl(legacyUrlBuilder.dimensions(dataSet.getId()));

        if (includeDimensions) {
            final List<DimensionMetadata> dimensions = metadataDao.findDimensionsForDataSet(dataSet.getId()).stream()
                    .map(d -> convertDimension(dataSet.getId(), d, DimensionViewType.NONE))
                    .collect(toList());
            dataSet.setDimensions(dimensions);
        }

        return dataSet;
    }

    private static Dimension findByName(Collection<Dimension> dimensions, String name) throws DimensionNotFoundException {
        if (dimensions == null) {
            throw new DimensionNotFoundException(name);
        }
        return dimensions.stream().filter(d -> name.equals(d.getName())).findAny().orElseThrow(() -> new DimensionNotFoundException(name));
    }

    private DimensionMetadata convertDimension(String dataSetId, Dimension dimension, DimensionViewType viewType) {
        Hierarchy hierarchy = dimension.getHierarchy();
        DimensionMetadata result = new DimensionMetadata();

        result.setId(dimension.getName());
        result.setName(dimension.getName());
        result.setUrl(legacyUrlBuilder.dimension(dataSetId, dimension.getName()));
        result.setHierarchical(hierarchy != null);
        result.setType(hierarchy == null ? "standard" : hierarchy.getType());
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
