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
  
--
-- PNA + TERC Locations
--

CREATE TABLE locations_pna (
  pna character(6),
  "miejscowość" character varying,
  ulica character varying,
  numery character varying,
  gmina character varying,
  powiat character varying,
  "województwo" character varying,
  nazwa character varying,
  nazwa_pod character varying,
  nazwa_rm character varying
);


CREATE TABLE locations_simc (
  woj character(2),
  pow character(2),
  gmi character(3),
  rodz_gmi character(1),
  rm character(2),
  mz character(1),
  nazwa character varying,
  sym character(7),
  sympod character(7),
  stan_na date
);


CREATE TABLE locations_terc (
  woj character(2),
  pow character(2),
  gmi character(3),
  rodz character(1),
  nazwa character varying,
  nazdod character varying,
  stan_na date
);


CREATE TABLE locations_wmrodz (
  rm character(2),
  nazwa_rm character varying,
  stan_na date
);


CREATE OR REPLACE VIEW locations_vterc AS
  SELECT 
    g.woj, 
    g.pow, 
    g.gmi, 
    g.rodz, 
    w.nazwa AS "województwo", 
    p.nazwa AS powiat, 
    g.nazwa AS gmina, 
    g.nazdod AS typ 
  FROM (
    SELECT 
      terc.woj, 
      terc.pow, 
      terc.gmi, 
      terc.rodz, 
      terc.nazwa, 
      terc.nazdod 
    FROM 
      locations_terc AS terc 
    WHERE terc.gmi IS NOT NULL
  ) g, (
    SELECT 
      terc.woj, 
      terc.pow, 
      terc.nazwa 
    FROM 
      locations_terc AS terc 
    WHERE terc.pow IS NOT NULL 
      AND terc.gmi IS NULL
  ) p, (
   SELECT 
     terc.woj, 
     lower((terc.nazwa)) AS nazwa 
   FROM 
     locations_terc AS terc 
   WHERE terc.pow IS NULL
  ) w 
  WHERE g.woj = w.woj 
    AND g.pow = p.pow 
    AND p.woj = g.woj
  ORDER BY 
    g.woj;


CREATE OR REPLACE VIEW locations_vsimc AS
  SELECT 
    t."województwo", 
    t.powiat, 
    CASE 
      WHEN (s.rm IN ('95', '98')) THEN t.powiat 
      ELSE t.gmina 
    END AS gmina, 
    CASE 
      WHEN (s.sym = s.sympod) THEN s.nazwa 
      ELSE sp.nazwa || ' (' || s.nazwa || ')' 
    END AS "miejscowość", 
    CASE 
      WHEN (s.rm IN ('95', '98')) THEN t.powiat 
      ELSE sp.nazwa 
    END AS nazwa_pod, 
    CASE 
      WHEN (s.rm IN ('95', '98')) THEN t.gmina 
      ELSE s.nazwa 
    END AS nazwa, 
    r.nazwa_rm, 
    s.sym, 
    s.woj, 
    s.pow, 
    s.gmi, 
    s.rodz_gmi, 
    s.rm 
  FROM locations_simc s, 
    locations_simc sp, 
    locations_vterc t, 
    locations_wmrodz r 
  WHERE s.woj = t.woj 
    AND s.pow = t.pow 
    AND s.gmi = t.gmi 
    AND s.rodz_gmi = t.rodz 
    AND sp.sym = s.sympod 
    AND r.rm = s.rm
  ORDER BY s.woj, 
    s.pow, 
    s.gmi, 
    s.rodz_gmi, 
    sp.nazwa, 
    CASE 
      WHEN (s.sym = s.sympod) THEN 0 
      ELSE 1 
    END, 
    s.nazwa;

CREATE OR REPLACE VIEW locations_vpna AS
  SELECT DISTINCT 
    b.pna, 
    b."miejscowość", 
    b.ulica, 
    b.numery, 
    b.gmina, 
    b.powiat, 
    b."województwo", 
    b.nazwa, 
    b.nazwa_pod, 
    b.nazwa_rm, 
    b.s_nazwa_pod, 
    b.s_nazwa, 
    b.s_nazwa_rm, 
    b.woj, 
    b.pow, 
    b.gmi, 
    b.rodz_gmi, 
    b.sym, 
    b.score, 
    b.rank 
  FROM (
    SELECT 
      a.pna, 
      a."miejscowość", 
      a.ulica, 
      a.numery, 
      a.gmina, 
      a.powiat, 
      a."województwo", 
      a.nazwa, 
      a.nazwa_pod, 
      a.nazwa_rm, 
      a.s_nazwa_pod, 
      a.s_nazwa, 
      a.s_nazwa_rm, 
      a.woj, 
      a.pow, 
      a.gmi, 
      a.rodz_gmi, 
      a.sym, 
      a.score, 
      rank() OVER (PARTITION BY 
        a."województwo", 
        a.powiat, 
        a.gmina, 
        a.nazwa_pod, 
        a.nazwa, 
        a.nazwa_rm 
      ORDER BY 
        a.score, 
        a.sym
      ) AS rank 
    FROM (
      SELECT 
        p.pna, 
        p."miejscowość", 
        p.ulica, 
        p.numery, 
        p.gmina, 
        p.powiat, 
        p."województwo", 
        p.nazwa, 
        p.nazwa_pod, 
        p.nazwa_rm, 
        s.nazwa_pod AS s_nazwa_pod, 
        s.nazwa AS s_nazwa, 
        s.nazwa_rm AS s_nazwa_rm, 
        s.woj, 
        s.pow, 
        s.gmi, 
        s.rodz_gmi, 
        s.sym, 
        CASE 
          WHEN (p.nazwa = s.nazwa 
            AND p.nazwa_pod = s.nazwa_pod 
            AND p.nazwa_rm IS NULL
          ) THEN 1 
          WHEN (p.nazwa = s.nazwa 
            AND p.nazwa_rm = btrim(s.nazwa_rm)
          ) THEN 2 
          WHEN (replace(replace((p.nazwa), '-', ' '), '''', '''''') = replace((s.nazwa), '-', ' ') 
            AND replace(replace((p.nazwa_pod), '-', ' '), '''', '''''') = replace((s.nazwa_pod), '-', ' ')
          ) THEN 3 
          WHEN (p.nazwa = s.nazwa) THEN 4 
          WHEN (replace(replace(p.nazwa, '-', ' '), '''', '''''') = replace(s.nazwa, '-', ' ')) THEN 5 
          WHEN (p.nazwa <> s.nazwa 
            AND s.nazwa = s.gmina
          ) THEN 6 
          ELSE 7 
        END AS score 
      FROM
        locations_pna p 
        LEFT JOIN locations_vsimc s 
        ON (p."województwo" = s."województwo" 
          AND p.powiat = s.powiat 
          AND p.gmina = s.gmina 
          AND (
            replace(replace(p.nazwa, '-', ' '), '''', '''''') = replace(s.nazwa, '-', ' ') 
            OR s.nazwa = s.gmina
         )
        )
      ) a
    ) b 
  WHERE b.rank = 1;


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

  
