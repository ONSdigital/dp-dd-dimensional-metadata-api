package uk.co.onsdigital.discovery.metadata.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import static java.util.Objects.requireNonNull;

/**
 * Specialised URL builder service to centralise construction of external URLs.
 */
@Service
public class LegacyUrlBuilder {
    static final int MAX_PAGE_SIZE = 1000;

    private final String baseUrl;

    private final UriTemplate pageTemplate;
    private final UriTemplate dataSetTemplate;
    private final UriTemplate dimensionsTemplate;
    private final UriTemplate dimensionTemplate;
    private final UriTemplate hierarchyTemplate;

    LegacyUrlBuilder(@Value("#{systemEnvironment['BASE_URL'] ?: 'http://localhost:20099'}") String baseUrl) {
        this.baseUrl = requireNonNull(baseUrl);

        pageTemplate = new UriTemplate(baseUrl + "/versions?page={page}&size={size}");
        dataSetTemplate = new UriTemplate(baseUrl + "/version/{dataSetId}");
        dimensionsTemplate = new UriTemplate(baseUrl + "/versions/{dataSetId}/dimensions");
        dimensionTemplate = new UriTemplate(baseUrl + "/versions/{dataSetId}/dimensions/{dimensionId}");
        hierarchyTemplate = new UriTemplate(baseUrl + "/hierarchies/{hierarchyId}");
    }

    /**
     * Returns a template function that can be used to construct URLs for navigating to particular pages.
     *
     * @param pageSize the number of items on each page.
     * @return a {@link PageUrlTemplate} for building links to individual pages in the result set.
     */
    public PageUrlTemplate datasetsPage(int pageSize) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize should be >= 1");
        }
        if (pageSize > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("pageSize must be <= 1000");
        }
        return pageNumber -> pageTemplate.expand(pageNumber, pageSize).toString();
    }

    /**
     * Constructs a link to a particular dataset.
     *
     * @param id the id of the dataset.
     * @return an external link to the given dataset.
     */
    public String dataset(String id) {
        return dataSetTemplate.expand(id).toString();
    }

    /**
     * Constructs a link to the dimensions of a given dataset.
     *
     * @param dataSetId the dataset id.
     * @return an external link to the dimensions page for the dataset.
     */
    public String dimensions(String dataSetId) {
        return dimensionsTemplate.expand(dataSetId).toString();
    }

    /**
     * Constructs a link to a given dimension referenced in a dataset.
     *
     * @param dataSetId the id of the dataset.
     * @param dimensionId the id of the dimension.
     * @return a link to the given dimension in the given dataset.
     */
    public String dimension(String dataSetId, String dimensionId) {
        return dimensionTemplate.expand(dataSetId, dimensionId).toString();
    }

    /**
     * Constructs a link to the given hierarchy.
     *
     * @param hierarchyId the id of the hierarchy.
     * @return a link to the given hierarchy.
     */
    public String hierarchy(String hierarchyId) {
        return hierarchyTemplate.expand(hierarchyId).toString();
    }

    /**
     * Simple builder interface for constructing links to particular pages in a result set.
     */
    public interface PageUrlTemplate {
        /**
         * Builds a link to the given page number in the results.
         *
         * @param pageNumber the page number to link to.
         * @return a link to that page.
         */
        String build(int pageNumber);
    }

    @Override
    public String toString() {
        return "UrlBuilder{" +
                "baseUrl='" + baseUrl + '\'' +
                '}';
    }
}
