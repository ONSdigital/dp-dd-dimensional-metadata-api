package uk.co.onsdigital.discovery.metadata.api.dao;

import uk.co.onsdigital.discovery.model.DimensionalDataSet;

import java.util.List;

/**
 * Created by neil on 12/12/2016.
 */
public interface MetadataDao {
    List<DimensionalDataSet> findAllDataSets();
    DimensionalDataSet findDataSetById(String dataSetId);
}
