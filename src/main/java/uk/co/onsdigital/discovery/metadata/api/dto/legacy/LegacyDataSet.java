package uk.co.onsdigital.discovery.metadata.api.dto.legacy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import uk.co.onsdigital.discovery.metadata.api.dto.common.DimensionMetadata;

import java.util.Collections;
import java.util.List;

/**
 * Represents metadata about a particular dataset.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LegacyDataSet {

    protected String id;
    protected String s3URL;
    protected String title;
    protected String url;
    protected String metadata;
    protected List<DimensionMetadata> dimensions = Collections.emptyList();
    protected String dimensionsUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getS3URL() {
        return s3URL;
    }

    public void setS3URL(String s3URL) {
        this.s3URL = s3URL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonRawValue
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getDimensionsUrl() {
        return dimensionsUrl;
    }

    public void setDimensionsUrl(String dimensionsUrl) {
        this.dimensionsUrl = dimensionsUrl;
    }

    public List<DimensionMetadata> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<DimensionMetadata> dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public String toString() {
        return "DataSet{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", s3URL='" + s3URL + '\'' +
                ", url='" + url + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
