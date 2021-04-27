-- Number of patients should match
-- OpenMRS
---- The query depends on your implementation
-- Avni
select count(*)
from encounter
         join audit a on encounter.audit_id = a.id
         join encounter_type et on encounter.encounter_type_id = et.id
where et.name = 'Bahmni Patient Registration'
  and a.created_by_id = ?;
-- Integration (only one error log should be present for one patient)
select count(*)
from error_record
where error_record.bahmni_entity_type = 'Patient';
select count(*)
from error_record_log
         join error_record er on error_record_log.error_record_id = er.id
where er.bahmni_entity_type = 'Patient';


-- Count of observations on all patients should be at least the number of mandatory fields
-- Avni
select id, count(*)
from (
         select encounter.id, jsonb_each(encounter.observations)
         from encounter
                  join audit a on encounter.audit_id = a.id
                  join encounter_type et on encounter.encounter_type_id = et.id
         where et.name = 'Bahmni Patient Registration'
           and a.created_by_id = ?
         order by 1
     ) x
group by x.id
having count(*) < ?;

-- On error record rerun same as all of above

-- Patient with max number of attributes should be migrated with all the attributes
-- Check possible only with non-anonymised database
-- OpenMRS
select patient_identifier.identifier, count(*)
from patient_identifier
         join patient p on patient_identifier.patient_id = p.patient_id
         join person p2 on p.patient_id = p2.person_id
         join person_attribute on p2.person_id = person_attribute.person_id
where p.voided = false
  and identifier like 'TRI%'
group by patient_identifier.identifier
order by 2 desc;
-- Avni
select id, count(*)
from (
         select encounter.id, jsonb_each(encounter.observations)
         from encounter
                  join audit a on encounter.audit_id = a.id
                  join encounter_type et on encounter.encounter_type_id = et.id
         where et.name = 'Bahmni Patient Registration'
           and a.created_by_id = 354
         order by 1
     ) x
group by x.id
order by 2 desc;
-- To get details of one patient in OpenMRS for verification
select patient_identifier.identifier, person_attribute_type.description, person_attribute.value, person_attribute_type.format
from patient_identifier
         join patient p on patient_identifier.patient_id = p.patient_id
         join person p2 on p.patient_id = p2.person_id
         join person_attribute on p2.person_id = person_attribute.person_id
         join person_attribute_type on person_attribute.person_attribute_type_id = person_attribute_type.person_attribute_type_id
where p.voided = false
  and identifier like 'TRI%'
  and person_attribute.voided = false
  and identifier = 'TRI02012701'
order by format;


-- Query to get sample patients to check in Avni
-- OpenMRS
select patient_identifier.identifier, count(*)
from patient_identifier
         join patient p on patient_identifier.patient_id = p.patient_id
         join person p2 on p.patient_id = p2.person_id
         join person_attribute on p2.person_id = person_attribute.person_id
where p.voided = false
  and identifier like 'TRI%'
group by patient_identifier.identifier
having count(*) > 2
order by 2 desc;
-- Avni
select identifier, count(*)
from (
         select individual.observations ->> 'c9e64f06-23ac-47dc-b7e4-c93b819f52ce' identifier, jsonb_each(encounter.observations)
         from encounter
                  join individual on encounter.individual_id = individual.id
                  join audit a on encounter.audit_id = a.id
                  join encounter_type et on encounter.encounter_type_id = et.id
         where et.name = 'Bahmni Patient Registration'
           and a.created_by_id = 354
         order by 1
     ) x
group by x.identifier
order by 2 desc;