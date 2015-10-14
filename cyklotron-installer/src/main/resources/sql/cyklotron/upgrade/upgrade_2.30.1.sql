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
    SELECT terc.woj, terc.pow, terc.gmi, terc.rodz, terc.nazwa, terc.nazdod
    FROM locations_terc terc
   WHERE terc.gmi IS NOT NULL
    AND terc.rodz NOT IN ('4', '5') 
  ) g, ( 
    SELECT terc.woj, terc.pow, terc.nazwa
    FROM locations_terc terc
    WHERE terc.pow IS NOT NULL 
    AND terc.gmi IS NULL 
  ) p, ( 
    SELECT terc.woj, lower(terc.nazwa::text) AS nazwa
    FROM locations_terc terc
    WHERE terc.pow IS NULL 
  ) w
WHERE g.woj = w.woj AND g.pow = p.pow AND p.woj = g.woj
ORDER BY g.woj, g.pow, g.gmi;

CREATE OR REPLACE VIEW locations_vsimc AS
SELECT   t."województwo", 
         t.powiat, 
         CASE 
             WHEN s.rm = '95' THEN 'Warszawa-' || t.gmina
             ELSE t.gmina
         END AS gmina, 
         CASE 
             WHEN s.rm = '95' THEN 'Warszawa-' || s.nazwa 
             ELSE s.nazwa 
         END AS "miejscowość", 
         r.nazwa_rm, s.sym, s.woj, s.pow, s.gmi, s.rodz_gmi, s.rm
    FROM locations_simc s, 
         locations_vterc t, 
         locations_wmrodz r
   WHERE s.woj = t.woj 
     AND s.pow = t.pow 
     AND s.gmi = t.gmi 
     AND s.sym = s.sympod 
     AND r.rm = s.rm
     AND s.sym != '0918123' -- miasto Warszawa (bez wskazania dzielnicy)
ORDER BY s.woj, 
         s.pow, 
         s.gmi, 
         s.rodz_gmi, 
         s.nazwa;

CREATE OR REPLACE VIEW locations_varea AS
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
  p.gmi is null and
  (substring(p.pow, 1, 1) not in ('6', '7') -- miasta na prawach powiatu za wyjątkiem Warszawy
    or p.woj = '14' and p.pow = '65')
union all
select
  CASE 
    WHEN g.rodz = '8' THEN 'Warszawa-' || g.nazwa
    ELSE g.nazwa
  END nazwa,
  lower(w.nazwa) "województwo",
  p.nazwa powiat,
  g.nazwa gmina,
  null "miejscowość",
  'gmina' typ,
  null rm,
  p.woj || p.pow || trim(g.gmi) terc,
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
  g.rodz not in ('1', '4', '5') and
  substring(g.pow, 1, 1) not in ('6', '7') 
union all
select
  "miejscowość" nazwa,
  "województwo",
  powiat,
  gmina,
  "miejscowość",
  CASE 
    WHEN rm IN ('95') THEN 'dzielnica'
    ELSE trim(nazwa_rm) 
  END typ,
  rm,
  woj || pow || trim(gmi) terc,
  sym
from
  locations_vsimc
where
  rm NOT IN ('00', '03', '05', '06', '07', '98')
order by terc, sym NULLS FIRST;

CREATE OR REPLACE VIEW locations_vpna AS
  SELECT DISTINCT 
    b.pna, 
    b.ulica, 
    b."miejscowość", 
    b.gmina, 
    b.powiat, 
    b."województwo", 
    b.typ,
    b.rm,
    b.woj || b.pow || b.gmi terc, 
    b.sym, 
    b.score
  FROM (
    SELECT 
      a.pna, 
      a."miejscowość", 
      a.ulica, 
      a.gmina, 
      a.powiat, 
      a."województwo", 
      a.typ,
      a.rm,
      a.woj, 
      a.pow, 
      a.gmi, 
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
        a.rm_rank,
        a.sym
      ) AS rank 
    FROM (
      SELECT 
        p.pna,
        CASE WHEN p."miejscowość" = 'skrytki/przegródki' THEN p.gmina
        ELSE s."miejscowość" END AS "miejscowość", 
        p.ulica, 
        s.gmina, 
        p.powiat, 
        p."województwo",
        CASE 
          WHEN s.rm IN ('95') THEN 'dzielnica'
          ELSE trim(s.nazwa_rm) 
        END typ, 
        p.nazwa, 
        p.nazwa_pod, 
        p.nazwa_rm, 
        s.miejscowość AS s_nazwa_pod, 
        ''::text AS s_nazwa, 
        s.nazwa_rm AS s_nazwa_rm, 
        s.woj, 
        s.pow, 
        s.gmi, 
        s.rodz_gmi, 
        s.sym, 
        s.rm,
        CASE 
          WHEN s.rm = '96' THEN 1
          WHEN s.rm = '01' THEN 2
          WHEN s.rm = '02' THEN 3
          WHEN s.rm = '04' THEN 4
          ELSE 5
        END rm_rank,
        CASE 
          WHEN (p.nazwa_pod = s.miejscowość
            AND p.nazwa_rm IS NULL
          ) THEN 1
          WHEN (p.nazwa_pod = 'Warszawa'
            AND 'Warszawa-' || p.nazwa = s."miejscowość"
          ) THEN 2
          WHEN (p.nazwa_pod = 'skrytki/przegródki' and p.gmina = s.miejscowość
          ) THEN 3           
          WHEN ( p.gmina = p."miejscowość"
          ) THEN 4
          ELSE 9 
        END AS score 
      FROM
        locations_pna p 
        LEFT JOIN locations_vsimc s 
        ON (p."województwo" = s."województwo" 
          AND p.powiat = s.powiat 
          AND (
            p.gmina = s.gmina 
            OR 'Warszawa-' || p.nazwa = s.gmina
          ) 
          AND (
            p.nazwa_pod = s.miejscowość
            OR p.nazwa_pod = 'Warszawa' AND 'Warszawa-' || p.nazwa = s."miejscowość"
            OR p.nazwa_pod = 'skrytki/przegródki' and s.miejscowość = p.gmina
            OR p.gmina  = p."miejscowość"
          )
        )
      ) a
    ) b 
  WHERE b.rank = 1
  AND b.rm NOT IN ('00', '03', '05', '06', '07', '98') OR b.miejscowość = b.gmina;
