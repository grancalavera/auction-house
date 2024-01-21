select 
    u.id, u.username, 
    u.password, 
    a.status_name as status,
    r.role_name as role, 
    o.org_name as organisation
from ah_users u
    join ah_organisations o on u.organisation_id = o.id
    join ah_accountstatus a on u.account_status_id = a.id
    join ah_roles r on u.role_id = r.id;
