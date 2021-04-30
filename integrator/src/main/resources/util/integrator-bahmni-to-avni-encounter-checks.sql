-- Number of general encounters should match
-- Total
-- Bahmni
select count(distinct encounter_id)
from (select encounter.encounter_id
      from encounter
               join visit on encounter.visit_id = visit.visit_id
               join visit_type on visit.visit_type_id = visit_type.visit_type_id
               join encounter_type et on encounter.encounter_type = et.encounter_type_id
               join patient p on encounter.patient_id = p.patient_id
               join patient_identifier pi on p.patient_id = pi.patient_id
      where visit_type.uuid in (
                                'c22a5000-3f10-11e4-adec-0800271c1b75',
                                'c23d6c9d-3f10-11e4-adec-0800271c1b75',
                                'c2402997-3f10-11e4-adec-0800271c1b75',
                                'b9cb32c9-3f12-11e4-adec-0800271c1b75',
                                'bef32e14-3f12-11e4-adec-0800271c1b75',
                                '8c719d2e-a97d-48c1-a595-bacc33f9bbbe',
                                'ef9ecae8-d9e7-4e70-bf1b-3e8e98211931')
        and et.uuid in ('82024e00-3f10-11e4-adec-0800271c1b75')
        and encounter.voided = false
        and p.voided = false
        and pi.identifier like 'TRI%'

      union

      select encounter.encounter_id
      from encounter
               join visit on encounter.visit_id = visit.visit_id
               join visit_type on visit.visit_type_id = visit_type.visit_type_id
               join encounter_type et on encounter.encounter_type = et.encounter_type_id
               join patient p on encounter.patient_id = p.patient_id
               join patient_identifier pi on p.patient_id = pi.patient_id
               join orders o on encounter.encounter_id = o.encounter_id
               join drug_order on drug_order.order_id = o.order_id
      where visit_type.uuid in (
                                'c22a5000-3f10-11e4-adec-0800271c1b75',
                                'c23d6c9d-3f10-11e4-adec-0800271c1b75',
                                'c2402997-3f10-11e4-adec-0800271c1b75',
                                'b9cb32c9-3f12-11e4-adec-0800271c1b75',
                                'bef32e14-3f12-11e4-adec-0800271c1b75',
                                '8c719d2e-a97d-48c1-a595-bacc33f9bbbe',
                                'ef9ecae8-d9e7-4e70-bf1b-3e8e98211931')
        and encounter.voided = false
        and p.voided = false
        and pi.identifier like 'TRI%'
      group by encounter.encounter_id

      union

      select encounter.encounter_id
      from encounter
               join patient p on encounter.patient_id = p.patient_id
               join patient_identifier pi on p.patient_id = pi.patient_id
               join encounter_type on encounter.encounter_type = encounter_type.encounter_type_id
               join obs on encounter.encounter_id = obs.encounter_id
               join concept on obs.concept_id = concept.concept_id
      where concept.uuid in ('c36a7537-3f10-11e4-adec-0800271c1b75',
                             'c393fd1d-3f10-11e4-adec-0800271c1b75',
                             'c389d0ea-3f10-11e4-adec-0800271c1b75',
                             '930adbff-e6c9-44b6-a717-b9dfc678dd9e',
                             'f3d7969c-e9c7-4c7a-9b4f-278617212c3f',
                             '285604a9-6e8b-4029-9af6-947a1f0a2e17',
                             '81c5840e-3f10-11e4-adec-0800271c1b75')
        and encounter.voided = false
        and p.voided = false
        and obs.voided = false
        and pi.identifier like 'TRI%') x;

select count(distinct encounter.observations ->> '4b49798e-5416-44b8-82a0-6f464207e2ef')
from encounter
         join encounter_type et on encounter.encounter_type_id = et.id
where et.name not in ('Bahmni Patient Registration');

---- Lab
------ Bahmni
select count(*) number_of_lab_encounters
from encounter
         join visit on encounter.visit_id = visit.visit_id
         join visit_type on visit.visit_type_id = visit_type.visit_type_id
         join encounter_type et on encounter.encounter_type = et.encounter_type_id
         join patient p on encounter.patient_id = p.patient_id
         join patient_identifier pi on p.patient_id = pi.patient_id
