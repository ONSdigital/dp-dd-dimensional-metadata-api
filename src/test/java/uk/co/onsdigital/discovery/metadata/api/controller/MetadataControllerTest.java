package uk.co.onsdigital.discovery.metadata.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.co.onsdigital.discovery.metadata.api.dto.common.DimensionMetadata;
import uk.co.onsdigital.discovery.metadata.api.service.DimensionViewType;
import uk.co.onsdigital.discovery.metadata.api.service.MetadataService;

import static org.mockito.Mockito.*;

public class MetadataControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired MetadataController metadataController;
    @Autowired MetadataService metadataService;

    @Configuration
    @EnableCaching
    static class Config {

        public static final int defaultCacheTimeMinutes = 3;
        public static final int defaultCacheTimeSeconds = defaultCacheTimeMinutes * 60;

        @Bean
        public MetadataController getMetadataController() {
            return new MetadataController(getMetadataService(), defaultCacheTimeMinutes);
        }

        @Bean
        public MetadataService getMetadataService() {
            return mock(MetadataService.class);
        }

        @Bean
        public CacheManager getCacheManager() {
            return new ConcurrentMapCacheManager("hierarchies", "dimensions");
        }
    }

    @Test
    public void getHierarchyShouldCacheResponse() {

        // Given a hierarchy ID we want to get.
        String hierarchyId = "hierarchy1";

        // When we call getHierarchy multiple times
        metadataController.getHierarchy(hierarchyId);
        metadataController.getHierarchy(hierarchyId);

        // Then the metadata service is called only once as it is cached after the first call.
        verify(metadataService, times(1)).getHierarchy(hierarchyId);
    }

    @Test
    public void getHierarchyShouldReturnCacheControlHeaders() {

        // Given a hierarchy ID we want to get.
        String hierarchyId = "hierarchy1";

        // When we call getHierarchy
        ResponseEntity<DimensionMetadata> response = metadataController.getHierarchy(hierarchyId);

        // Then the response contains cache control headers
        String actual = response.getHeaders().get("Cache-Control").get(0);
        String expected = String.format("max-age=%d", Config.defaultCacheTimeSeconds);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void findDimensionByIdWithDatasetUuidShouldCacheResponse() {

        // Given a dimension ID we want to get.
        String datasetId = "datasetId";
        String dimensionId = "dimensionId";
        String view = "HIERARCHY";

        // When we call findDimensionByIdWithDatasetUuid multiple times
        metadataController.findDimensionByIdWithDatasetUuid(datasetId, dimensionId, view);
        metadataController.findDimensionByIdWithDatasetUuid(datasetId, dimensionId, view);

        // Then the metadata service is called only once as it is cached after the first call.
        verify(metadataService, times(1)).findDimensionByIdWithDatasetUuid(datasetId, dimensionId, DimensionViewType.HIERARCHY);
    }

    @Test
    public void findDimensionByIdWithDatasetUuidShouldReturnCacheControlHeaders() {

        // Given a dimension ID we want to get.
        String datasetId = "datasetId";
        String dimensionId = "dimensionId";
        String view = "HIERARCHY";

        // When we call findDimensionByIdWithDatasetUuid
        ResponseEntity<DimensionMetadata> response = metadataController.findDimensionByIdWithDatasetUuid(datasetId, dimensionId, view);

        // Then the response contains cache control headers
        String actual = response.getHeaders().get("Cache-Control").get(0);
        String expected = String.format("max-age=%d", Config.defaultCacheTimeSeconds);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void findDimensionByIdWithEditionVersionShouldCacheResponse() {

        // Given a dimension ID we want to get.
        String datasetId = "datasetId";
        String edition = "edition";
        String dimensionId = "dimensionId";
        int version = 3;
        String view = "HIERARCHY";

        // When we call findDimensionByIdWithEditionVersion multiple times
        metadataController.findDimensionByIdWithEditionVersion(datasetId, edition, version, dimensionId, view);
        metadataController.findDimensionByIdWithEditionVersion(datasetId, edition, version, dimensionId, view);

        // Then the metadata service is called only once as it is cached after the first call.
        verify(metadataService, times(1))
                .findDimensionByIdWithEditionVersion(datasetId, edition, version, dimensionId, DimensionViewType.HIERARCHY);
    }

    @Test
    public void findDimensionByIdWithEditionVersionShouldReturnCacheControlHeaders() {

        // Given a dimension ID we want to get.
        String datasetId = "datasetId";
        String edition = "edition";
        String dimensionId = "dimensionId";
        int version = 3;
        String view = "HIERARCHY";

        // When we call findDimensionByIdWithEditionVersion multiple times
        ResponseEntity<DimensionMetadata> response = metadataController.findDimensionByIdWithEditionVersion(datasetId, edition, version, dimensionId, view);

        // Then the response contains cache control headers
        String actual = response.getHeaders().get("Cache-Control").get(0);
        String expected = String.format("max-age=%d", Config.defaultCacheTimeSeconds);
        Assert.assertEquals(actual, expected);
    }
}
