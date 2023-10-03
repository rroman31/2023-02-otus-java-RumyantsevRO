DO
$$
    DECLARE
        cur_client_id bigint;
    BEGIN
        insert into client (name)
        values ('first_name')
        RETURNING id INTO cur_client_id;

        insert into address(street, client_id)
        values ('first_street', cur_client_id);

        insert into phone(number, client_id)
        values ('98887776655', cur_client_id);

        insert into phone(number, client_id)
        values ('98887776644', cur_client_id);
---
        insert into client (name)
        values ('second_name')
        RETURNING id INTO cur_client_id;

        insert into address(street, client_id)
        values ('second_street', cur_client_id);

        insert into phone(number, client_id)
        values ('98887776633', cur_client_id);

        insert into phone(number, client_id)
        values ('98887776622', cur_client_id);
---
        insert into client (name)
        values ('third_name')
        RETURNING id INTO cur_client_id;

        insert into address(street, client_id)
        values ('third_street', cur_client_id);

        insert into phone(number, client_id)
        values ('98887776611', cur_client_id);

        insert into phone(number, client_id)
        values ('98887776600', cur_client_id);
    END
$$;
