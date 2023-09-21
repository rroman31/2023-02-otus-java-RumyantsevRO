insert into address (id, street)
values (nextval('address_SEQ'), 'first_street');
insert into client (id, address_id, name)
values (nextval('client_SEQ'), currval('address_SEQ'), 'first_client_name');
insert into phone(id, number, client_id)
values (nextval('phone_SEQ'), '8-999-555-44-33', currval('client_SEQ'));
insert into phone(id, number, client_id)
values (nextval('phone_SEQ'), '8-999-555-44-22', currval('client_SEQ'));

insert into address (id, street)
values (nextval('address_SEQ'), 'second_street');
insert into client (id, address_id, name)
values (nextval('client_SEQ'), currval('address_SEQ'), 'second_client_name');
insert into phone(id, number, client_id)
values (nextval('phone_SEQ'), '8-999-555-33-33', currval('client_SEQ'));
insert into phone(id, number, client_id)
values (nextval('phone_SEQ'), '8-999-555-33-22', currval('client_SEQ'));

insert into address (id, street)
values (nextval('address_SEQ'), 'third_street');
insert into client (id, address_id, name)
values (nextval('client_SEQ'), currval('address_SEQ'), 'third_client_name');
insert into phone(id, number, client_id)
values (nextval('phone_SEQ'), '8-999-555-22-33', currval('client_SEQ'));
insert into phone(id, number, client_id)
values (nextval('phone_SEQ'), '8-999-555-22-22', currval('client_SEQ'));

insert into app_user (id, name, login, password)
values (nextval('app_user_SEQ'), 'admin', 'admin', '1111');