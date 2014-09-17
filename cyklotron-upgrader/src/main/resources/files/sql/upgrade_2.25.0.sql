-- cyklotron 2.25.0


insert 
  into coral_relation (relation_id, name) 
values 
  (nextval('coral_relation_seq'), 'structure.SiteDocs');

/* nie ma struktury tabelarycznej
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

*/ 
  
create or replace view locations_varea as
select
  lower(nazwa) nazwa,
  lower(nazwa) "województwo",
  null powiat,
  null gmina,
  null "miejscowość",
  trim(nazdod) typ,
  null rm,
  woj terc,
  null sym
from 
  locations_terc
where 
  pow is null
union all
select
  p.nazwa nazwa,
  lower(w.nazwa) "województwo",
  p.nazwa powiat,
  null gmina,
  null "miejscowość",
  trim(p.nazdod) typ,
  null rm,
  p.woj || p.pow terc,
  null sym
from 
  locations_terc p join 
  locations_terc w on (p.woj = w.woj)
where 
  w.pow is null and
  p.woj is not null and
  p.pow is not null and
  p.gmi is null
union all
select
  g.nazwa nazwa,
  lower(w.nazwa) "województwo",
  p.nazwa powiat,
  g.nazwa gmina,
  null "miejscowość",
  trim(g.nazdod) typ,
  null rm,
  p.woj || p.pow || trim(g.gmi) || g.rodz terc,
  null sym
from 
  locations_terc g join 
  locations_terc p on (g.woj = p.woj and g.pow = p.pow) join 
  locations_terc w on (p.woj = w.woj)
where 
  w.pow is null and
  p.woj is not null and
  p.pow is not null and
  p.gmi is null and
  g.woj is not null and
  g.pow is not null and
  g.gmi is not null and
  g.nazdod != 'miasto'
union all
select
  g.nazwa nazwa,
  lower(w.nazwa) "województwo",
  p.nazwa powiat,
  g.nazwa gmina,
  null "miejscowość",
  trim(g.nazdod) typ,
  null rm,
  p.woj || p.pow || trim(g.gmi) || g.rodz terc,
  null sym
from 
  locations_terc g join 
  locations_terc p on (g.woj = p.woj and g.pow = p.pow) join 
  locations_terc w on (p.woj = w.woj)
where 
  w.pow is null and
  p.woj is not null and
  p.pow is not null and
  p.gmi is null and
  g.woj is not null and
  g.pow is not null and
  g.gmi is not null and
  g.nazdod != 'miasto'
union all
select
  "miejscowość" nazwa,
  "województwo",
  powiat,
  gmina,
  "miejscowość",
  trim(nazwa_rm) typ,
  rm,
  woj || pow || trim(gmi) || rodz_gmi terc,
  sym
from
  locations_vsimc  
order by terc;
