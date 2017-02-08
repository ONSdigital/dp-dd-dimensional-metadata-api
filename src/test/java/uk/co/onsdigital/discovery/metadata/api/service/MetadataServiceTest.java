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
import uk.co.onsdigital.discovery.model.HierarchyLevelType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.util.CollectionUtils.isEmpty;

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
        when(mockDao.findDataSetsPage(1, 5)).thenReturn(singletonList(dbDataSet(UUID.fromString(DATASET_ID), "", "")));

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
        when(mockDao.findDataSetsPage(1, 10)).thenReturn(singletonList(dbDataSet));

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

    @Test
    public void shouldReturnAllHierarchies() throws Exception {
        Hierarchy h1 = hierarchy("id1", "type1", "name1");
        Hierarchy h2 = hierarchy("id2", "type2", "name2");
        List<Hierarchy> hierarchies = Arrays.asList(h1, h2);
        when(mockDao.listAllHierarchies()).thenReturn(hierarchies);

        List<DimensionMetadata> results = metadataService.listHierarchies();

        assertThat(results).hasSize(hierarchies.size());
        DimensionMetadata m1 = results.get(0);
        DimensionMetadata m2 = results.get(1);

        assertThat(m1).isEqualToComparingOnlyGivenFields(h1, "id", "name", "type");
        assertThat(m2).isEqualToComparingOnlyGivenFields(h2, "id", "name", "type");
        assertThat(m1.getUrl()).isEqualTo(BASE_URL + "/hierarchies/" + h1.getId());
        assertThat(m2.getUrl()).isEqualTo(BASE_URL + "/hierarchies/" + h2.getId());
        assertThat(m1.getOptions()).isNullOrEmpty();
        assertThat(m2.getOptions()).isNullOrEmpty();
    }

    @Test
    public void shouldReturnExistingHierarchyIfExists() throws Exception {
        String hierarchyId = "testHierarchy";
        Hierarchy hierarchy = hierarchy(hierarchyId, "test type", "test name");
        HierarchyEntry entry1 = entry(hierarchy, 0, "code1", "name1");
        HierarchyEntry entry2 = entry(hierarchy, 0, "code2", "name2");
        List<HierarchyEntry> entries = Arrays.asList(entry1, entry2);
        when(mockDao.findAllEntriesInHierarchy(hierarchyId)).thenReturn(entries);

        DimensionMetadata result = metadataService.getHierarchy(hierarchyId);

        assertThat(result).isEqualToComparingOnlyGivenFields(hierarchy, "id", "name", "type");
        // Flat hierarchy
        assertThat(result.getOptions()).hasSize(2);
        assertThat(result.getOptions().get(0)).isEqualToComparingOnlyGivenFields(entry1, "name", "code", "levelType");
        assertThat(result.getOptions().get(1)).isEqualToComparingOnlyGivenFields(entry2, "name", "code", "levelType");
        assertThat(result.getOptions()).allMatch(option -> isEmpty(option.getChildren()));
    }

    @Test
    public void shouldReflectHierarchy() throws Exception {
        String hierarchyId = "testHierarchy";
        Hierarchy hierarchy = hierarchy(hierarchyId, "test type", "test name");
        HierarchyEntry entry1 = entry(hierarchy, 0, "code1", "name1");
        HierarchyEntry entry2 = entry(hierarchy, 1, "code2", "name2");
        HierarchyEntry entry3 = entry(hierarchy, 2, "code3", "name3");
        entry2.setParent(entry1); entry1.getChildren().add(entry2);
        entry3.setParent(entry2); entry2.getChildren().add(entry3);

        // Mix up the order a bit to ensure that we get the correct result anyway
        when(mockDao.findAllEntriesInHierarchy(hierarchyId)).thenReturn(Arrays.asList(entry3, entry1, entry2));

        DimensionMetadata result = metadataService.getHierarchy(hierarchyId);
        assertThat(result.getOptions()).hasSize(1);
        DimensionOption root = result.getOptions().get(0);
        assertThat(root).isEqualToComparingOnlyGivenFields(entry1, "name", "code", "levelType");
        assertThat(root.getChildren()).hasSize(1);
        DimensionOption firstChild = root.getChildren().iterator().next();
        assertThat(firstChild).isEqualToComparingOnlyGivenFields(entry2, "name", "code", "levelType");
        assertThat(firstChild.getChildren()).hasSize(1);
        DimensionOption secondChild = firstChild.getChildren().iterator().next();
        assertThat(secondChild).isEqualToComparingOnlyGivenFields(entry3, "name", "code", "levelType");
        assertThat(secondChild.getChildren()).isNullOrEmpty();
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

    private static Hierarchy hierarchy(String id, String type, String name) {
        final Hierarchy hierarchy = new Hierarchy();
        hierarchy.setId(id);
        hierarchy.setType(type);
        hierarchy.setName(name);
        return hierarchy;
    }

    private static HierarchyEntry entry(Hierarchy hierarchy, int level, String code, String name) {
        HierarchyEntry entry = new HierarchyEntry();
        entry.setHierarchy(hierarchy);
        entry.setName(name);
        entry.setCode(code);
        entry.setChildren(new ArrayList<>());
        entry.setId(UUID.randomUUID());
        HierarchyLevelType levelType = new HierarchyLevelType();
        levelType.setLevel(level);
        entry.setLevelType(levelType);
        return entry;
    }
}