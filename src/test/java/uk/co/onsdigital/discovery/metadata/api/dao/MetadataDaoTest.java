package uk.co.onsdigital.discovery.metadata.api.dao;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.model.DataResource;
import uk.co.onsdigital.discovery.model.DataSet;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class MetadataDaoTest {

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private TypedQuery<DataSet> mockDimensionalDataSetQuery;

    @Mock
    private TypedQuery<DataResource> mockDataResourceQuery;

    private MetadataDao metadataDao;

    @BeforeMethod
    public void createDao() {
        MockitoAnnotations.initMocks(this);
        metadataDao = new MetadataDaoImpl(mockEntityManager);
    }

    @Test
    public void shouldReturnAllDataSetsFromDatabase() throws Exception {
        List<DataSet> dataSets = asList(new DataSet(), new DataSet());

        when(mockEntityManager.createNamedQuery("DataSet.findAll", DataSet.class)).thenReturn(mockDimensionalDataSetQuery);
        when(mockDimensionalDataSetQuery.setFirstResult(0)).thenReturn(mockDimensionalDataSetQuery);
        when(mockDimensionalDataSetQuery.setMaxResults(10)).thenReturn(mockDimensionalDataSetQuery);
        when(mockDimensionalDataSetQuery.getResultList()).thenReturn(dataSets);

        List<DataSet> result = metadataDao.findLegacyDataSetsPage(1, 10);
        assertThat(result).isEqualTo(dataSets);
    }

    @Test
    public void shouldReturnAllDataResourcesFromDatabase() throws Exception {
        List<DataResource> dataResources = asList(new DataResource());

        when(mockEntityManager.createNamedQuery("DataResource.findAll", DataResource.class)).thenReturn(mockDataResourceQuery);
        when(mockDataResourceQuery.setFirstResult(0)).thenReturn(mockDataResourceQuery);
        when(mockDataResourceQuery.setMaxResults(10)).thenReturn(mockDataResourceQuery);
        when(mockDataResourceQuery.getResultList()).thenReturn(dataResources);

        List<DataResource> result = metadataDao.findDataResourcesPage(1, 10);
        assertThat(result).isEqualTo(dataResources);
    }


    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailIfDataSetNotFound() throws Exception {
        metadataDao.findDataSetByUuid(UUID.randomUUID().toString());
    }

    @Test
    public void shouldReturnMatchingDataSet() throws Exception {
        final DataSet dataSet = new DataSet();
        final UUID dataSetId = UUID.randomUUID();

        when(mockEntityManager.find(DataSet.class, dataSetId)).thenReturn(dataSet);

        final DataSet result = metadataDao.findDataSetByUuid(dataSetId.toString());
        assertThat(result).isEqualTo(dataSet);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldRejectInvalidDataSetIds() throws Exception {
        metadataDao.findDataSetByUuid("not a uuid");
    }
}