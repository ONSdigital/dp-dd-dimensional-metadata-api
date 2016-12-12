package uk.co.onsdigital.discovery.metadata.api.dao;

import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;
import uk.co.onsdigital.discovery.model.Variable;

import java.util.List;

/**
 * Created by neil on 12/12/2016.
 */
public interface MetadataDao {
    List<DimensionalDataSet> findAllDataSets();
    DimensionalDataSet findDataSetById(String dataSetId) throws DataSetNotFoundException;
    List<Variable> getVariablesInDataSet(String dataSetId);
    Variable findVariableByDataSetAndDimensionId(String dataSetId, String dimensionId) throws DataSetNotFoundException, DimensionNotFoundException;
}
