/*
 * Oryginal file name cyklo_2.22.0_coral_update.sql
 */

-- coral_attribute_definition

ALTER TABLE coral_attribute_definition ADD COLUMN db_column VARCHAR(255);

-- coral_attribute_mapping

CREATE TABLE coral_attribute_mapping (
attribute_class_id BIGINT NOT NULL,
is_fk boolean NOT NULL,
sql_type VARCHAR(32) NOT NULL,
fk_table VARCHAR(64),
fk_key_column VARCHAR(64),
is_custom boolean
);

INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type) 
VALUES(1, false, 'VARCHAR(255)');
INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type) 
VALUES(2, false, 'VARCHAR');
INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type) 
VALUES(3, false, 'BOOLEAN');
INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type) 
VALUES(4, false, 'INTEGER');
INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type) 
VALUES(5, false, 'BIGINT');
INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type) 
VALUES(6, false, 'DECIMAL');
INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type) 
VALUES(7, false, 'TIMESTAMP');

INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type, fk_table, fk_key_column, is_custom) 
VALUES(8, true, 'BIGINT', 'coral_resource_class', 'resource_class_id', false);
INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type, fk_table, fk_key_column, is_custom) 
VALUES(9, true, 'BIGINT', 'coral_resource', 'resource_id', false);
INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type, fk_table, fk_key_column, is_custom) 
VALUES(10, true, 'BIGINT', 'coral_subject', 'subject_id', false);
INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type, fk_table, fk_key_column, is_custom) 
VALUES(11, true, 'BIGINT', 'coral_role', 'role_id', false);
INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type, fk_table, fk_key_column, is_custom) 
VALUES(12, true, 'BIGINT', 'coral_permission', 'permission_id', false);

INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type, fk_table, fk_key_column, is_custom) 
VALUES(13, true, 'BIGINT', 'coral_attribute_date_range', 'data_key', true);
INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type, fk_table, fk_key_column, is_custom) 
VALUES(14, false, 'BIGINT', 'ledge_parameters', 'parameters_id', true);
INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type, fk_table, fk_key_column, is_custom) 
VALUES(15, false, 'BIGINT', 'coral_attribute_resource_list', 'data_key', true);
INSERT INTO coral_attribute_mapping (attribute_class_id, is_fk, sql_type, fk_table, fk_key_column, is_custom) 
VALUES(16, false, 'BIGINT', 'coral_attribute_weak_resource_list', 'data_key', true);

-- db_table_names

UPDATE coral_attribute_class set db_table_name = 'ledge_parameters' WHERE name = 'parameters';

UPDATE coral_resource_class SET db_table_name = NULL WHERE db_table_name = '';

-- real BOOELANs

ALTER TABLE coral_permission_assignment ADD COLUMN is_interited_b BOOLEAN;
UPDATE coral_permission_assignment SET is_interited_b = (is_inherited = '1');
ALTER TABLE coral_permission_assignment DROP COLUMN is_inherited;
ALTER TABLE coral_permission_assignment RENAME COLUMN is_interited_b TO is_inherited;

ALTER TABLE coral_role_assignment ADD COLUMN granting_allowed_b BOOLEAN;
UPDATE coral_role_assignment SET granting_allowed_b = (granting_allowed = '1');
ALTER TABLE coral_role_assignment DROP COLUMN granting_allowed;
ALTER TABLE coral_role_assignment RENAME COLUMN granting_allowed_b TO granting_allowed;

ALTER TABLE coral_attribute_boolean ADD COLUMN data_b BOOLEAN;
UPDATE coral_attribute_boolean SET data_b = (data = '1');
ALTER TABLE coral_attribute_boolean DROP COLUMN data;
ALTER TABLE coral_attribute_boolean RENAME COLUMN data_b TO data;
            
-- ledge_parameters

--ALTER TABLE ledge_parameters DROP COLUMN id;
--DROP SEQUENCE ledge_parameters_seq;
CREATE INDEX ledge_parameters_id_idx ON ledge_parameters(parameters_id);
