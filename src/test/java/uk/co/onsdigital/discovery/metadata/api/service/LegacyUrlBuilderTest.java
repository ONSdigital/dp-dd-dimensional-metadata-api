package uk.co.onsdigital.discovery.metadata.api.service;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LegacyUrlBuilderTest {
    private static final String BASE_URL = "http://example.com:1234/test";

    private LegacyUrlBuilder legacyUrlBuilder;

    @BeforeMethod
    public void createUrlBuilder() {
        legacyUrlBuilder = new LegacyUrlBuilder(BASE_URL);
    }

    @Test
    public void shouldConstructCorrectPageLinks() {
        assertThat(legacyUrlBuilder.datasetsPage(5).build(4)).isEqualTo(BASE_URL + "/versions?page=4&size=5");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldRejectPageSizeOfZero() {
        legacyUrlBuilder.datasetsPage(0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldRejectNegativePageSize() {
        legacyUrlBuilder.datasetsPage(-1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldRejectPageSizeTooLarge() {
        legacyUrlBuilder.datasetsPage(LegacyUrlBuilder.MAX_PAGE_SIZE + 1);
    }

    @Test
    public void shouldConstructCorrectDataSetLinks() {
        assertThat(legacyUrlBuilder.dataset("test")).isEqualTo(BASE_URL + "/versions/test");
    }

    @Test
    public void shouldConstructCorrectDimensionsLinks() {
        assertThat(legacyUrlBuilder.dimensions("test")).isEqualTo(BASE_URL + "/versions/test/dimensions");
    }

    @Test
    public void shouldConstructCorrectDimensionLinks() {
        assertThat(legacyUrlBuilder.dimension("test", "testDimension")).isEqualTo(BASE_URL + "/versions/test/dimensions/testDimension");
    }

    @Test
    public void shouldHandleUnicodeDimensionNames() {
        // Dataset IDs are UUIDs so will always be ASCII, but dimension IDs might not be. NB: assume UTF-8 encoding.
        assertThat(legacyUrlBuilder.dimension("test", "café")).isEqualTo(BASE_URL + "/versions/test/dimensions/caf%C3%A9");
    }

    @Test
    public void shouldConstructCorrectHierarchyLinks() {
        assertThat(legacyUrlBuilder.hierarchy("testHierarchy")).isEqualTo(BASE_URL + "/hierarchies/testHierarchy");
    }
}