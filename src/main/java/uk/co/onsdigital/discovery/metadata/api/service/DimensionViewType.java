package uk.co.onsdigital.discovery.metadata.api.service;

import uk.co.onsdigital.discovery.metadata.api.legacy.dto.DimensionOption;
import uk.co.onsdigital.discovery.model.DimensionValue;
import uk.co.onsdigital.discovery.model.HierarchyEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Indicates the type of view to use when rendering dimension options: either a flat list or (sparse) hierarchy.
 */
public enum DimensionViewType {
    /**
     * A flat list of the dimension options, ignoring any hierarchy.
     */
    LIST {
        @Override
        List<DimensionOption> convertValues(List<DimensionValue> values) {
            return values.stream()
                    .map(DimensionViewType::convertValueToOption)
                    .sorted()
                    .collect(toList());
        }
    },
    /**
     * A sparse hierarchical view of the dimension according to the referenced hierarchy. If no hierarchy
     * is defined for this dimension, then the result will be the same as the flat list view.
     */
    HIERARCHY {
        @Override
        List<DimensionOption> convertValues(List<DimensionValue> values) {
            // Map from hierarchy entry IDs to corresponding dimension value IDs
            final Map<UUID, DimensionValue> valuesByHierarchyEntryId = values.stream()
                    .filter(value -> value.getHierarchyEntry() != null)
                    .collect(toMap(value -> value.getHierarchyEntry().getId(), Function.identity()));

            // Map from dimension value IDs to the created dimension option, for de-duplication
            final Map<UUID, DimensionOption> options = new LinkedHashMap<>();
            // Set to collect the top-level elements in the hierarchy
            final Set<DimensionOption> roots = new LinkedHashSet<>();

            for (DimensionValue value : values) {
                DimensionOption option = option(options, value, value.getHierarchyEntry());
                boolean isNewEntry = true;

                // Walk up the hierarchy creating intermediate levels as required.
                // If an entry in the hierarchy does not exist in the dataset then we create an "empty" level
                // that exists only for structure. These empty levels have a null id.
                if (value.getHierarchyEntry() != null) {
                    for (HierarchyEntry parentEntry = value.getHierarchyEntry().getParent();
                         parentEntry != null && isNewEntry;
                         parentEntry = parentEntry.getParent()) {
                        final DimensionValue parentValue = valuesByHierarchyEntryId.get(parentEntry.getId());
                        final DimensionOption parent = option(options, parentValue, parentEntry);

                        isNewEntry = parent.addChild(option);
                        option = parent;
                    }
                }

                // Either this is a flat dimension or we have walked to the top of the hierarchy without encountering an existing option
                if (isNewEntry) {
                    roots.add(option);
                }
            }

            return new ArrayList<>(roots);
        }

        private DimensionOption option(Map<UUID, DimensionOption> options, DimensionValue value, HierarchyEntry entry) {
            if (entry != null) {
                return options.computeIfAbsent(entry.getId(), id -> DimensionViewType.convertEntryToOption(value, entry));
            } else {
                return DimensionViewType.convertEntryToOption(value, null);
            }
        }
    },
    /**
     * Do not render the dimension values at all.
     */
    NONE {
        @Override
        List<DimensionOption> convertValues(List<DimensionValue> values) {
            return null;
        }
    };

    /**
     * Convert the raw dimension values from the database into a list of options to be rendered.
     */
    abstract List<DimensionOption> convertValues(List<DimensionValue> values);


    /**
     * Converts a dimension value into a dimension option. If the value is hierarchical then the returned
     * option will include the hierarchical entry code and name, otherwise it will use the raw dimension value.
     *
     * @param dimensionValue the value to convert.
     * @return the equivalent option.
     */
    private static DimensionOption convertValueToOption(final DimensionValue dimensionValue) {
        return convertEntryToOption(dimensionValue, dimensionValue.getHierarchyEntry());
    }

    private static DimensionOption convertEntryToOption(final DimensionValue dimensionValue, final HierarchyEntry hierarchyEntry) {
        if (hierarchyEntry != null) {
            final UUID id = dimensionValue != null ? dimensionValue.getId() : null;
            return new DimensionOption(id, hierarchyEntry.getCode(), hierarchyEntry.getName(), hierarchyEntry.getLevelType());
        } else {
            return new DimensionOption(dimensionValue.getId(), dimensionValue.getValue());
        }

    }
}
