package uk.co.onsdigital.discovery.metadata.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Latest {
    private String edition;
    private String version;
    private String metadata;
    private String title;
    private String url;

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

    @JsonRawValue
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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

    @Override
    public String toString() {
        return "Latest {" +
                "edition='" + edition + '\'' +
                ", version='" + version + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
