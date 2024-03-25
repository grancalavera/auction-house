drop table if exists tasks cascade;
drop table if exists labels cascade;
drop table if exists labeledTasks cascade;

create table if not exists tasks (
    id serial primary key,
    name text unique not null
);

create table if not exists labels (
    id serial primary key,
    name text unique not null
);

create table if not exists labeledTasks (
    task_id int references tasks(id) on delete cascade,
    label_id int references labels(id) on delete cascade,
    primary key (task_id, label_id)
);


insert into tasks (name) 
    values ('do dishes'), ('make bed'), ('file taxes'), ('swim'), ('vote');

insert into labels (name) 
    values ('household'), ('finance'), ('overdue');

insert into labeledTasks (task_id, label_id) 
    values 
        (1, 1),
        (2, 1),
        (3, 2),
        (1, 3);

select * from tasks;
select * from labels;
select * from tasks;
select * from labeledTasks;

select tasks.name as task, labels.name as label
    from tasks
    join labeledTasks on tasks.id = labeledTasks.task_id
    join labels on labeledTasks.label_id = labels.id
    where labels.name = 'household';

select tasks.name as task, labels.name as label
    from tasks
    join labeledTasks on tasks.id = labeledTasks.task_id
    join labels on labeledTasks.label_id = labels.id
    where labels.name = 'finance';

select tasks.name as task, labels.name as label
    from tasks
    join labeledTasks on tasks.id = labeledTasks.task_id
    join labels on labeledTasks.label_id = labels.id
    where labels.name = 'overdue';

select tasks.name as unlabelled
    from tasks
    left join labeledtasks on tasks.id = labeledtasks.task_id
    where labeledtasks.task_id is null;

delete from labeledTasks
    where label_id = (
        select id from labels
        where name = 'overdue'
    );

select tasks.name as overdue
    from tasks
    join labeledTasks on tasks.id = labeledTasks.task_id
    join labels on labeledTasks.label_id = labels.id
    where labels.name = 'overdue';

select * from tasks;
select * from labeledTasks;
