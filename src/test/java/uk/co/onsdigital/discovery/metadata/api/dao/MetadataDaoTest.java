package uk.co.onsdigital.discovery.metadata.api.dao;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.co.onsdigital.discovery.metadata.api.exception.ConceptSystemNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.GeographicHierarchyNotFoundException;
import uk.co.onsdigital.discovery.model.ConceptSystem;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;
import uk.co.onsdigital.discovery.model.GeographicAreaHierarchy;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
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
        List<DimensionalDataSet> dataSets = asList(new DimensionalDataSet(), new DimensionalDataSet());

        when(mockEntityManager.createNamedQuery("DimensionalDataSet.findAll", DimensionalDataSet.class)).thenReturn(mockQuery);
        when(mockQuery.setFirstResult(0)).thenReturn(mockQuery);
        when(mockQuery.setMaxResults(10)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(dataSets);

        List<DimensionalDataSet> result = metadataDao.findDataSetsPage(1, 10);
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
    public void shouldReturnAllReferencedConceptSystems() throws Exception {
        final DimensionalDataSet dataSet = mock(DimensionalDataSet.class);
        final UUID dataSetId = UUID.randomUUID();
        final Set<ConceptSystem> referencedConcepts = new HashSet<>(asList(new ConceptSystem(), new ConceptSystem()));

        when(mockEntityManager.find(DimensionalDataSet.class, dataSetId)).thenReturn(dataSet);
        when(dataSet.getReferencedConceptSystems()).thenReturn(referencedConcepts);

        final Set<ConceptSystem> result = metadataDao.findConceptSystemsInDataSet(dataSetId.toString());

        assertThat(result).isEqualTo(referencedConcepts);
    }

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailIfDataSetNotFoundWhenFindingConceptSystems() throws Exception {
        metadataDao.findConceptSystemsInDataSet(UUID.randomUUID().toString());
    }

    @Test
    public void shouldReturnEmptyListIfNoConceptSystemsInDataSet() throws Exception {
        final DimensionalDataSet dataSet = mock(DimensionalDataSet.class);
        final UUID dataSetId = UUID.randomUUID();

        when(mockEntityManager.find(DimensionalDataSet.class, dataSetId)).thenReturn(dataSet);
        when(dataSet.getReferencedConceptSystems()).thenReturn(null);

        final Set<ConceptSystem> result = metadataDao.findConceptSystemsInDataSet(dataSetId.toString());
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnMatchingConceptSystem() throws Exception {
        final DimensionalDataSet dataSet = mock(DimensionalDataSet.class);
        final UUID dataSetId = UUID.randomUUID();
        final ConceptSystem conceptSystem = new ConceptSystem();
        final String conceptSystemId = "NACE";
        conceptSystem.setId(conceptSystemId);
        final Set<ConceptSystem> referencedConcepts = Collections.singleton(conceptSystem);

        when(mockEntityManager.find(DimensionalDataSet.class, dataSetId)).thenReturn(dataSet);
        when(dataSet.getReferencedConceptSystems()).thenReturn(referencedConcepts);

        ConceptSystem result = metadataDao.findConceptSystemByDataSetAndConceptSystemName(dataSetId.toString(), conceptSystemId);
        assertThat(result).isEqualTo(conceptSystem);
    }

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailIfDataSetNotFoundForConceptSystem() throws Exception {
        metadataDao.findConceptSystemByDataSetAndConceptSystemName(UUID.randomUUID().toString(), "NACE");
    }

    @Test(expectedExceptions = ConceptSystemNotFoundException.class)
    public void shouldFailIfConceptSystemNotFound() throws Exception {
        final UUID dataSetId = UUID.randomUUID();
        final DimensionalDataSet dataSet = mock(DimensionalDataSet.class);
        when(mockEntityManager.find(DimensionalDataSet.class, dataSetId)).thenReturn(dataSet);
        when(dataSet.getReferencedConceptSystems()).thenReturn(new HashSet<>(asList(new ConceptSystem(), new ConceptSystem())));

        metadataDao.findConceptSystemByDataSetAndConceptSystemName(dataSetId.toString(), "NACE");
    }

    @Test
    public void shouldReturnMatchingGeography() throws Exception {
        final DimensionalDataSet dataSet = mock(DimensionalDataSet.class);
        final UUID dataSetId = UUID.randomUUID();
        final String geographyName = "2013ADMIN";
        final GeographicAreaHierarchy geography = new GeographicAreaHierarchy();
        geography.setId(geographyName);

        when(mockEntityManager.find(DimensionalDataSet.class, dataSetId)).thenReturn(dataSet);
        when(dataSet.getReferencedGeographies()).thenReturn(Stream.of(geography));

        GeographicAreaHierarchy result = metadataDao.findGeographyInDataSet(dataSetId.toString(), geographyName);
        assertThat(result).isEqualTo(geography);
    }

    @Test(expectedExceptions = DataSetNotFoundException.class)
    public void shouldFailIfDataSetNotFoundForGeography() throws Exception {
        metadataDao.findGeographyInDataSet(UUID.randomUUID().toString(), "2013ADMIN");
    }

    @Test(expectedExceptions = GeographicHierarchyNotFoundException.class)
    public void shouldFailIfGeographyNotFound() throws Exception {
        final UUID dataSetId = UUID.randomUUID();
        final DimensionalDataSet dataSet = mock(DimensionalDataSet.class);
        when(mockEntityManager.find(DimensionalDataSet.class, dataSetId)).thenReturn(dataSet);
        when(dataSet.getReferencedGeographies()).thenReturn(Stream.empty());

        metadataDao.findGeographyInDataSet(dataSetId.toString(), "2013ADMIN");
    }
}