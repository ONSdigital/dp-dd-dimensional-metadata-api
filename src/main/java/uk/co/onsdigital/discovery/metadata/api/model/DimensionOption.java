package uk.co.onsdigital.discovery.metadata.api.model;

/**
 * A possible option for a dimension, such as <em>Male</em> or <em>Female</em> for the dimension <em>Sex</em>.
 */
public class DimensionOption {
    private final String id;
    private final String name;

    public DimensionOption(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DimensionOption that = (DimensionOption) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DimensionOption{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
