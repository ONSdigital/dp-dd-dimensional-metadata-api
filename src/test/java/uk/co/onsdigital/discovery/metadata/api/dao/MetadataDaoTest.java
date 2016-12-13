package uk.co.onsdigital.discovery.metadata.api.dao;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.VariableNotFoundException;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;
import uk.co.onsdigital.discovery.model.Variable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MetadataDaoTest {

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private TypedQuery<DimensionalDataSet> mockQuery;

    private MetadataDao metadataDao;

    @BeforeMethod
    public void createDao() {
        MockitoAnnotations.initMocks(this);
        metadataDao = new MetadataDaoImpl(mockEntityManager);
    }

    @Test
    public void shouldReturnAllDataSetsFromDatabase() throws Exception {
        List<DimensionalDataSet> dataSets = Arrays.asList(new DimensionalDataSet(), new DimensionalDataSet());

        when(mockEntityManager.createNamedQuery("DimensionalDataSet.findAll", DimensionalDataSet.class)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(dataSets);

        List<DimensionalDataSet> result = metadataDao.findAllDataSets();
        assertThat(result).isEqualTo(dataSets);
    }

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailIfDataSetNotFound() throws Exception {
        metadataDao.findDataSetById(UUID.randomUUID().toString());
    }

    @Test
    public void shouldReturnMatchingDataSet() throws Exception {
        final DimensionalDataSet dataSet = new DimensionalDataSet();
        final UUID dataSetId = UUID.randomUUID();

        when(mockEntityManager.find(DimensionalDataSet.class, dataSetId)).thenReturn(dataSet);

        final DimensionalDataSet result = metadataDao.findDataSetById(dataSetId.toString());
        assertThat(result).isEqualTo(dataSet);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldRejectInvalidDataSetIds() throws Exception {
        metadataDao.findDataSetById("not a uuid");
    }

    @Test
    public void shouldReturnAllReferencedVariables() throws Exception {
        final DimensionalDataSet dataSet = mock(DimensionalDataSet.class);
        final UUID dataSetId = UUID.randomUUID();
        final List<Variable> referencedVariables = Arrays.asList(new Variable(), new Variable());

        when(mockEntityManager.find(DimensionalDataSet.class, dataSetId)).thenReturn(dataSet);
        when(dataSet.getReferencedVariables()).thenReturn(referencedVariables);

        final List<Variable> result = metadataDao.findVariablesInDataSet(dataSetId.toString());

        assertThat(result).isEqualTo(referencedVariables);
    }

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailIfDataSetNotFoundWhenFindingVariables() throws Exception {
        metadataDao.findVariablesInDataSet(UUID.randomUUID().toString());
    }

    @Test
    public void shouldReturnEmptyListIfNoVariablesInDataSet() throws Exception {
        final DimensionalDataSet dataSet = mock(DimensionalDataSet.class);
        final UUID dataSetId = UUID.randomUUID();

        when(mockEntityManager.find(DimensionalDataSet.class, dataSetId)).thenReturn(dataSet);
        when(dataSet.getReferencedVariables()).thenReturn(null);

        final List<Variable> result = metadataDao.findVariablesInDataSet(dataSetId.toString());
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnMatchingVariable() throws Exception {
        final DimensionalDataSet dataSet = mock(DimensionalDataSet.class);
        final UUID dataSetId = UUID.randomUUID();
        final Variable variable = new Variable();
        final long variableId = 42L;
        variable.setVariableId(variableId);
        final List<Variable> referencedVariables = Collections.singletonList(variable);

        when(mockEntityManager.find(DimensionalDataSet.class, dataSetId)).thenReturn(dataSet);
        when(dataSet.getReferencedVariables()).thenReturn(referencedVariables);

        Variable result = metadataDao.findVariableByDataSetAndVariableId(dataSetId.toString(), Long.toString(variableId));
        assertThat(result).isEqualTo(variable);
    }

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailIfDataSetNotFoundForVariable() throws Exception {
        metadataDao.findVariableByDataSetAndVariableId(UUID.randomUUID().toString(), "42");
    }

    @Test(expectedExceptions = VariableNotFoundException.class)
    public void shouldFailIfVariableNotFound() throws Exception {
        final UUID dataSetId = UUID.randomUUID();
        final DimensionalDataSet dataSet = mock(DimensionalDataSet.class);
        when(mockEntityManager.find(DimensionalDataSet.class, dataSetId)).thenReturn(dataSet);
        when(dataSet.getReferencedVariables()).thenReturn(Arrays.asList(new Variable(), new Variable()));

        metadataDao.findVariableByDataSetAndVariableId(dataSetId.toString(), "42");
    }
}