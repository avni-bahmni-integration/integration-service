insert into integration_system (name) values ('Goonj');

insert into avni_entity_status (entity_type, entity_sub_type, read_upto, integration_system_id) VALUES ('Encounter', 'Distribution', '1900-01-01', (select id from integration_system where name = 'Goonj'));
insert into avni_entity_status (entity_type, entity_sub_type, read_upto, integration_system_id) VALUES ('Encounter', 'Activity', '1900-01-01', (select id from integration_system where name = 'Goonj'));
insert into avni_entity_status (entity_type, entity_sub_type, read_upto, integration_system_id) VALUES ('Encounter', 'Dispatch Receipt', '1900-01-01', (select id from integration_system where name = 'Goonj'));

insert into integrating_entity_status(entity_type, read_upto_date_time, integration_system_id) values ('Demand', '2000-01-01', (select id from integration_system where name = 'Goonj'));
insert into integrating_entity_status(entity_type, read_upto_date_time, integration_system_id) values ('Dispatch', '2000-01-01', (select id from integration_system where name = 'Goonj'));

insert into integrating_entity_status(entity_type, read_upto_date_time, integration_system_id) values ('Demand', '2000-01-01', (select id from integration_system where name = 'Goonj'));
insert into integrating_entity_status(entity_type, read_upto_date_time, integration_system_id) values ('Dispatch', '2000-01-01', (select id from integration_system where name = 'Goonj'));

insert into mapping_group (name, integration_system_id) VALUES ('Demand', (select id from integration_system where name = 'Goonj'));
insert into mapping_group (name, integration_system_id) VALUES ('Dispatch', (select id from integration_system where name = 'Goonj'));
insert into mapping_group (name, integration_system_id) VALUES ('DispatchLineItem', (select id from integration_system where name = 'Goonj'));
insert into mapping_type (name, integration_system_id) VALUES ('Obs', (select id from integration_system where name = 'Goonj'));

insert into mapping_metadata(int_system_value, avni_value, about, data_type_hint, mapping_group_id, mapping_type_id)
VALUES ('Unit', 'Unit (Dispatched)', null, 'Coded', (select id from mapping_group where name = 'DispatchLineItem'), (select id from mapping_type where name='Obs'));
insert into mapping_metadata(int_system_value, avni_value, about, data_type_hint, mapping_group_id, mapping_type_id)
VALUES ('Quantity', 'Quantity (Dispatched)', null, 'Coded', (select id from mapping_group where name = 'DispatchLineItem'), (select id from mapping_type where name='Obs'));
