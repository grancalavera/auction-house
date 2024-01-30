--  psql auction-house -U grancalavera -f create-user.pgsql  -v org="Cautious Peachez" -v usr="u11"
-- or
--  psql admin -U admin -f create-user.pgsql  -v org="Cautious Peachez" -v usr="u11"
BEGIN;
insert into ah_organisations (org_name) values (:'org') on conflict do nothing;
insert into ah_users (username, password, first_name, last_name, organisation_id, account_status_id, role_id)
select :'usr', 'changeme', '', '', org.id, 1, 1 from ah_organisations as org where org.org_name = :'org';
COMMIT;
