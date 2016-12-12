package uk.co.onsdigital.discovery.metadata.api.service;

import uk.co.onsdigital.discovery.metadata.api.model.DataSet;
import uk.co.onsdigital.discovery.metadata.api.model.Dimension;

import java.util.Set;

/**
 * Created by neil on 12/12/2016.
 */
public interface MetadataService {

    Set<DataSet> listAvailableDataSets();

    DataSet findDataSetById(String dataSetId);

    Set<Dimension> listDimensionsForDataSet(String dataSetId);

    Dimension findDimensionById(String dataSetId, String dimensionId);

}
