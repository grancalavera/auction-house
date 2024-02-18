drop table if exists test;

create table if not exists test (
    id serial primary key,
    name varchar(255) not null,
    quantity int not null
);

insert 
    into test (name, quantity) 
    values ('initial name', 5)
    on conflict (id) 
    do update set 
        name=excluded.name,quantity=excluded.quantity
    returning *;


insert 
    into test (id, name, quantity) 
    values (1, 'updated name', 30) 
    on conflict (id) 
    do update set 
        name=excluded.name,quantity=excluded.quantity
    returning *

-- select * from test;
 