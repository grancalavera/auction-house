select
    u.id, u.username,
    u.password,
    u.firstname,
    u.lastname,
    a.name as acountstatus,
    r.name as role,
    o.name as organisation
from users u
    left join organisations o on u.organisationId = o.id
    left join accountStatus a on u.accountStatusId = a.id
    left join roles r on u.roleId = r.id
order by u.id;

