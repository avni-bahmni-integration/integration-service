insert into integration_system (name) values ('power');

insert into integrating_entity_status
(entity_type, read_upto_numeric, read_upto_date_time, integration_system_id)
values ('Call Details', 0, '2022-08-10', (select id from integration_system where name = 'power'));

insert into error_type (name, integration_system_id) values ('TaskNotSaved', (select id from integration_system where name = 'power'));
insert into error_type (name, integration_system_id) values ('CallSidDeleted', (select id from integration_system where name = 'power'));