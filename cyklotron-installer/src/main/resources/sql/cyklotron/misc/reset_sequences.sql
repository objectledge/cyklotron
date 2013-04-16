CREATE OR REPLACE FUNCTION reset_sequences() RETURNS INTEGER AS $$
DECLARE
	r RECORD;
        m BIGINT;
	n INTEGER := 1;
BEGIN
	FOR r IN
		SELECT tc.table_name, 
		kc.column_name
		FROM information_schema.sequences s, 
		information_schema.table_constraints tc,
		information_schema.key_column_usage kc
		WHERE tc.table_name = replace(s.sequence_name, '_seq', '')
		AND tc.constraint_type = 'PRIMARY KEY'
		AND kc.constraint_name = tc.constraint_name
		AND kc.ordinal_position = 1
		UNION ALL
		SELECT 'ledge_parameters', 'parameters_id'
		ORDER BY 1
	LOOP
		EXECUTE 'SELECT coalesce(max(' || r.column_name || '),0) FROM ' || r.table_name INTO m;
		EXECUTE 'DROP SEQUENCE ' || r.table_name || '_seq';
		EXECUTE 'CREATE SEQUENCE ' || r.table_name || '_seq START WITH ' || (m + 1);
		n := n + 1;
	END LOOP;				
	RETURN n;
END; $$ LANGUAGE plpgsql;

SELECT reset_sequences();
