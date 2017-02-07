package uk.co.onsdigital.discovery.metadata.api.service;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.co.onsdigital.discovery.metadata.api.dao.MetadataDao;
import uk.co.onsdigital.discovery.metadata.api.dto.DataSet;
import uk.co.onsdigital.discovery.metadata.api.dto.DimensionMetadata;
import uk.co.onsdigital.discovery.metadata.api.dto.DimensionOption;
import uk.co.onsdigital.discovery.metadata.api.dto.ResultPage;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.model.Dimension;
import uk.co.onsdigital.discovery.model.DimensionValue;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;
import uk.co.onsdigital.discovery.model.Hierarchy;
import uk.co.onsdigital.discovery.model.HierarchyEntry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class MetadataServiceTest {

    private static final String BASE_URL = "http://example.org/dd-test-api";
    private static final String DATASET_ID = "ac31776f-17a8-4e68-a673-e19589b23496";

    @Mock
    private MetadataDao mockDao;

    private MetadataService metadataService;

    @BeforeMethod
    public void createMetadataService() {
        MockitoAnnotations.initMocks(this);
        metadataService = new MetadataServiceImpl(mockDao, new UrlBuilder(BASE_URL));
    }

    @Test
    public void shouldReturnAllDataSetsDefinedInDatabase() throws Exception {
        final int pageNumber = 3;
        final int resultPerPage = 10;
        List<DimensionalDataSet> dbDataSets = Arrays.asList(
                dbDataSet(UUID.randomUUID(), "test 1 title", "test 1 description"),
                dbDataSet(UUID.randomUUID(), "test 2 title", "test 2 description"));
        long total = 342L;
        when(mockDao.findDataSetsPage(pageNumber, resultPerPage)).thenReturn(dbDataSets);
        when(mockDao.countDataSets()).thenReturn(total);

        ResultPage<DataSet> result = metadataService.listAvailableDataSets(pageNumber, resultPerPage);

        assertThat(result).isNotNull()
                .hasFieldOrPropertyWithValue("total", total)
                .hasFieldOrPropertyWithValue("count", dbDataSets.size())
                .hasFieldOrPropertyWithValue("page", pageNumber)
                .hasFieldOrPropertyWithValue("itemsPerPage", resultPerPage)
                .hasFieldOrPropertyWithValue("startIndex", 20L);
        assertThat(result.getItems()).isNotNull().hasSize(dbDataSets.size());
    }

    @Test
    public void shouldConstructCorrectURLs() throws Exception {
        when(mockDao.findDataSetsPage(1, 5)).thenReturn(Collections.singletonList(dbDataSet(UUID.fromString(DATASET_ID), "", "")));

        List<DataSet> result = metadataService.listAvailableDataSets(1, 5).getItems();

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getUrl()).isEqualTo(BASE_URL + "/datasets/" + DATASET_ID);
    }

    @Test
    public void shouldMapDataSetAttributes() throws Exception {
        UUID dataSetId = UUID.randomUUID();
        String title = "test title";
        String description = "test description";
        DimensionalDataSet dbDataSet = dbDataSet(dataSetId, title, description);
        when(mockDao.findDataSetsPage(1, 10)).thenReturn(Collections.singletonList(dbDataSet));

        List<DataSet> result = metadataService.listAvailableDataSets(1, 10).getItems();

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

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailIfDataSetNotFound() throws Exception {
        when(mockDao.findDataSetById(DATASET_ID)).thenThrow(new DataSetNotFoundException("test"));

        metadataService.findDataSetById(DATASET_ID);
    }

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailIfDataSetNotFoundForDimension() throws Exception {
        when(mockDao.findDimensionsForDataSet(DATASET_ID)).thenThrow(new DataSetNotFoundException(""));

        metadataService.findDimensionById(DATASET_ID, "any", DimensionViewType.LIST);
    }

    @Test(expectedExceptions = DimensionNotFoundException.class)
    public void shouldFailIfDimensionNotFound() throws Exception {
        String dimensionId = "testDimension";
        when(mockDao.findDimensionsForDataSet(DATASET_ID)).thenReturn(Collections.emptyList());

        metadataService.findDimensionById(DATASET_ID, dimensionId, DimensionViewType.LIST);
    }

    @Test
    public void shouldMapDimensionsCorrectly() throws Exception {
        DimensionalDataSet dataSet = new DimensionalDataSet();
        dataSet.setId(UUID.randomUUID());
        Dimension dim1 = new Dimension(dataSet, "dim1", new DimensionValue(dataSet.getId(), "dim1", "val1"),
                                                        new DimensionValue(dataSet.getId(), "dim1", "val2"));
        Dimension dim2 = new Dimension(dataSet, "dim2", new DimensionValue(dataSet.getId(), "dim2", "val3"));
        HierarchyEntry entry = new HierarchyEntry();
        entry.setName("hierarchical name");
        entry.setCode("ABC0123F");
        Hierarchy hierarchy = new Hierarchy();
        hierarchy.setType("test");
        dim2.setHierarchy(hierarchy);
        entry.setHierarchy(hierarchy);
        dim2.getValues().get(0).setHierarchyEntry(entry);

        when(mockDao.findDimensionsForDataSet(DATASET_ID)).thenReturn(Arrays.asList(dim1, dim2));

        List<DimensionMetadata> result = metadataService.listDimensionsForDataSet(DATASET_ID);
        assertThat(result).hasSize(2);
        DimensionMetadata dimension1 = result.get(0);
        DimensionMetadata dimension2 = result.get(1);

        // Check that dimension options are properly created
        assertThat(dimension1.getName()).isEqualTo("dim1");
        assertThat(dimension1.getType()).isEqualTo("standard");
        assertThat(dimension1.getUrl()).isEqualTo(BASE_URL + "/datasets/" + DATASET_ID + "/dimensions/dim1");
        assertThat(dimension1.isHierarchical()).isFalse();
        assertThat(dimension1.getOptions()).containsOnly(new DimensionOption(null, "val1"), new DimensionOption(null, "val2"));

        assertThat(dimension2.getName()).isEqualTo("dim2");
        assertThat(dimension2.getType()).isEqualTo("test");
        assertThat(dimension2.getUrl()).isEqualTo(BASE_URL + "/datasets/" + DATASET_ID + "/dimensions/dim2");
        assertThat(dimension2.isHierarchical()).isTrue();
        assertThat(dimension2.getOptions()).containsOnly(new DimensionOption(null, entry.getCode(), entry.getName()));
    }

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailToListDimensionsIfDataSetNotFound() throws Exception {
        when(mockDao.findDimensionsForDataSet(DATASET_ID)).thenThrow(new DataSetNotFoundException(""));

        metadataService.listDimensionsForDataSet(DATASET_ID);
    }

    private static void assertDataSetEqualsDbModel(final DataSet actual, final DimensionalDataSet expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId().toString());
        assertThat(actual.getS3URL()).isEqualTo(expected.getS3URL());
    }

    private DimensionalDataSet dbDataSet(UUID dataSetId, String s3URL, String description) {
        DimensionalDataSet dataSet = new DimensionalDataSet();
        dataSet.setId(dataSetId);
        dataSet.setS3URL(s3URL);
        return dataSet;
    }
}