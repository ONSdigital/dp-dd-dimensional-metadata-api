package uk.co.onsdigital.discovery.metadata.api.service;

import uk.co.onsdigital.discovery.metadata.api.dao.MetadataDao;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.model.DataSet;
import uk.co.onsdigital.discovery.metadata.api.model.Dimension;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by neil on 12/12/2016.
 */
public class MetadataServiceImpl implements MetadataService {
    private static final String DATASET_TEMPLATE = "%s/datasets/%s";

    private final MetadataDao metadataDao;
    private final String baseUrl;

    public MetadataServiceImpl(MetadataDao metadataDao, String baseUrl) {
        this.metadataDao = metadataDao;
        this.baseUrl = baseUrl;
    }

    public Set<DataSet> listAvailableDataSets() {
        final List<DimensionalDataSet> dbDataSets = metadataDao.findAllDataSets();
        final Set<DataSet> resultDataSets = new HashSet<>(dbDataSets.size());

        for (DimensionalDataSet dbDataSet : dbDataSets) {
            resultDataSets.add(convertDataSet(dbDataSet));
        }

        return resultDataSets;
    }

    public DataSet findDataSetById(String dataSetId) throws DataSetNotFoundException {
        return convertDataSet(metadataDao.findDataSetById(dataSetId));
    }

    public Set<Dimension> listDimensionsForDataSet(String dataSetId) {
        return null;
    }

    public Dimension findDimensionById(String dataSetId, String dimensionId) {
        return null;
    }

    private DataSet convertDataSet(final DimensionalDataSet dbDataSet) {
        final DataSet dataSet = new DataSet();
        dataSet.setId(dbDataSet.getDimensionalDataSetId().toString());
        dataSet.setTitle(dbDataSet.getTitle());
        dataSet.setDescription(dbDataSet.getDescription());

        dataSet.setUrl(String.format(Locale.ROOT, DATASET_TEMPLATE, baseUrl, dbDataSet.getDimensionalDataSetId().toString()));
        return dataSet;
    }
}
