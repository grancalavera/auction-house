-- https://betterstack.com/community/guides/logging/how-to-start-logging-with-postgresql/
-- https://stackoverflow.com/questions/34918025/how-to-restart-postgresql
-- psql -U admin
-- select pg_current_logfile();
-- /var/lib/postgresql/data/log/
-- https://www.postgresql.org/docs/current/citext.html
create extension if not exists citext;

create table if not exists organisations (
    id serial primary key,
    name citext unique not null
);

create table if not exists accountStatus (
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
    accountStatusId int references accountStatus(id) not null,
    organisationId int references organisations(id) not null,
    roleId int references roles(id) not null
);

create table if not exists auctions (
    id serial primary key,
    sellerId int references users(id) not null,
    symbol varchar(256) not null,
    quantity int not null,
    -- not that I know
    -- https://stackoverflow.com/a/224866
    price numeric(19, 4) not null,
    createdAt timestamp with time zone not null,
    closedAt timestamp with time zone
);

create table if not exists bids (
    id serial primary key,
    bidderId int references users(id) not null,
    auctionId int references auctions(id) not null,
    amount numeric(19, 4) not null,
    -- https://stackoverflow.com/a/42779109
    -- https://stackoverflow.com/a/6627999
    createdAt timestamp with time zone not null
);

insert into organisations (name)
    values
        ('Auction House Solutions Inc.'),
        ('Optimistic Traders'),
        ('Institutional Investors'),
        ('The Bank of England')
    on conflict do nothing;

insert into accountStatus (name)
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
        (username, password, firstName, lastName, organisationId, accountStatusId, roleId)
    values
        ('u1', '123', 'Frank', 'Takahashi', 2, 1, 1),
        ('u2', '123', 'Alice', 'Smith', 2, 1, 1),
        ('u3', '123', 'Jiří', 'Novák', 1, 1, 1),
        ('admin', 'admin', 'admin', 'amin', 1, 1, 2),
        ('bu', '123', 'Blocked', 'User', 3, 2, 1),
        ('ba', '123', 'Blocked', 'Admin', 3, 2, 2)
    on conflict do nothing;

insert into auctions
        (sellerId, symbol, quantity, price, createdAt, closedAt)
    values
        -- open auction with single unit
        (1, 'A', 1, 1.000, now() at time zone 'utc', null),
        -- closed auction with single unit
        (1, 'A', 1, 1.000, now() at time zone 'utc', now() at time zone 'utc'),
        -- open auction with multiple units
        (1, 'B', 2, 1.000, now() at time zone 'utc', null),
        -- open auction, no bids (keep empty)
        (1, 'C', 1, 1.000, now() at time zone 'utc', null),
        -- closed auction, no bids (keep empty)
        (1, 'C', 1, 1.000, now() at time zone 'utc', now() at time zone 'utc');

insert into bids
        (auctionId, bidderId, amount, createdAt)
    values
        -- auction 1: u1 wins
        (1, 2, 1.001, now() at time zone 'utc'),
        (1, 3, 1.000, now() at time zone 'utc'),
        -- auction 2: u1 wins
        (2, 2, 1.001, now() at time zone 'utc'),
        (2, 3, 1.000, now() at time zone 'utc'),
        -- auction 3: u1 wins
        (3, 2, 2.001, now() at time zone 'utc'),
        (3, 3, 2.000, now() at time zone 'utc');

