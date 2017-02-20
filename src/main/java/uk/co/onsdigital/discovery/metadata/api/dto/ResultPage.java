package uk.co.onsdigital.discovery.metadata.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uk.co.onsdigital.discovery.metadata.api.service.LegacyUrlBuilder;
import uk.co.onsdigital.discovery.metadata.api.service.UrlBuilder;

import java.util.List;

/**
 * Specialistion of Spring's {@link PageImpl} to convert the JSON output to match the Stub API field naming.
 */
@JsonPropertyOrder({"items", "first", "prev", "next", "last"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultPage<T> {
    private final Page<T> page;
    private final UrlBuilder.PageUrlTemplate pageUrlTemplate;

    public ResultPage(UrlBuilder.PageUrlTemplate pageUrlTemplate, List<T> content, long total, int pageNumber, int pageSize) {
        this.pageUrlTemplate = pageUrlTemplate;
        Pageable pageable = new PageRequest(pageNumber-1, pageSize);
        this.page = new PageImpl<>(content, pageable, total);
    }

    public List<T> getItems() {
        return page.getContent();
    }

    public long getTotal() {
        return page.getTotalElements();
    }

    public int getCount() {
        return page.getNumberOfElements();
    }

    public int getPage() {
        // Spring pages are 0-based, which seems a bit unintuitive
        return page.getNumber() + 1;
    }

    public int getTotalPages() {
        return (int) (getTotal() / getItemsPerPage());
    }

    public int getItemsPerPage() {
        return page.getSize();
    }

    public long getStartIndex() {
        return page.getNumber() * page.getSize();
    }

    public String getPrev() {
        return getPage() > 1 ? pageUrlTemplate.build(getPage() - 1) : null;
    }

    public String getNext() {
        return getPage() < getTotalPages() ? pageUrlTemplate.build(getPage() + 1) : null;
    }

    public String getFirst() {
        return pageUrlTemplate.build(1);
    }

    public String getLast() {
        return pageUrlTemplate.build(getTotalPages());
    }

    @Override
    public String toString() {
        return "LegacyResultPage{" +
                "page=" + page +
                '}';
    }
}
