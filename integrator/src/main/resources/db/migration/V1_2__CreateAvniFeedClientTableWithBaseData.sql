CREATE TABLE avni_entity_status
(
  id          serial primary key,
  entity_type varchar(100) not null,
  read_upto   timestamp    not null
);

insert into avni_entity_status (entity_type, read_upto) values ('Subject', '1900-01-01');