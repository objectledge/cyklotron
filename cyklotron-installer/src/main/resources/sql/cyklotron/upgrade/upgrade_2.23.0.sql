CREATE OR REPLACE FUNCTION init_sequences() RETURNS INTEGER AS $$
DECLARE
	r RECORD;
        m BIGINT;
	n INTEGER := 1;
BEGIN
	FOR r IN
		SELECT i.table_name, 
		kc.column_name
		FROM ledge_id_table i, 
		information_schema.table_constraints tc,
		information_schema.key_column_usage kc
		WHERE tc.table_name = i.table_name
		AND tc.constraint_type = 'PRIMARY KEY'
		AND kc.constraint_name = tc.constraint_name
		AND kc.ordinal_position = 1
		UNION ALL 
		SELECT 'ledge_parameters', 'parameters_id'
		ORDER BY 1
	LOOP
		EXECUTE 'SELECT coalesce(max(' || r.column_name || '),0) + 1 FROM ' || r.table_name INTO m;		
		EXECUTE 'CREATE SEQUENCE ' || r.table_name || '_seq START WITH ' || m;
		n := n + 1;
	END LOOP;				
	RETURN n;
END; $$ LANGUAGE plpgsql;

SELECT init_sequences();

DROP FUNCTION init_sequences();

