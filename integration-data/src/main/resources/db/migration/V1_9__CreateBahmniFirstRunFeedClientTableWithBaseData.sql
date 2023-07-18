CREATE TABLE bahmni_entity_status
(
    id          serial primary key,
    entity_type varchar(100) not null,
    read_upto   int          not null
);

insert into bahmni_entity_status (entity_type, read_upto) values ('Patient', 0);
insert into bahmni_entity_status (entity_type, read_upto) values ('Encounter', 0);