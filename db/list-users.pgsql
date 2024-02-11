select
    u.id, u.username,
    u.password,
    u.firstname,
    u.lastname,
    a.name as acountstatus,
    r.name as role,
    o.name as organisation
from users u
    left join organisations o on u.organisation_id = o.id
    left join account_status a on u.accountstatus_id = a.id
    left join roles r on u.role_id = r.id
order by u.id;

