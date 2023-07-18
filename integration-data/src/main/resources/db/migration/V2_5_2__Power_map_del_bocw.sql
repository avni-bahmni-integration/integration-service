DELETE
FROM mapping_metadata
WHERE
        int_system_value <> '01141236600' and
        integration_system_id = (select id from integration_system where name = 'power');

insert into integrating_entity_status
(entity_type, read_upto_numeric, read_upto_date_time, integration_system_id)
values ('Call Details::01141236600', 0, '2023-03-01 00:00:00.000000', (select id from integration_system where name = 'power'));

insert into mapping_metadata(int_system_value, avni_value, about, data_type_hint, mapping_group_id, mapping_type_id, integration_system_id) VALUES
    ('01141236600', 'Delhi BoCW', null, 'NA', (select id from mapping_group where name = 'PhoneNumber'), (select id from mapping_type where name='State'), (select id from integration_system where name = 'power')),
    ('01141236600', 'BoCW', null, 'NA', (select id from mapping_group where name = 'PhoneNumber'), (select id from mapping_type where name='Program'), (select id from integration_system where name = 'power'));
