insert into integration_system (name) values ('Goonj');

insert into integrating_entity_type (name, integration_system_id) values ('Demand', (select id from integration_system where name = 'Goonj'));
insert into integrating_entity_type (name, integration_system_id) values ('Dispatch', (select id from integration_system where name = 'Goonj'));
insert into integrating_entity_type (name, integration_system_id) values ('Distribution', (select id from integration_system where name = 'Goonj'));
insert into integrating_entity_type (name, integration_system_id) values ('Dispatch Receipt', (select id from integration_system where name = 'Goonj'));
