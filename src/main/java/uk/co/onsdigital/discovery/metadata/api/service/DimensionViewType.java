package uk.co.onsdigital.discovery.metadata.api.service;

import uk.co.onsdigital.discovery.metadata.api.model.DimensionOption;
import uk.co.onsdigital.discovery.model.DimensionValue;
import uk.co.onsdigital.discovery.model.HierarchyEntry;

import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

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
                    .sorted(comparing(DimensionOption::getName))
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
            // TODO: implement sparse hierarchy support
            throw new UnsupportedOperationException("Hierarchical dimension view not yet implemented.");
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
        final HierarchyEntry hierarchyEntry = dimensionValue.getHierarchyEntry();
        if (hierarchyEntry != null) {
            return new DimensionOption(dimensionValue.getId(), hierarchyEntry.getCode(), hierarchyEntry.getName());
        } else {
            return new DimensionOption(dimensionValue.getId(), dimensionValue.getValue());
        }
    }
}
