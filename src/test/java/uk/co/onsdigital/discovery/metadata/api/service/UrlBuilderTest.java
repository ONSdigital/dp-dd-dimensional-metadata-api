package uk.co.onsdigital.discovery.metadata.api.service;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlBuilderTest {
    private static final String BASE_URL = "http://example.com:1234/test";

    private UrlBuilder urlBuilder;

    @BeforeMethod
    public void createUrlBuilder() {
        urlBuilder = new UrlBuilder(BASE_URL);
    }

    @Test
    public void shouldConstructCorrectPageLinks() {
        assertThat(urlBuilder.datasetsPage(5).build(4)).isEqualTo(BASE_URL + "/datasets?page=4&size=5");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldRejectPageSizeOfZero() {
        urlBuilder.datasetsPage(0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldRejectNegativePageSize() {
        urlBuilder.datasetsPage(-1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldRejectPageSizeTooLarge() {
        urlBuilder.datasetsPage(LegacyUrlBuilder.MAX_PAGE_SIZE + 1);
    }

    @Test
    public void shouldConstructCorrectDataResourceLinks() {
        assertThat(urlBuilder.dataset("test")).isEqualTo(BASE_URL + "/datasets/test");
    }

    @Test
    public void shouldConstructCorrectDimensionsLinks() {
        assertThat(urlBuilder.dimensionalDataSet("test", "2017", 1)).isEqualTo(BASE_URL + "/datasets/test/editions/2017/versions/1");
    }

    @Test
    public void shouldConstructCorrectDimensionLinks() {
        assertThat(urlBuilder.dimension("test", "2017", 1, "testDimension")).isEqualTo(BASE_URL + "/datasets/test/editions/2017/versions/1/dimensions/testDimension");
    }

    @Test
    public void shouldHandleUnicodeDimensionNames() {
        // Dataset IDs are UUIDs so will always be ASCII, but dimension IDs might not be. NB: assume UTF-8 encoding.
        assertThat(urlBuilder.dimension("test", "2017", 1, "café")).isEqualTo(BASE_URL + "/datasets/test/editions/2017/versions/1/dimensions/caf%C3%A9");
    }

    @Test
    public void shouldConstructCorrectHierarchyLinks() {
        assertThat(urlBuilder.hierarchy("testHierarchy")).isEqualTo(BASE_URL + "/hierarchies/testHierarchy");
    }
}