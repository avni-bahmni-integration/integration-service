alter table bahmni_entity_status rename to integrating_entity_status;
alter table error_record rename column bahmni_entity_type to integrating_entity_type;
alter table ignored_bahmni_concept rename to ignored_integrating_concept;
alter table ignored_integrating_concept rename column concept_uuid to concept_id;
