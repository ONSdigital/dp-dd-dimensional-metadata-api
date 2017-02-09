package uk.co.onsdigital.discovery.metadata.api.service;

import org.testng.annotations.Test;
import uk.co.onsdigital.discovery.metadata.api.dto.DimensionOption;
import uk.co.onsdigital.discovery.model.DimensionValue;
import uk.co.onsdigital.discovery.model.Hierarchy;
import uk.co.onsdigital.discovery.model.HierarchyEntry;
import uk.co.onsdigital.discovery.model.HierarchyLevelType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.CollectionUtils.isEmpty;

public class DimensionViewTypeTest {
    private static final HierarchyLevelType ROOT_LEVEL = new HierarchyLevelType();
    static {
        ROOT_LEVEL.setName("root");
        ROOT_LEVEL.setLevel(0);
    }

    @Test
    public void viewTypeNoneShouldAlwaysReturnNull() {
        List<DimensionValue> values = Arrays.asList(new DimensionValue("1"), new DimensionValue("2"));
        assertThat(DimensionViewType.NONE.convertValues(values)).isNull();
    }

    @Test
    public void viewTypeListShouldReturnAFlatListForHierarchicalDimensions() {
        final List<DimensionValue> values = getTestDimensionValues(getTestHierarchy());
        final List<DimensionOption> options = DimensionViewType.LIST.convertValues(values);

        assertThat(options).hasSize(values.size());
        assertThat(options).allMatch(option -> isEmpty(option.getChildren()));
    }

    @Test
    public void viewTypeListShouldReturnAFlatListForFlatDimensions() {
        final List<DimensionValue> values = getTestDimensionValues(Collections.emptyMap());
        final List<DimensionOption> options = DimensionViewType.LIST.convertValues(values);

        assertThat(options).hasSize(values.size());
        assertThat(options).allMatch(option -> isEmpty(option.getChildren()));
    }

    @Test
    public void viewTypeListShouldUseHierarchyInfoWhenPresent() {
        final List<DimensionValue> values = getTestDimensionValues(getTestHierarchy());
        final List<DimensionOption> options = DimensionViewType.LIST.convertValues(values);

        assertThat(options).hasSize(values.size());
        for (int i = 0; i < values.size(); ++i) {
            DimensionValue value = values.get(i);
            DimensionOption option = options.get(i);

            assertThat(option.getId()).isNotNull();
            assertThat(option.getName()).isEqualTo(value.getHierarchyEntry().getName());
            assertThat(option.getCode()).isEqualTo(value.getHierarchyEntry().getCode());
            assertThat(option.getLevelType()).isEqualTo(value.getHierarchyEntry().getLevelType());
        }
    }

    @Test
    public void viewTypeHierarchyShouldReturnFlatListForFlatDimensions() {
        final List<DimensionValue> values = getTestDimensionValues(Collections.emptyMap());
        final List<DimensionOption> options = DimensionViewType.HIERARCHY.convertValues(values);

        assertThat(options).hasSize(values.size());
        assertThat(options).allMatch(option -> option.getChildren() == null);
    }

    @Test
    public void viewTypeHierarchyShouldReturnSparseHierarchies() {
        final List<DimensionValue> values = getTestDimensionValues(getTestHierarchy());
        final List<DimensionOption> options = DimensionViewType.HIERARCHY.convertValues(values);

        // Should only be a single root option in the test data
        assertThat(options).as("root options").hasSize(1);
        assertThat(countAll(options)).isEqualTo(values.size());

        // All top-level options should be at the root level
        assertThat(options).as("top-level options").allMatch(root -> root.getLevelType() == ROOT_LEVEL);
        assertThat(options.get(0).getChildren()).as("first-level options").isNotEmpty().allMatch(node -> node.getLevelType().getLevel() == 1);
    }

