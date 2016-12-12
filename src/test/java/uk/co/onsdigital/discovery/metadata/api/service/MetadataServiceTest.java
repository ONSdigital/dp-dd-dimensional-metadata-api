package uk.co.onsdigital.discovery.metadata.api.service;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.co.onsdigital.discovery.metadata.api.dao.MetadataDao;
import uk.co.onsdigital.discovery.metadata.api.model.DataSet;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class MetadataServiceTest {

    private static final String BASE_URL = "http://example.org/dd-test-api";

    @Mock
    private MetadataDao mockDao;

    private MetadataService metadataService;

    @BeforeMethod
    public void createMetadataService() {
        MockitoAnnotations.initMocks(this);
        metadataService = new MetadataServiceImpl(mockDao, BASE_URL);
    }

    @Test
    public void shouldReturnAllDataSetsDefinedInDatabase() throws Exception {
        List<DimensionalDataSet> dbDataSets = Arrays.asList(
                dbDataSet(UUID.randomUUID(), "test 1 title", "test 1 description"),
                dbDataSet(UUID.randomUUID(), "test 2 title", "test 2 description"));
        when(mockDao.findAllDataSets()).thenReturn(dbDataSets);

        Set<DataSet> result = metadataService.listAvailableDataSets();

        assertThat(result).isNotNull()
                .hasSize(dbDataSets.size());
    }

    @Test
    public void shouldConstructCorrectURLs() throws Exception {
        String uuid = "ac31776f-17a8-4e68-a673-e19589b23496";
        when(mockDao.findAllDataSets()).thenReturn(Collections.singletonList(dbDataSet(UUID.fromString(uuid), "", "")));

        Set<DataSet> result = metadataService.listAvailableDataSets();

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getUrl()).isEqualTo(BASE_URL + "/datasets/" + uuid);
    }

    @Test
    public void shouldMapDataSetAttributes() throws Exception {
        UUID dataSetId = UUID.randomUUID();
        String title = "test title";
        String description = "test description";
        DimensionalDataSet dbDataSet = dbDataSet(dataSetId, title, description);
        when(mockDao.findAllDataSets()).thenReturn(Collections.singletonList(dbDataSet));

        Set<DataSet> result = metadataService.listAvailableDataSets();

        assertThat(result).hasSize(1);
        DataSet dataSet = result.iterator().next();
        assertDataSetEqualsDbModel(dataSet, dbDataSet);
    }

    @Test
    public void shouldMapDataSetAttributesForSingleDataSet() throws Exception {
        UUID dataSetId = UUID.randomUUID();
        String title = "test title";
        String description = "test description";
        DimensionalDataSet dbDataSet = dbDataSet(dataSetId, title, description);
        when(mockDao.findDataSetById(dataSetId.toString())).thenReturn(dbDataSet);

        DataSet result = metadataService.findDataSetById(dataSetId.toString());

        assertDataSetEqualsDbModel(result, dbDataSet);
    }

    private static void assertDataSetEqualsDbModel(final DataSet actual, final DimensionalDataSet expected) {
        assertThat(actual.getId()).isEqualTo(expected.getDimensionalDataSetId().toString());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getMetadata()).isNotNull();
        assertThat(actual.getMetadata().getDescription()).isEqualTo(expected.getDescription());
    }

    private DimensionalDataSet dbDataSet(UUID dataSetId, String title, String description) {
        DimensionalDataSet dataSet = new DimensionalDataSet();
        dataSet.setDimensionalDataSetId(dataSetId);
        dataSet.setTitle(title);
        dataSet.setDescription(description);
        return dataSet;
    }
}