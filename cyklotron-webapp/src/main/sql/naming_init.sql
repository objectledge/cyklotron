DELETE FROM ledge_naming_attribute;
DELETE FROM ledge_naming_context;

INSERT INTO ledge_naming_context VALUES (1,'',-1);
INSERT INTO ledge_naming_context VALUES (2,'ou=people,dc=ngo,dc=pl',1);
INSERT INTO ledge_naming_context VALUES (10,'uid=root,ou=people,dc=ngo,dc=pl',2);
INSERT INTO ledge_naming_context VALUES (12,'uid=anonymous,ou=people,dc=ngo,dc=pl',2);

INSERT INTO ledge_naming_attribute VALUES (10,'objectClass','uidObject');
INSERT INTO ledge_naming_attribute VALUES (10,'objectClass','simpleSecurityObject');
INSERT INTO ledge_naming_attribute VALUES (10,'uid','root');
INSERT INTO ledge_naming_attribute VALUES (10,'userPassword','{md5}gnzLDuqKcGxMNKFokfhOew==');

INSERT INTO ledge_naming_attribute VALUES (12,'objectClass','uidObject');
INSERT INTO ledge_naming_attribute VALUES (12,'objectClass','simpleSecurityObject');
INSERT INTO ledge_naming_attribute VALUES (12,'uid','anonymous');
INSERT INTO ledge_naming_attribute VALUES (12,'userPassword','{md5}gnzLDuqKcGxMNKFokfhOew==');

DELETE FROM ledge_id_table WHERE table_name = 'ledge_naming_context';
INSERT INTO ledge_id_table VALUES (15,'ledge_naming_context');

