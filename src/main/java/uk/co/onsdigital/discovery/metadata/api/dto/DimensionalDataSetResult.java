package uk.co.onsdigital.discovery.metadata.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import uk.co.onsdigital.discovery.metadata.api.dto.legacy.LegacyDataSet;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DimensionalDataSetResult extends LegacyDataSet {
    private String edition;
    private String version;
    private String datasetId;

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override @JsonRawValue
    public String getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "DataSet{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", s3URL='" + s3URL + '\'' +
                ", url='" + url + '\'' +
                ", edition=" + edition + "\'" +
                ", version=" + version + "\'" +
                ", metadata=" + metadata +
                '}';
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }
}
