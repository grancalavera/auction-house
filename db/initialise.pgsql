create table if not exists ah_organisations (
    id serial primary key,
    org_name varchar(256) unique not null
);

create table if not exists ah_accountstatus (
    id serial primary key,
    status_name varchar(256) unique not null
);

create table if not exists ah_roles (
    id serial primary key,
    role_name varchar(256) unique not null
);

create table if not exists ah_users (
    id serial primary key,
    username varchar(256) unique not null,
    password varchar(256) not null,
    role_id int references ah_roles(id) not null,
    organisation_id int references ah_organisations(id) not null,
    account_status_id int references ah_accountstatus(id) not null,
    first_name varchar(256),
    last_name varchar(256)
);


insert into ah_organisations (org_name) values ('Auction House Solutions Inc.') on conflict do nothing;
insert into ah_organisations (org_name) values ('Optimistic Traders') on conflict do nothing;
insert into ah_organisations (org_name) values ('Institutional Investors') on conflict do nothing;
insert into ah_organisations (org_name) values ('The Bank of England') on conflict do nothing;

insert into ah_accountstatus (status_name) values ('ACTIVE') on conflict do nothing;
insert into ah_accountstatus (status_name) values ('BLOCKED') on conflict do nothing;

insert into ah_roles (role_name) values ('USER') on conflict do nothing;
insert into ah_roles (role_name) values ('ADMIN') on conflict do nothing;

insert into ah_users
        (username, password, first_name, last_name, organisation_id, account_status_id, role_id)
    values
        ('admin', 'admin', 'Pinche', 'Pancho', 1, 1, 2);

insert into ah_users
        (username, password, organisation_id, account_status_id, role_id)
    values
        ('u1', '123', 2, 1, 1);

insert into ah_users
        (username, password, organisation_id, account_status_id, role_id)
    values
        ('u2', '123', 3, 1, 1);

insert into ah_users
        (username, password, organisation_id, account_status_id, role_id)
    values
        ('u4', '123', 4, 1, 1);

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
