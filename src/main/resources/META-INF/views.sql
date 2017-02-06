-- Views required by the metadata API domain model mapping
-- For some unfathomable reason, EclipseLink requires all statements to be on a single line. I kid you not.

CREATE OR REPLACE VIEW dimension(dimensional_data_set_id, name) AS SELECT DISTINCT dv.dimensional_data_set_id, dv."name" AS "name" FROM dimension_value dv;