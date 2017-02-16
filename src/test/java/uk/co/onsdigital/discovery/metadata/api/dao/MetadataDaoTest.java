package uk.co.onsdigital.discovery.metadata.api.dao;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;

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
    private TypedQuery<DimensionalDataSet> mockQuery;

    private MetadataDao metadataDao;

    @BeforeMethod
    public void createDao() {
        MockitoAnnotations.initMocks(this);
        metadataDao = new MetadataDaoImpl(mockEntityManager);
    }

    @Test
    public void shouldReturnAllDataSetsFromDatabase() throws Exception {
        List<DimensionalDataSet> dataSets = asList(new DimensionalDataSet(), new DimensionalDataSet());

        when(mockEntityManager.createNamedQuery("DimensionalDataSet.findAll", DimensionalDataSet.class)).thenReturn(mockQuery);
        when(mockQuery.setFirstResult(0)).thenReturn(mockQuery);
        when(mockQuery.setMaxResults(10)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(dataSets);

        List<DimensionalDataSet> result = metadataDao.findLegacyDataSetsPage(1, 10);
        assertThat(result).isEqualTo(dataSets);
    }

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailIfDataSetNotFound() throws Exception {
        metadataDao.findDataSetByUuid(UUID.randomUUID().toString());
    }

    @Test
    public void shouldReturnMatchingDataSet() throws Exception {
        final DimensionalDataSet dataSet = new DimensionalDataSet();
        final UUID dataSetId = UUID.randomUUID();

        when(mockEntityManager.find(DimensionalDataSet.class, dataSetId)).thenReturn(dataSet);

        final DimensionalDataSet result = metadataDao.findDataSetByUuid(dataSetId.toString());
        assertThat(result).isEqualTo(dataSet);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldRejectInvalidDataSetIds() throws Exception {
        metadataDao.findDataSetByUuid("not a uuid");
    }
}