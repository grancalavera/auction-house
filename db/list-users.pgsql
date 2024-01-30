select 
    u.id, u.username, 
    u.password, 
    u.first_name, 
    u.last_name,
    a.status_name as status,
    r.role_name as role, 
    o.org_name as organisation
from ah_users u
    left join ah_organisations o on u.organisation_id = o.id
    left join ah_accountstatus a on u.account_status_id = a.id
    left join ah_roles r on u.role_id = r.id
order by u.id;
    
