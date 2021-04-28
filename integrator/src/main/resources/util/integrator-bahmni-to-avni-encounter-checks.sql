-- Number of encounters should match
-- Bahmni
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
  and pi.identifier like 'TRI%';

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
  and pi.identifier like 'TRI%';