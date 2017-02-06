package uk.co.onsdigital.discovery.metadata.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.onsdigital.discovery.metadata.api.dao.MetadataDao;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.model.DataSet;
import uk.co.onsdigital.discovery.metadata.api.model.Dimension;
import uk.co.onsdigital.discovery.metadata.api.model.DimensionOption;
import uk.co.onsdigital.discovery.metadata.api.model.ResultPage;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;

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
    private final UrlBuilder urlBuilder;

    MetadataServiceImpl(MetadataDao metadataDao, UrlBuilder urlBuilder) {
        logger.info("Initialising metadata service. Base URL: {}", urlBuilder);

        this.metadataDao = metadataDao;
        this.urlBuilder = urlBuilder;
    }

    @Transactional(readOnly = true)
    public ResultPage<DataSet> listAvailableDataSets(int pageNumber, int pageSize) {
        final long totalDataSets = metadataDao.countDataSets();
        final List<DimensionalDataSet> dbDataSets = metadataDao.findDataSetsPage(pageNumber, pageSize);
        final List<DataSet> resultDataSets = new ArrayList<>(dbDataSets.size());

        for (DimensionalDataSet dbDataSet : dbDataSets) {
            resultDataSets.add(convertDataSet(dbDataSet, false));
        }

        return new ResultPage<>(urlBuilder.datasetsPage(pageSize), resultDataSets, totalDataSets, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    public DataSet findDataSetById(String dataSetId) throws DataSetNotFoundException {
        return convertDataSet(metadataDao.findDataSetById(dataSetId), true);
    }

    @Transactional(readOnly = true)
    public List<Dimension> listDimensionsForDataSet(String dataSetId) throws DataSetNotFoundException {
        return metadataDao.findDimensionsForDataSet(dataSetId).stream()
                .map(d -> populateOptions(DimensionViewType.LIST, d))
                .map(d -> addUrl(dataSetId, d))
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public Dimension findDimensionById(String dataSetId, String dimensionId, DimensionViewType viewType) throws DataSetNotFoundException, DimensionNotFoundException {
        return populateOptions(viewType, addUrl(dataSetId, findByName(metadataDao.findDimensionsForDataSet(dataSetId), dimensionId)));
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
        dataSet.setUrl(urlBuilder.dataset(dataSet.getId()));
        dataSet.setDimensionsUrl(urlBuilder.dimensions(dataSet.getId()));

        if (includeDimensions) {
            final List<Dimension> dimensions = metadataDao.findDimensionsForDataSet(dataSet.getId()).stream()
                    .map(d -> addUrl(dataSet.getId(), d))
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

    private Dimension addUrl(String dataSetId, Dimension dimension) {
        if (dimension != null) {
            dimension.setUrl(urlBuilder.dimension(dataSetId, dimension.getName()));
        }
        return dimension;
    }

    private static Dimension populateOptions(DimensionViewType viewType, Dimension dimension) {
        if (dimension == null || dimension.getValues() == null) {
            return dimension;
        }

        final List<DimensionOption> options = viewType.convertValues(dimension.getValues());
        dimension.setOptions(options);
        return dimension;
    }

}
