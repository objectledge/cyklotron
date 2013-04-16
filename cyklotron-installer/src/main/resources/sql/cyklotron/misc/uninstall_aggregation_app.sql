\t
\a
\o agregacja.rml
select '# odpięcie aplikacji od serwisów';
select 'ALTER RELATION integration.SiteApplications DELETE REVERSE ''/cms/applications/aggregation'';';

select '# zasób aplikacji w rejestrze';
select 'DELETE RESOURCE ''/cms/applications/aggregation'';';

select '# zasoby cms.aggregation.root';
select 'DELETE RESOURCE ' || r.resource_id || ' RECURSIVE;' from coral_resource r, coral_resource_class rc where rc.name = 'cms.aggregation.root' and r.resource_class_id = rc.resource_class_id;

select '# granty uprawnień';
select 'REVOKE PERMISSION ''cms.aggregation.import'' ON ' ||  pa.resource_id || ' FROM ' || pa.role_id || ';' from coral_permission_assignment pa where permission_id = 56;
select 'REVOKE PERMISSION ''cms.aggregation.export'' ON ' ||  pa.resource_id || ' FROM ' || pa.role_id || ';' from coral_permission_assignment pa where permission_id = 57;

select '# odpięcie uprawnień od klas';
select 'ALTER RESOURCE CLASS ''site.site'' DELETE PERMISSIONS ( ''cms.aggregation.import'', ''cms.aggregation.export'');';
select 'ALTER RESOURCE CLASS ''node'' DELETE PERMISSIONS ( ''cms.aggregation.import'');';

select '# uprawnienia';
select 'DELETE PERMISSION ''cms.aggregation.import'';';
select 'DELETE PERMISSION ''cms.aggregation.export'';';

select '# klasy zasobów';
select 'DELETE RESOURCE CLASS ''cms.aggregation.root'';';
select 'DELETE RESOURCE CLASS ''cms.aggregation.import'';';
select 'DELETE RESOURCE CLASS ''cms.aggregation.recommendation'';';
select 'DELETE RESOURCE CLASS ''cms.aggregation.recommendation_comment'';';

select '# granty ról';
select 'REVOKE ROLE ' || r.role_id || ' FROM ' || subject_id || ';' from coral_role r, coral_role_assignment ra where r.name ~ '\.aggregation\.' and ra.role_id = r.role_id;

select '# role nadrzędne';
select 'ALTER ROLE ' || ri.super_role || ' DELETE SUBROLES (' || ri.sub_role || ');' from coral_role_implication ri, coral_role r where r.name ~ '\.aggregation\.' and ri.sub_role = r.role_id;

select '# odniesienia do ról';
select 'DELETE RESOURCE ' || rr.resource_id || ';' from coral_role r, coral_attribute_role ar, coral_generic_resource g, coral_attribute_definition ad, coral_resource rr where r.name ~ '\.aggregation\.' and ar.ref = r.role_id and g.data_key = ar.data_key and g.attribute_definition_id = ad.attribute_definition_id and ad.attribute_class_id = 11 and rr.resource_id = g.resource_id;

select '# role ';
select 'DELETE ROLE ' || r.role_id || ';' from coral_role r where r.name ~ '\.aggregation\.';

select '# definicje komponentów';
select 'ALTER RESOURCE CLASS ''integration.component'' DELETE ATTRIBUTE ''aggregationSourceView'';';

