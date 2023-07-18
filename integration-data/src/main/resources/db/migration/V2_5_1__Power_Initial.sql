insert into integration_system (name) values ('power');

insert into integrating_entity_status
(entity_type, read_upto_numeric, read_upto_date_time, integration_system_id)
values ('Call Details', 0, now(), (select id from integration_system where name = 'power'));

insert into error_type (name, integration_system_id) values ('TaskNotSaved', (select id from integration_system where name = 'power'));
insert into error_type (name, integration_system_id) values ('CallSidDeleted', (select id from integration_system where name = 'power'));

insert into mapping_group (name, integration_system_id) VALUES ('PhoneNumber', (select id from integration_system where name = 'power'));

insert into mapping_type (name, integration_system_id) VALUES ('State', (select id from integration_system where name = 'power'));
insert into mapping_type (name, integration_system_id) VALUES ('Program', (select id from integration_system where name = 'power'));

insert into mapping_metadata(int_system_value, avni_value, about, data_type_hint, mapping_group_id, mapping_type_id, integration_system_id) VALUES
('01141136600', 'DL', null, 'NA', (select id from mapping_group where name = 'PhoneNumber'), (select id from mapping_type where name='State'), (select id from integration_system where name = 'power')),
('01141132680', 'DL', null, 'NA', (select id from mapping_group where name = 'PhoneNumber'), (select id from mapping_type where name='State'), (select id from integration_system where name = 'power')),
('01141132689', 'CG', null, 'NA', (select id from mapping_group where name = 'PhoneNumber'), (select id from mapping_type where name='State'), (select id from integration_system where name = 'power')),

('01141136600', 'BoCW', null, 'NA', (select id from mapping_group where name = 'PhoneNumber'), (select id from mapping_type where name='Program'), (select id from integration_system where name = 'power')),
('01141132680', 'RTE', null, 'NA', (select id from mapping_group where name = 'PhoneNumber'), (select id from mapping_type where name='Program'), (select id from integration_system where name = 'power')),
('01141132689', 'RTE', null, 'NA', (select id from mapping_group where name = 'PhoneNumber'), (select id from mapping_type where name='Program'), (select id from integration_system where name = 'power'));