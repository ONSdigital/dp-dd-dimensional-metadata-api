package uk.co.onsdigital.discovery.metadata.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;

import java.util.Collections;
import java.util.List;

/**
 * Represents metadata about a particular dataset.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataResourceResult {

    private String datasetId;
    private Latest latest;
    private String metadata;
    private List<Edition> editions;

    public Latest getLatest() {
        return latest;
    }

    public void setLatest(Latest latest) {
        this.latest = latest;
    }

    public List<Edition> getEditions() {
        return editions;
    }

    public void setEditions(List<Edition> editions) {
        this.editions = editions;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @JsonRawValue
    public String getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "DataResourceResult{" +
                "datasetId='" + datasetId + '\'' +
                ", latest='" + latest + '\'' +
                ", editions=" + editions + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
