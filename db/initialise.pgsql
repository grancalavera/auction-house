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
    status_id int references auction_status(id) not null,
    createdAt timestamp with time zone not null,
    closedAt timestamp with time zone
);

create table if not exists bids (
    id serial primary key,
    bidder_id int references users(id) not null,
    auction_id int references auctions(id) not null,
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
        ('bu', '123', 'Blocked', 'User', 3, 2, 1),          -- 2
        ('ba', '123', 'Blocked', 'Admin', 3, 2, 2),         -- 3

        ('u4', '123', 'Frank', 'Takahashi', 2, 1, 1),       -- 4
        ('u5', '123', 'Alice', 'Smith', 2, 1, 1),           -- 5
        ('u6', '123', 'Jiří', 'Novák', 1, 1, 1),            -- 6
        ('u7', '123', 'Marta', 'García', 2, 1, 1),          -- 7
        ('u8', '123', 'Andrea', 'Rossi', 1, 1, 1),          -- 8
        ('u9', '123', 'Jens', 'Hansen', 2, 1, 1),           -- 9
        ('u10', '123', 'Piotr', 'Nowak', 4, 1, 1)           -- 10
    on conflict do nothing;

insert into auction_status (name)
    values
        ('OPEN'),
        ('CLOSED')
    on conflict do nothing;

insert into auctions
        (id, seller_id, symbol, quantity, price, status_id, createdAt, closedAt)
    values
        (1, 4, 'A', 1, 100.13, 1, now() at time zone 'utc', null),     
        (2, 4, 'B', 1, 100.13, 1, now() at time zone 'utc', null),     
        (3, 4, 'C', 1, 100.13, 1, now() at time zone 'utc', null),   

        (4, 5, 'ZA', 1, 200.13, 1, now() at time zone 'utc', null),    
        (5, 5, 'ZB', 1, 200.13, 1, now() at time zone 'utc', null),    
        
        (6, 6, 'ZC', 1, 200.13, 1, now() at time zone 'utc', null),    
        
        (7, 8, 'ZD', 1, 200.13, 1, now() at time zone 'utc', null),    
        (8, 8, 'Zr', 1, 200.13, 1, now() at time zone 'utc', null),    

        (9, 5, 'ZA', 1, 200.13, 2, now() at time zone 'utc', now() at time zone 'utc'),    
        (10, 5, 'ZB', 1, 200.13, 2, now() at time zone 'utc', now() at time zone 'utc'),   

        (11, 8, 'ZC', 1, 200.13, 2, now() at time zone 'utc', now() at time zone 'utc'),    
        (12, 8, 'hK', 1, 200.13, 2, now() at time zone 'utc', now() at time zone 'utc'),

        (13, 10, 'ODDITY', 23, 101.101, 1, now() at time zone 'utc', null);


insert into bids
        (auction_id, bidder_id, amount, createdAt)
    values
        (1, 10, 100.17, (now() at time zone 'utc')),
        (1, 9, 100.17, (now() at time zone 'utc')),
        
        (2, 10, 100.17, (now() at time zone 'utc')),
        (2, 9, 100.17, (now() at time zone 'utc')),
        
        (3, 10, 100.17, (now() at time zone 'utc')),
        (3, 9, 100.17, (now() at time zone 'utc')),
        
        (4, 10, 200.17, (now() at time zone 'utc')),
        (4, 9, 200.17, (now() at time zone 'utc')),
        
        (5, 10, 200.17, (now() at time zone 'utc')),
        (5, 9, 200.17, (now() at time zone 'utc')),
        
        (6, 10, 200.17, (now() at time zone 'utc')),
        (6, 9, 200.17, (now() at time zone 'utc')),
        
        (9, 4, 200.17, (now() at time zone 'utc')),

        (11, 10, 200.17, (now() at time zone 'utc')),
        (11, 8, 200.17, (now() at time zone 'utc'));
        
        
        
