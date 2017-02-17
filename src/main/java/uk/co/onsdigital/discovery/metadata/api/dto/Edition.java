package uk.co.onsdigital.discovery.metadata.api.dto;

import java.util.List;

public class Edition {

    private String label;
    private String id;
    private List<Integer> versions;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Integer> getVersions() {
        return versions;
    }

    public void setVersions(List<Integer> versions) {
        this.versions = versions;
    }

    @Override
    public String toString() {
        return "Edition{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", versions='" + versions +
                '}';
    }
}
