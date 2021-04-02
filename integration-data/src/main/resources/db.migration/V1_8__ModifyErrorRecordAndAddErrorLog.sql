alter table error_record drop column subject_patient_external_id;
alter table error_record drop column enrolment_external_id;
alter table error_record drop column program_encounter_external_id;
alter table error_record drop column encounter_external_id;
alter table error_record drop column error_type;

alter table error_record add column entity_id CHARACTER VARYING(250) NOT NULL default 'foo';
delete from error_record where entity_id = 'foo';

CREATE TABLE error_record_log (
  id SERIAL PRIMARY KEY,
  error_type CHARACTER VARYING(250) not null,
  logged_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  error_record_id int not null
);
ALTER TABLE ONLY error_record_log
  ADD CONSTRAINT error_record_log_error_record FOREIGN KEY (error_record_id) REFERENCES error_record (id);