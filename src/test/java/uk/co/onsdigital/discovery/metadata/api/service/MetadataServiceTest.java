package uk.co.onsdigital.discovery.metadata.api.service;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.co.onsdigital.discovery.metadata.api.dao.MetadataDao;
import uk.co.onsdigital.discovery.metadata.api.exception.ConceptSystemNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.model.DataSet;
import uk.co.onsdigital.discovery.metadata.api.model.Dimension;
import uk.co.onsdigital.discovery.metadata.api.model.DimensionOption;
import uk.co.onsdigital.discovery.model.Category;
import uk.co.onsdigital.discovery.model.ConceptSystem;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
        when(mockDao.findAllDataSets()).thenReturn(Collections.singletonList(dbDataSet(UUID.fromString(DATASET_ID), "", "")));

        Set<DataSet> result = metadataService.listAvailableDataSets();

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getUrl()).isEqualTo(BASE_URL + "/datasets/" + DATASET_ID);
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

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailIfDataSetNotFound() throws Exception {
        when(mockDao.findDataSetById(DATASET_ID)).thenThrow(new DataSetNotFoundException("test"));

        metadataService.findDataSetById(DATASET_ID);
    }

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailIfDataSetNotFoundForDimension() throws Exception {
        when(mockDao.findConceptSystemByDataSetAndConceptSystemName(DATASET_ID, "any")).thenThrow(new DataSetNotFoundException("test"));

        metadataService.findDimensionById(DATASET_ID, "any");
    }

    @Test(expectedExceptions = DimensionNotFoundException.class)
    public void shouldFailIfDimensionNotFound() throws Exception {
        String dimensionId = "testDimension";
        when(mockDao.findConceptSystemByDataSetAndConceptSystemName(DATASET_ID, dimensionId)).thenThrow(new ConceptSystemNotFoundException(dimensionId));

        metadataService.findDimensionById(DATASET_ID, dimensionId);
    }

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailToFindDimensionIfDataSetNotFound() throws Exception {
        String dimensionId = "testDimension";
        when(mockDao.findConceptSystemByDataSetAndConceptSystemName(DATASET_ID, dimensionId)).thenThrow(new DataSetNotFoundException("test"));

        metadataService.findDimensionById(DATASET_ID, dimensionId);
    }

    @Test
    public void shouldMapConceptSystemsToDimensions() throws Exception {
        ConceptSystem conceptSystem = new ConceptSystem();
        conceptSystem.setConceptSystem("NACE");
        conceptSystem.setCategories(Collections.singletonList(new Category()));
        when(mockDao.findConceptSystemsInDataSet(DATASET_ID)).thenReturn(Collections.singleton(conceptSystem));

        Set<Dimension> result = metadataService.listDimensionsForDataSet(DATASET_ID);

        assertThat(result).hasSize(1);
        Dimension dimension = result.iterator().next();
        assertThat(dimension.getId()).isEqualTo("NACE");
        assertThat(dimension.getName()).isEqualTo("NACE");
    }

    @Test
    public void shouldMapConceptSystemCategoriesToDimensionOptions() throws Exception {
        ConceptSystem concept = conceptSystem();
        when(mockDao.findConceptSystemByDataSetAndConceptSystemName(DATASET_ID, concept.getConceptSystem())).thenReturn(concept);

        Dimension dimension = metadataService.findDimensionById(DATASET_ID, concept.getConceptSystem());

        assertThat(dimension).isNotNull();
        assertThat(dimension.getOptions()).containsOnly(new DimensionOption("1", "Category 1"), new DimensionOption("2", "Category 2"));
    }

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailToListDimensionsIfDataSetNotFound() throws Exception {
        when(mockDao.findConceptSystemsInDataSet(DATASET_ID)).thenThrow(new DataSetNotFoundException(""));

        metadataService.listDimensionsForDataSet(DATASET_ID);
    }

    @Test
    public void shouldExcludeDimensionsWithNoOptions() throws Exception {
        ConceptSystem conceptSystem = new ConceptSystem();
        conceptSystem.setConceptSystem("NACE");
        when(mockDao.findConceptSystemsInDataSet(DATASET_ID)).thenReturn(Collections.singleton(conceptSystem));

        Set<Dimension> result = metadataService.listDimensionsForDataSet(DATASET_ID);

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldConvertConceptSystemToDimension() throws Exception {
        ConceptSystem conceptSystem = conceptSystem();
        String dimensionId = "NACE";
        when(mockDao.findConceptSystemByDataSetAndConceptSystemName(DATASET_ID, dimensionId)).thenReturn(conceptSystem);

        Dimension result = metadataService.findDimensionById(DATASET_ID, dimensionId);

        assertThat(result.getId()).isEqualTo(dimensionId);
        assertThat(result.getName()).isEqualTo(conceptSystem.getConceptSystem());
        assertThat(result.getOptions()).hasSize(conceptSystem.getCategories().size());
    }

    private static ConceptSystem conceptSystem() {
        ConceptSystem conceptSystem = new ConceptSystem();
        conceptSystem.setConceptSystem("NACE");
        Category cat1 = new Category();
        cat1.setName("Category 1");
        cat1.setCategoryId(1L);
        Category cat2 = new Category();
        cat2.setName("Category 2");
        cat2.setCategoryId(2L);
        conceptSystem.setCategories(Arrays.asList(cat1, cat2));
        return conceptSystem;
    }

    private static void assertDataSetEqualsDbModel(final DataSet actual, final DimensionalDataSet expected) {
        assertThat(actual.getId()).isEqualTo(expected.getDimensionalDataSetId().toString());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
    }

    private DimensionalDataSet dbDataSet(UUID dataSetId, String title, String description) {
        DimensionalDataSet dataSet = new DimensionalDataSet();
        dataSet.setDimensionalDataSetId(dataSetId);
        dataSet.setTitle(title);
        dataSet.setDescription(description);
        return dataSet;
    }
}