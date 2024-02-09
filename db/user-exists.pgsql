-- psql ah -U admin -f ./user-exists.pgsql -v id=1
select id from users where id=:id;
-- SELECT EXISTS(SELECT 1 FROM users WHERE id=:id)
