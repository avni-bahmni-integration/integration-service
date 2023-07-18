CREATE TABLE error_record
(
  id        SERIAL PRIMARY KEY,
  bahmni_entity_type       CHARACTER VARYING(250),
  avni_entity_type       CHARACTER VARYING(250),
  subject_patient_external_id      CHARACTER VARYING(250)  NOT NULL,
  enrolment_external_id      CHARACTER VARYING(250),
  program_encounter_external_id      CHARACTER VARYING(250),
  encounter_external_id      CHARACTER VARYING(250),
  error_type CHARACTER VARYING(250) not null,
  check (bahmni_entity_type is not null or avni_entity_type is not null)
);