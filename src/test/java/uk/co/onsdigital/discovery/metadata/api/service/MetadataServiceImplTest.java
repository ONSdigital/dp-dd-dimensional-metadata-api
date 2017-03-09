package uk.co.onsdigital.discovery.metadata.api.service;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.co.onsdigital.discovery.metadata.api.dao.MetadataDao;
import uk.co.onsdigital.discovery.metadata.api.dto.*;
import uk.co.onsdigital.discovery.model.DataResource;
import uk.co.onsdigital.discovery.model.DataSet;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * Created by matt on 09/03/17.
 */
public class MetadataServiceImplTest {

    MetadataServiceImpl testObj;

    @Mock MetadataDao metadataDaoMock;
    @Mock LegacyUrlBuilder legacyUrlBuilderMock;
    @Mock UrlBuilder urlBuilderMock;

    DataResource dataResource;
    DataSet completeDataSet;
    DataSet incompleteDataSet;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        testObj = new MetadataServiceImpl(metadataDaoMock, urlBuilderMock, legacyUrlBuilderMock);
    }

    @Test
    public void listAvailableDataResourcesShouldExcludeIncompleteDatasets() throws Exception {
        dataResource = new DataResource("TestResource", "Test resource");
        completeDataSet = new DataSet("s3://foo/bar", dataResource);
        completeDataSet.setTitle("Complete dataset");
        completeDataSet.setMajorLabel("Complete dataset label");
        completeDataSet.setMajorVersion(1);
        completeDataSet.setStatus(DataSet.STATUS_COMPLETE);

        incompleteDataSet = new DataSet("s3://foo/baz", dataResource);
        incompleteDataSet.setTitle("Incomplete");
        incompleteDataSet.setTitle("Incomplete label");
        incompleteDataSet.setMajorVersion(2);
        incompleteDataSet.setStatus(DataSet.STATUS_NEW);

        dataResource.setDataSets(Arrays.asList(incompleteDataSet,completeDataSet));

        when(metadataDaoMock.findDataResourcesPage(1,1)).thenReturn(Arrays.asList(dataResource));

        ResultPage<DataResourceResult> result = testObj.listAvailableDataResources(1, 1);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).isNotNull();
        assertThat(result.getItems().size()).isEqualTo(1);

        DataResourceResult resource = result.getItems().get(0);
        assertThat(resource).isNotNull();
        List<Edition> editions = resource.getEditions();
        assertThat(editions).isNotNull();
        assertThat(editions.size()).isEqualTo(1);
        Edition edition = editions.get(0);
        assertThat(edition).isNotNull();
        assertThat(edition.getLabel()).isEqualTo("Complete dataset label");
        assertThat(edition.getVersions().size()).isEqualTo(1);
    }

}