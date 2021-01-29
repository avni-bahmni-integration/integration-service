CREATE TABLE mapping_metadata
(
  id                 SERIAL PRIMARY KEY,
  mapping_group_name CHARACTER VARYING(250)  NOT NULL,
  mapping_name       CHARACTER VARYING(250)  NOT NULL,
  bahmni_value       CHARACTER VARYING(250)  NULL,
  avni_value         CHARACTER VARYING(250)  NULL,
  about              CHARACTER VARYING(1000) NULL
);