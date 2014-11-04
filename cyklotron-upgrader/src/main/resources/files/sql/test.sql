DROP TABLE IF EXISTS upgrade_test;

CREATE TABLE upgrade_test(
id integer not null,
name character varying(255)
);

INSERT INTO upgrade_test(id, name)
values(1,'X');

UPDATE upgrade_test 
SET name='Y' 
WHERE id = 1;

DELETE FROM upgrade_test;

DROP TABLE IF EXISTS upgrade_test;
