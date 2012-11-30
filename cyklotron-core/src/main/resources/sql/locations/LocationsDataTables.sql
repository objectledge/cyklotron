-- 
-- Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
-- All rights reserved. 
-- 
-- Redistribution and use in source and binary forms, with or without modification,  
-- are permitted provided that the following conditions are met: 
-- 
-- * Redistributions of source code must retain the above copyright notice,  
--   this list of conditions and the following disclaimer. 
-- * Redistributions in binary form must reproduce the above copyright notice,  
--   this list of conditions and the following disclaimer in the documentation  
--   and/or other materials provided with the distribution. 
-- * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
--   nor the names of its contributors may be used to endorse or promote products  
--   derived from this software without specific prior written permission. 
-- 
-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
-- AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
-- WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
-- IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
-- INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
-- BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
-- OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
-- WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
-- ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
-- POSSIBILITY OF SUCH DAMAGE. 
-- 

--
-- Locations data schema
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


CREATE TABLE locations_ulic (
    woj character(2),
    pow character(2),
    gmi character(3),
    rodz_gmi character(1),
    sym character(7),
    sym_ul character(5),
    cecha character varying,
    nazwa_1 character varying,
    nazwa_2 character varying,
    stan_na date
);


CREATE TABLE locations_wmrodz (
    rm character(2),
    nazwa_rm character varying,
    stan_na date
);


CREATE VIEW locations_vterc AS
    SELECT g.woj, g.pow, g.gmi, g.rodz, w.nazwa AS "województwo", p.nazwa AS powiat, g.nazwa AS gmina, g.nazdod AS typ FROM (SELECT terc.woj, terc.pow, terc.gmi, terc.rodz, terc.nazwa, terc.nazdod FROM locations_terc AS terc WHERE (terc.gmi IS NOT NULL)) g, (SELECT terc.woj, terc.pow, terc.nazwa FROM locations_terc as terc WHERE ((terc.pow IS NOT NULL) AND (terc.gmi IS NULL))) p, (SELECT terc.woj, lower((terc.nazwa)::text) AS nazwa FROM locations_terc as terc WHERE (terc.pow IS NULL)) w WHERE (((g.woj = w.woj) AND (g.pow = p.pow)) AND (p.woj = g.woj)) ORDER BY g.woj;


CREATE VIEW locations_vsimc AS
    SELECT t."województwo", t.powiat, CASE WHEN (s.rm = ANY (ARRAY['95'::bpchar, '98'::bpchar])) THEN t.powiat ELSE t.gmina END AS gmina, CASE WHEN (s.sym = s.sympod) THEN (s.nazwa)::text ELSE ((((sp.nazwa)::text || ' ('::text) || (s.nazwa)::text) || ')'::text) END AS "miejscowość", CASE WHEN (s.rm = ANY (ARRAY['95'::bpchar, '98'::bpchar])) THEN t.powiat ELSE sp.nazwa END AS nazwa_pod, CASE WHEN (s.rm = ANY (ARRAY['95'::bpchar, '98'::bpchar])) THEN t.gmina ELSE s.nazwa END AS nazwa, r.nazwa_rm, s.sym, s.woj, s.pow, s.gmi, s.rodz_gmi, s.rm FROM locations_simc s, locations_simc sp, locations_vterc t, locations_wmrodz r WHERE ((((((s.woj = t.woj) AND (s.pow = t.pow)) AND (s.gmi = t.gmi)) AND (s.rodz_gmi = t.rodz)) AND (sp.sym = s.sympod)) AND (r.rm = s.rm)) ORDER BY s.woj, s.pow, s.gmi, s.rodz_gmi, sp.nazwa, CASE WHEN (s.sym = s.sympod) THEN 0 ELSE 1 END, s.nazwa;

 
CREATE VIEW locations_vpna AS
    SELECT DISTINCT b.pna, b."miejscowość", b.ulica, b.numery, b.gmina, b.powiat, b."województwo", b.nazwa, b.nazwa_pod, b.nazwa_rm, b.s_nazwa_pod, b.s_nazwa, b.s_nazwa_rm, b.woj, b.pow, b.gmi, b.rodz_gmi, b.sym, b.score, b.rank FROM (SELECT a.pna, a."miejscowość", a.ulica, a.numery, a.gmina, a.powiat, a."województwo", a.nazwa, a.nazwa_pod, a.nazwa_rm, a.s_nazwa_pod, a.s_nazwa, a.s_nazwa_rm, a.woj, a.pow, a.gmi, a.rodz_gmi, a.sym, a.score, rank() OVER (PARTITION BY a."województwo", a.powiat, a.gmina, a.nazwa_pod, a.nazwa, a.nazwa_rm ORDER BY a.score, a.sym) AS rank FROM (SELECT p.pna, p."miejscowość", p.ulica, p.numery, p.gmina, p.powiat, p."województwo", p.nazwa, p.nazwa_pod, p.nazwa_rm, s.nazwa_pod AS s_nazwa_pod, s.nazwa AS s_nazwa, s.nazwa_rm AS s_nazwa_rm, s.woj, s.pow, s.gmi, s.rodz_gmi, s.sym, CASE WHEN ((((p.nazwa)::text = (s.nazwa)::text) AND ((p.nazwa_pod)::text = (s.nazwa_pod)::text)) AND (p.nazwa_rm IS NULL)) THEN 1 WHEN (((p.nazwa)::text = (s.nazwa)::text) AND ((p.nazwa_rm)::text = btrim((s.nazwa_rm)::text))) THEN 2 WHEN ((replace(replace((p.nazwa)::text, '-'::text, ' '::text), ''''::text, ''''''::text) = replace((s.nazwa)::text, '-'::text, ' '::text)) AND (replace(replace((p.nazwa_pod)::text, '-'::text, ' '::text), ''''::text, ''''''::text) = replace((s.nazwa_pod)::text, '-'::text, ' '::text))) THEN 3 WHEN ((p.nazwa)::text = (s.nazwa)::text) THEN 4 WHEN (replace(replace((p.nazwa)::text, '-'::text, ' '::text), ''''::text, ''''''::text) = replace((s.nazwa)::text, '-'::text, ' '::text)) THEN 5 WHEN (((p.nazwa)::text <> (s.nazwa)::text) AND ((s.nazwa)::text = (s.gmina)::text)) THEN 6 ELSE 7 END AS score FROM (locations_pna p LEFT JOIN locations_vsimc s ON ((((((p."województwo")::text = s."województwo") AND ((p.powiat)::text = (s.powiat)::text)) AND ((p.gmina)::text = (s.gmina)::text)) AND ((replace(replace((p.nazwa)::text, '-'::text, ' '::text), ''''::text, ''''''::text) = replace((s.nazwa)::text, '-'::text, ' '::text)) OR ((s.nazwa)::text = (s.gmina)::text)))))) a) b WHERE (b.rank = 1);