    @Test
    public void viewTypeHierarchyShouldCreateEmptyLevelsWhenMissingInData() {
        final HierarchyEntry england = getEnglandHierarchy();
        final DimensionValue value = new DimensionValue("testValue");
        value.setHierarchyEntry(england);

        final List<DimensionOption> roots = DimensionViewType.HIERARCHY.convertValues(singletonList(value));

        // Check that we have "empty" UK and England and Wales entries above the non-empty England entry
        assertThat(roots).hasSize(1);
        DimensionOption uk = roots.get(0);
        assertThat(uk.getCode()).isEqualTo("UK");
        assertThat(uk.getName()).isEqualTo("United Kingdom");
        assertThat(uk.isEmpty()).isTrue();
        assertThat(uk.getChildren()).hasSize(1);

        DimensionOption ew = uk.getChildren().iterator().next();
        assertThat(ew.getCode()).isEqualTo("EW");
        assertThat(ew.getName()).isEqualTo("England and Wales");
        assertThat(ew.isEmpty()).isTrue();
        assertThat(ew.getChildren()).hasSize(1);

        DimensionOption eng = ew.getChildren().iterator().next();
        assertThat(eng.getCode()).isEqualTo("E");
        assertThat(eng.getName()).isEqualTo("England");
        assertThat(eng.isEmpty()).isFalse();
        assertThat(eng.getChildren()).isNullOrEmpty();
    }

    private int countAll(Collection<DimensionOption> options) {
        int count = 0;
        if (options != null) {
            for (DimensionOption option : options) {
                count += countAll(option.getChildren()) + 1;
            }
        }
        return count;
    }

    private List<DimensionValue> getTestDimensionValues(final Map<String, HierarchyEntry> hierarchyEntryMap) {
        final List<DimensionValue> values = new ArrayList<>();

        for (int i = 0; i < 100; ++i) {
            final String code = "value" + i;
            final DimensionValue value = new DimensionValue( code);
            if (hierarchyEntryMap.containsKey(code)) {
                value.setHierarchyEntry(hierarchyEntryMap.get(code));
            }
            values.add(value);
        }

        return values;
    }

    private Map<String, HierarchyEntry> getTestHierarchy() {
        final Hierarchy hierarchy = new Hierarchy();
        hierarchy.setName("Test Hierarchy");
        hierarchy.setType("test");
        hierarchy.setId("TH001");

        Map<String, HierarchyEntry> entries = new HashMap<>();
        Random random = new Random();

        for (int i = 0; i < 100; ++i) {
            HierarchyEntry entry = new HierarchyEntry();
            String code = "value" + i;
            entry.setHierarchy(hierarchy);
            entry.setName("foo" + i);
            entry.setCode(code);
            entry.setDisplayOrder(i);
            entry.setId(UUID.randomUUID());
            entry.setLevelType(ROOT_LEVEL);

            // Randomly set a parent entry
            if (i > 0) {
                int parent = random.nextInt(i);
                entry.setParent(entries.get("value" + parent));
                entry.setLevelType(nextLevelType(entry.getLevelType()));
            }

            entries.put(code, entry);
        }

        return entries;
    }

    private static HierarchyLevelType nextLevelType(HierarchyLevelType parentLevel) {
        HierarchyLevelType nextLevel = new HierarchyLevelType();
        nextLevel.setName("test");
        nextLevel.setLevel(parentLevel.getLevel() + 1);
        return nextLevel;
    }

    private static HierarchyEntry getEnglandHierarchy() {
        final HierarchyEntry uk = new HierarchyEntry();
        uk.setId(UUID.randomUUID());
        uk.setCode("UK");
        uk.setLevelType(ROOT_LEVEL);
        uk.setName("United Kingdom");

        final HierarchyEntry englandAndWales = new HierarchyEntry();
        englandAndWales.setId(UUID.randomUUID());
        englandAndWales.setName("England and Wales");
        englandAndWales.setLevelType(nextLevelType(ROOT_LEVEL));
        englandAndWales.setCode("EW");
        englandAndWales.setParent(uk);

        final HierarchyEntry england = new HierarchyEntry();
        england.setId(UUID.randomUUID());
        england.setName("England");
        england.setLevelType(nextLevelType(englandAndWales.getLevelType()));
        england.setCode("E");
        england.setParent(englandAndWales);

        return england;
    }
}