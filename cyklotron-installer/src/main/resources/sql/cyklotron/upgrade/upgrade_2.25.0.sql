insert 
  into coral_relation (relation_id, name) 
values 
  (nextval('coral_relation_seq'), 'structure.SiteDocs');

insert into 
  coral_relation_data (relation_id, resource1, resource2)
select
  rl.relation_id,
  ar.ref site,
  r.resource_id node
from coral_resource_class src,
  coral_resource_class drc,
  coral_attribute_definition ad,
  coral_resource r,
  coral_generic_resource g,
  coral_attribute_resource ar,
  coral_relation rl
where src.name = 'structure.navigation_node'
  and drc.name = 'documents.document_node'
  and ad.resource_class_id = src.resource_class_id
  and ad.name = 'site'
  and r.resource_class_id = drc.resource_class_id
  and g.resource_id = r.resource_id
  and g.attribute_definition_id = ad.attribute_definition_id
  and ar.data_key = g.data_key
  and rl.name = 'structure.SiteDocs';

insert into 
  coral_relation_data (relation_id, resource1, resource2)
select
  rl.relation_id,
  n.site,
  n.resource_id node
from 
  structure_navigation_node n,
  coral_relation rl
where 
  rl.name = 'structure.SiteDocs';
