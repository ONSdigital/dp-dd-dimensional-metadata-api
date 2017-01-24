package uk.co.onsdigital.discovery.metadata.api.service;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.co.onsdigital.discovery.metadata.api.dao.MetadataDao;
import uk.co.onsdigital.discovery.metadata.api.exception.ConceptSystemNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.GeographicHierarchyNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.model.DataSet;
import uk.co.onsdigital.discovery.metadata.api.model.Dimension;
import uk.co.onsdigital.discovery.metadata.api.model.DimensionOption;
import uk.co.onsdigital.discovery.metadata.api.model.ResultPage;
import uk.co.onsdigital.discovery.model.Category;
import uk.co.onsdigital.discovery.model.ConceptSystem;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;
import uk.co.onsdigital.discovery.model.GeographicArea;
import uk.co.onsdigital.discovery.model.GeographicAreaHierarchy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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
        metadataService = new MetadataServiceImpl(mockDao, new UrlBuilder(BASE_URL), true);
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
        when(mockDao.findConceptSystemByDataSetAndConceptSystemName(DATASET_ID, "any")).thenThrow(new DataSetNotFoundException("test"));

        metadataService.findDimensionById(DATASET_ID, "any");
    }

    @Test(expectedExceptions = DimensionNotFoundException.class)
    public void shouldFailIfDimensionNotFound() throws Exception {
        String dimensionId = "testDimension";
        when(mockDao.findConceptSystemByDataSetAndConceptSystemName(DATASET_ID, dimensionId)).thenThrow(new ConceptSystemNotFoundException(dimensionId));
        when(mockDao.findGeographyInDataSet(DATASET_ID, dimensionId)).thenThrow(new GeographicHierarchyNotFoundException(dimensionId));

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
        conceptSystem.setId("NACE");
        conceptSystem.setCategories(Collections.singletonList(new Category("test")));
        DimensionalDataSet dataSet = new DimensionalDataSet();
        dataSet.setId(UUID.fromString(DATASET_ID));
        dataSet.setReferencedConceptSystems(Collections.singleton(conceptSystem));
        when(mockDao.findDataSetById(DATASET_ID)).thenReturn(dataSet);

        Set<Dimension> result = metadataService.listDimensionsForDataSet(DATASET_ID);

        assertThat(result).hasSize(1);
        Dimension dimension = result.iterator().next();
        assertThat(dimension.getId()).isEqualTo("NACE");
        assertThat(dimension.getName()).isEqualTo("NACE");
    }

    @Test
    public void shouldMapConceptSystemCategoriesToDimensionOptions() throws Exception {
        ConceptSystem concept = conceptSystem();
        when(mockDao.findConceptSystemByDataSetAndConceptSystemName(DATASET_ID, concept.getId())).thenReturn(concept);

        Dimension dimension = metadataService.findDimensionById(DATASET_ID, concept.getId());

        assertThat(dimension).isNotNull();
        assertThat(dimension.getOptions()).containsOnly(new DimensionOption("1", "Category 1"), new DimensionOption("2", "Category 2"));
    }

    @Test
    public void shouldMapGeographicHierarchiesToDimensions() throws Exception {
        String geographyName = "2013ADMIN";
        GeographicAreaHierarchy geographicAreaHierarchy = new GeographicAreaHierarchy();
        geographicAreaHierarchy.setId(geographyName);

        DimensionalDataSet dataSet = mock(DimensionalDataSet.class);
        when(dataSet.getId()).thenReturn(UUID.fromString(DATASET_ID));
        when(dataSet.getReferencedConceptSystems()).thenReturn(Collections.emptySet());
        when(dataSet.getReferencedGeographies()).thenReturn(Stream.of(geographicAreaHierarchy));

        when(mockDao.findDataSetById(DATASET_ID)).thenReturn(dataSet);

        Set<Dimension> result = metadataService.listDimensionsForDataSet(DATASET_ID);

        assertThat(result).hasSize(1);
        Dimension dimension = result.iterator().next();
        assertThat(dimension.getId()).isEqualTo(geographyName);
        assertThat(dimension.getName()).isEqualTo(geographyName);
    }

    @Test
    public void shouldRecursivelyMapGeographyAreasToDimensionOptions() throws Exception {
        GeographicArea uk = area("K001", "UK", null);
        GeographicArea england = area("K002", "England", uk);
        GeographicArea wales = area("K003", "Wales", uk);
        GeographicArea newport = area("K004", "Newport", wales);

        String geographyName = "2013ADMIN";
        GeographicAreaHierarchy geographicAreaHierarchy = new GeographicAreaHierarchy();
        geographicAreaHierarchy.setId(geographyName);
        geographicAreaHierarchy.setGeographicAreas(Arrays.asList(uk, england, newport, wales));

        DimensionalDataSet dataSet = mock(DimensionalDataSet.class);
        when(dataSet.getId()).thenReturn(UUID.fromString(DATASET_ID));
        when(dataSet.getReferencedConceptSystems()).thenReturn(Collections.emptySet());
        when(dataSet.getReferencedGeographies()).thenReturn(Stream.of(geographicAreaHierarchy));

        when(mockDao.findDataSetById(DATASET_ID)).thenReturn(dataSet);

        Set<Dimension> result = metadataService.listDimensionsForDataSet(DATASET_ID);
        assertThat(result).hasSize(1);
        final Dimension dimension = result.iterator().next();
        assertThat(dimension.getId()).isEqualTo(geographyName);
        assertThat(dimension.getOptions()).hasSize(1)
            .containsOnly(option(uk.getExtCode(), uk.getName(),
                option(england.getExtCode(), england.getName()),
                option(wales.getExtCode(), wales.getName(),
                        option(newport.getExtCode(), newport.getName()))));
    }

    private GeographicArea area(String extCode, String name, GeographicArea parent) {
        final GeographicArea area = new GeographicArea();
        area.setExtCode(extCode);
        area.setName(name);
        if (parent != null) {
            if (parent.getGeographicAreas() == null) {
                parent.setGeographicAreas(new ArrayList<>());
            }
            parent.addGeographicArea(area);
        }
        return area;
    }

    private DimensionOption option(String id, String name, DimensionOption... children) {
        final DimensionOption option = new DimensionOption(id, name);
        for (DimensionOption child : children) {
            option.addOption(child);
        }
        return option;
    }

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailToListDimensionsIfDataSetNotFound() throws Exception {
        when(mockDao.findDataSetById(DATASET_ID)).thenThrow(new DataSetNotFoundException(""));

        metadataService.listDimensionsForDataSet(DATASET_ID);
    }

    @Test
    public void shouldExcludeDimensionsWithNoOptions() throws Exception {
        DimensionalDataSet dataSet = new DimensionalDataSet();
        dataSet.setId(UUID.fromString(DATASET_ID));
        ConceptSystem conceptSystem = new ConceptSystem();
        dataSet.setReferencedConceptSystems(Collections.singleton(conceptSystem));
        conceptSystem.setId("NACE");
        when(mockDao.findDataSetById(DATASET_ID)).thenReturn(dataSet);

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
        assertThat(result.getName()).isEqualTo(conceptSystem.getId());
        assertThat(result.getOptions()).hasSize(conceptSystem.getCategories().size());
    }

    private static ConceptSystem conceptSystem() {
        ConceptSystem conceptSystem = new ConceptSystem();
        conceptSystem.setId("NACE");
        Category cat1 = new Category();
        cat1.setName("Category 1");
        cat1.setId(1L);
        Category cat2 = new Category();
        cat2.setName("Category 2");
        cat2.setId(2L);
        conceptSystem.setCategories(Arrays.asList(cat1, cat2));
        return conceptSystem;
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