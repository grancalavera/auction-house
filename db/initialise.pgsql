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

create table if not exists auction_status (
    id serial primary key,
    name varchar(256) unique not null
);

create table if not exists auctions (
    id serial primary key,
    seller_id int references users(id) not null,
    symbol varchar(256) not null,
    quantity int not null,
    -- not that I know
    -- https://stackoverflow.com/a/224866
    price numeric(19, 4) not null,
    status_id int references auction_status(id) not null
);

create table if not exists bids (
    id serial primary key,
    bidder_id int references users(id) not null,
    auction_id int references auctions(id) not null,
    amount numeric(19, 4) not null,
    -- https://stackoverflow.com/a/42779109
    -- https://stackoverflow.com/a/6627999
    bidtimestamp timestamp with time zone not null
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
        ('admin', 'admin', 'Coyote', 'Jackson', 1, 1, 2),   -- 1
        ('blockedu', '123', 'Blocked', 'User', 3, 2, 1),    -- 2
        ('blockeda', '123', 'Blocked', 'Admin', 3, 2, 2),   -- 3
        ('u1', '123', 'Frank', 'Takahashi', 2, 1, 1),       -- 4
        ('u2', '123', 'Alice', 'Smith', 2, 1, 1),           -- 5
        ('u3', '123', 'Jiří', 'Novák', 1, 1, 1),            -- 6
        ('u4', '123', 'Marta', 'García', 2, 1, 1),          -- 7
        ('u5', '123', 'Andrea', 'Rossi', 1, 1, 1),          -- 8    
        ('u6', '123', 'Jens', 'Hansen', 2, 1, 1),           -- 9
        ('u7', '123', 'Piotr', 'Nowak', 4, 1, 1)            -- 10
    on conflict do nothing;

insert into auction_status (name)
    values
        ('OPEN'),
        ('CLOSED')
    on conflict do nothing;

insert into auctions
        (seller_id, symbol, quantity, price, status_id)
    values
        (2, 'AAPL', 1, 100.13, 1);

insert into bids
        (bidder_id, auction_id, amount, bidtimestamp)
    values
        (10, 1, 100.17, '2021-01-01 12:00:04');
