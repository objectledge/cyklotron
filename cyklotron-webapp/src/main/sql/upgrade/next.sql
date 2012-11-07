update coral_resource_class 
set db_table_name = 'coral_node' 
where resource_class_id = 1;

delete from coral_attribute_definition 
where resource_class_id = 2;

delete from coral_resource_class 
where resource_class_id = 2;

update coral_resource_class 
set db_table_name = replace(name, '.', '_') 
where db_table_name is null and name ~ '^cms';

update coral_resource_class 
set db_table_name = 'cms_' || replace(name, '.', '_') 
where db_table_name is null and not name ~ '^cms';
