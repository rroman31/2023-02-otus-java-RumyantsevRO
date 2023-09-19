-- Для @GeneratedValue(strategy = GenerationType.IDENTITY)
/*
create table client
(
    id   bigserial not null primary key,
    name varchar(50)
);

 */

-- Для @GeneratedValue(strategy = GenerationType.SEQUENCE)
create sequence client_SEQ start with 1 increment by 1;
create sequence phone_SEQ start with 1 increment by 1;
create sequence address_SEQ start with 1 increment by 1;

create table address
(
    id     bigint not null primary key,
    street varchar(500)
);

create table client
(
    id         bigint not null primary key,
    address_id bigint references address (id),
    name       varchar(50)
);

create table phone
(
    id        bigint not null primary key,
    number    varchar(50),
    client_id bigint references client (id)

);
