create table if not exists organisations (
    id serial primary key,
    name varchar(256) unique not null
);

create table if not exists account_status (
    id serial primary key,
    name varchar(256) unique not null
);

create table if not exists roles (
    id serial primary key,
    name varchar(256) unique not null
);

create table if not exists users (
    id serial primary key,
    username varchar(256) unique not null,
    password varchar(256) not null,
    firstname varchar(256) not null,
    lastname varchar(256) not null,
    accountstatus_id int references account_status(id) not null,
    organisation_id int references organisations(id) not null,
    role_id int references roles(id) not null
);

insert into organisations (name)
    values
        ('Auction House Solutions Inc.'),
        ('Optimistic Traders'),
        ('Institutional Investors'),
        ('The Bank of England')
    on conflict do nothing;

insert into account_status (name)
    values
        ('ACTIVE'),
        ('BLOCKED')
    on conflict do nothing;

insert into roles (name)
    values
        ('USER'),
        ('ADMIN')
    on conflict do nothing;

insert into users
        (username, password, firstName, lastName, organisation_id, accountstatus_id, role_id)
    values
        ('admin', 'admin', 'Coyote', 'Jackson', 1, 1, 2),
        ('blockedu', '123', 'Blocked', 'User', 3, 2, 1),
        ('blockeda', '123', 'Blocked', 'Admin', 3, 2, 2),
        ('u1', '123', 'Frank', 'Takahashi', 2, 1, 1),
        ('u2', '123', 'Alice', 'Smith', 2, 1, 1),
        ('u3', '123', 'Jiří', 'Novák', 1, 1, 1),
        ('u4', '123', 'Marta', 'García', 2, 1, 1),
        ('u5', '123', 'Andrea', 'Rossi', 1, 1, 1),
        ('u6', '123', 'Jens', 'Hansen', 2, 1, 1),
        ('u7', '123', 'Piotr', 'Nowak', 4, 1, 1)
    on conflict do nothing;