where visit_type.uuid in (
                          'c22a5000-3f10-11e4-adec-0800271c1b75',
                          'c23d6c9d-3f10-11e4-adec-0800271c1b75',
                          'c2402997-3f10-11e4-adec-0800271c1b75',
                          'b9cb32c9-3f12-11e4-adec-0800271c1b75',
                          'bef32e14-3f12-11e4-adec-0800271c1b75',
                          '8c719d2e-a97d-48c1-a595-bacc33f9bbbe',
                          'ef9ecae8-d9e7-4e70-bf1b-3e8e98211931')
  and et.uuid in ('82024e00-3f10-11e4-adec-0800271c1b75')
  and encounter.voided = false
  and p.voided = false
  and pi.identifier like 'TRI%';
------ Avni
select count(*)
from encounter
         join encounter_type et on encounter.encounter_type_id = et.id
where et.name = 'Lab Results (Hospital)';
-- Drug Prescription
select count(*) number_of_prescription_encounters
from encounter
         join visit on encounter.visit_id = visit.visit_id
         join visit_type on visit.visit_type_id = visit_type.visit_type_id
         join encounter_type et on encounter.encounter_type = et.encounter_type_id
         join patient p on encounter.patient_id = p.patient_id
         join patient_identifier pi on p.patient_id = pi.patient_id
         join orders o on encounter.encounter_id = o.encounter_id
         join drug_order on drug_order.order_id = o.order_id
where visit_type.uuid in (
                          'c22a5000-3f10-11e4-adec-0800271c1b75',
                          'c23d6c9d-3f10-11e4-adec-0800271c1b75',
                          'c2402997-3f10-11e4-adec-0800271c1b75',
                          'b9cb32c9-3f12-11e4-adec-0800271c1b75',
                          'bef32e14-3f12-11e4-adec-0800271c1b75',
                          '8c719d2e-a97d-48c1-a595-bacc33f9bbbe',
                          'ef9ecae8-d9e7-4e70-bf1b-3e8e98211931')
  and encounter.voided = false
  and p.voided = false
  and o.voided = true
  and pi.identifier like 'TRI%'
group by encounter.encounter_id;
------ Avni
select count(*)
from encounter
         join encounter_type et on encounter.encounter_type_id = et.id
where et.name = 'Drug Prescriptions (Hospital)';

-- Regular encounters. first execute the following to get the list of bahmni form uuids from integration db. Replace in the second query.
-- Bahmni
select bahmni_value
from mapping_metadata
where mapping_name = 'EncounterType'
  and mapping_group_name = 'GeneralEncounter';
select count(*)
from encounter
         join patient p on encounter.patient_id = p.patient_id
         join patient_identifier pi on p.patient_id = pi.patient_id
         join encounter_type on encounter.encounter_type = encounter_type.encounter_type_id
         join obs on encounter.encounter_id = obs.encounter_id
         join concept on obs.concept_id = concept.concept_id
where concept.uuid in ('c36a7537-3f10-11e4-adec-0800271c1b75',
                       'c393fd1d-3f10-11e4-adec-0800271c1b75',
                       'c389d0ea-3f10-11e4-adec-0800271c1b75',
                       '930adbff-e6c9-44b6-a717-b9dfc678dd9e',
                       'f3d7969c-e9c7-4c7a-9b4f-278617212c3f',
                       '285604a9-6e8b-4029-9af6-947a1f0a2e17',
                       '81c5840e-3f10-11e4-adec-0800271c1b75')
  and encounter.voided = false
  and p.voided = false
  and obs.voided = false
  and pi.identifier like 'TRI%';
-- Avni
select count(*)
from encounter
         join encounter_type et on encounter.encounter_type_id = et.id
where et.name not in ('Drug Prescriptions (Hospital)', 'Lab Results (Hospital)', 'Bahmni Patient Registration');