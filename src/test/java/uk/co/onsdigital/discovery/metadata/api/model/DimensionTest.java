package uk.co.onsdigital.discovery.metadata.api.model;

import org.testng.annotations.Test;
import uk.co.onsdigital.discovery.model.Hierarchy;

import static org.assertj.core.api.Assertions.assertThat;

public class DimensionTest {

    @Test
    public void shouldBeHierarchicalIfLinkedToAHierarchy() {
        Dimension dimension = new Dimension();
        dimension.setHierarchy(new Hierarchy());
        assertThat(dimension.isHierarchical()).isTrue();
    }

    @Test
    public void shouldNotBeHierarchicalIfNotLinked() {
        Dimension dimension = new Dimension();
        assertThat(dimension.isHierarchical()).isFalse();
    }

    @Test
    public void shouldByStandardTypeIfNotHierarchical() {
        Dimension dimension = new Dimension();
        assertThat(dimension.getType()).isEqualTo("standard");
    }

    @Test
    public void shouldInheritTypeFromHierarchy() {
        Dimension dimension = new Dimension();
        Hierarchy hierarchy = new Hierarchy();
        hierarchy.setType("some type");
        dimension.setHierarchy(hierarchy);

        assertThat(dimension.getType()).isEqualTo(hierarchy.getType());
    }

}