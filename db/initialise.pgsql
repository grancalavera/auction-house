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

create table if not exists executionStatus (
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

create table if not exists reports (
    auctionId int references auctions(id) not null primary key,
    revenue numeric(19, 4) not null,
    soldQuantity int not null
);

create table if not exists execution (
    auctionId int references auctions(id) not null,
    bidId int references bids(id) not null,
    bidderId int references users(id) not null,
    status int references executionStatus(id) not null,
    primary key (auctionId, bidId)
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

insert into executionStatus (name)
    values
        ('NOT_FILLED'),
        ('FILLED')
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
        (1, 'A', 1, 2.000, now() at time zone 'utc' - interval '10 hour', null),
        (1, 'A', 1, 2.000, now() at time zone 'utc'  - interval '10 hour', null),
        (1, 'A', 1, 2.000, now() at time zone 'utc'  - interval '10 hour', null),
        (1, 'A', 1, 2.000, now() at time zone 'utc'  - interval '10 hour', null),
        (1, 'A', 1, 2.000, now() at time zone 'utc'  - interval '10 hour', null);

insert into bids
        (auctionId, bidderId, amount, createdAt)
    values
        -- 1: no bids 
        -- 2: one bid below asking price
        (2, 2, 2.000, now() at time zone 'utc'),
        -- 3: one bid below asking price and one bid at asking price
        (3, 2, 1.000, now() at time zone 'utc'),
        (3, 3, 2.000, now() at time zone 'utc'),
        -- 4: two bids at asking price placed at different times
        (4, 2, 2.000, now() at time zone 'utc'),
        (4, 3, 2.000, now() at time zone 'utc' - interval '1 hour'),
        -- 5: one bid above asking price and one at asking price
        (5, 2, 3.000, now() at time zone 'utc'),
        (5, 3, 2.000, now() at time zone 'utc');

