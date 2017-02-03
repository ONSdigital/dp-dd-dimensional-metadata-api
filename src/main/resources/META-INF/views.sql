-- Views required by the metadata API domain model mapping
-- For some unfathomable reason, EclipseLink requires all statements to be on a single line. I kid you not.

CREATE OR REPLACE VIEW dimension(dimensional_data_set_id, dimension_name, hierarchy_id) AS SELECT DISTINCT dv.dimensional_data_set_id, dv."name" AS dimension_name, he.hierarchy_id FROM dimension_value dv LEFT OUTER JOIN hierarchy_entry he ON (dv.hierarchy_entry_id = he.id);

CREATE OR REPLACE VIEW dimension_option(id, hierarchy_entry_id, dimensional_data_set_id, dimension_name, dimension_value, parent_id, level_type_id) AS SELECT dv.id, dv.hierarchy_entry_id, dv.dimensional_data_set_id, COALESCE(he."name", dv."name") AS dimension_name, dv.value AS dimension_value, he.parent AS parent_id, he.hierarchy_level_type_id AS level_type_id FROM dimension_value dv LEFT OUTER JOIN hierarchy_entry he ON (dv.hierarchy_entry_id = he.id);