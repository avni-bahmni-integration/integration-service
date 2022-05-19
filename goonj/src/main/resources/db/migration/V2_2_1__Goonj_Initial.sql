insert into integration_system (name) values ('Goonj');

insert into avni_entity_status (entity_type, entity_sub_type, read_upto, integration_system_id) VALUES ('Encounter', 'Distribution', '1900-01-01', (select id from integration_system where name = 'Goonj'));
insert into avni_entity_status (entity_type, entity_sub_type, read_upto, integration_system_id) VALUES ('Encounter', 'Activity', '1900-01-01', (select id from integration_system where name = 'Goonj'));

insert into integrating_entity_status(entity_type, read_upto_date_time, integration_system_id) values ('Demand', '2000-01-01', (select id from integration_system where name = 'Goonj'));
insert into integrating_entity_status(entity_type, read_upto_date_time, integration_system_id) values ('Dispatch', '2000-01-01', (select id from integration_system where name = 'Goonj'));
