alter table integrating_entity_status rename column read_upto to read_upto_numeric;
alter table integrating_entity_status add column read_upto_date_time TIMESTAMP WITHOUT TIME ZONE null;

alter table integrating_entity_status add constraint all_read_upto_cannot_be_null check ((read_upto_numeric is not null) or (read_upto_date_time is not null));

alter table mapping_metadata rename column bahmni_value to int_system_value;
