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
    first_name varchar(256) not null,
    last_name varchar(256) not null
);

insert into ah_organisations (org_name) 
    values 
        ('Auction House Solutions Inc.'),
        ('Optimistic Traders'),
        ('Institutional Investors'),
        ('The Bank of England')
    on conflict do nothing;

insert into ah_accountstatus (status_name) 
    values 
        ('ACTIVE'),
        ('BLOCKED')
    on conflict do nothing;

insert into ah_roles (role_name) 
    values 
        ('USER'),
        ('ADMIN')
    on conflict do nothing;

insert into ah_users
        (username, password, first_name, last_name, organisation_id, account_status_id, role_id)
    values
        ('admin', 'admin', 'Coyote', 'Jackson', 1, 1, 2),
        ('u1', '123', 'Frank', 'Takahashi', 2, 1, 1),
        ('u2', '123', 'Ahmed', 'Al-Mansour', 3, 1, 1),
        ('u3', '123', 'Sigríður', 'Jónsdóttir', 4, 1, 1),
        ('u4', '123', 'Jiří', 'Novák', 1, 1, 1),
        ('u5', '123', 'Marta', 'García', 2, 1, 1),
        ('u6', '123', 'John', 'Smith', 3, 1, 1),
        ('u7', '123', 'Maria', 'González', 4, 1, 1),
        ('u8', '123', 'Andrea', 'Rossi', 1, 1, 1),
        ('u9', '123', 'Jens', 'Hansen', 2, 1, 1),
        ('u10', '123', 'Anna', 'Andersson', 3, 1, 1),
        ('u11', '123', 'Piotr', 'Nowak', 4, 1, 1),
        ('u12', '123', 'Ivan', 'Ivanov', 1, 1, 1),
        ('u13', '123', 'Pablo', 'Rodríguez', 2, 1, 1),
        ('u14', '123', 'Marta', 'García', 3, 1, 1),
        ('u15', '123', 'John', 'Smith', 4, 1, 1),
        ('u16', '123', 'Maria', 'González', 1, 1, 1),
        ('u17', '123', 'Andrea', 'Rossi', 2, 1, 1),
        ('u18', '123', 'Jens', 'Hansen', 3, 1, 1),
        ('u19', '123', 'Anna', 'Andersson', 4, 1, 1),
        ('u20', '123', 'Piotr', 'Nowak', 1, 1, 1),
        ('u21', '123', 'Ivan', 'Ivanov', 2, 1, 1),
        ('u22', '123', 'Pablo', 'Rodríguez', 3, 1, 1)
    on conflict do nothing;

