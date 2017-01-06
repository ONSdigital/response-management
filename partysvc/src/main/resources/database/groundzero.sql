REVOKE ALL PRIVILEGES ON DATABASE postgres FROM partysvc;
DROP SCHEMA IF EXISTS party CASCADE;
DROP ROLE IF EXISTS partysvc;

CREATE ROLE partysvc PASSWORD 'partysvc' NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION INHERIT LOGIN;

CREATE SCHEMA party AUTHORIZATION partysvc;

REVOKE ALL ON ALL TABLES IN SCHEMA party FROM PUBLIC;
REVOKE ALL ON ALL SEQUENCES IN SCHEMA party FROM PUBLIC;
REVOKE CONNECT ON DATABASE postgres FROM PUBLIC;

GRANT CONNECT ON DATABASE postgres TO partysvc;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA party TO partysvc;
GRANT ALL ON ALL SEQUENCES IN SCHEMA party TO partysvc;
