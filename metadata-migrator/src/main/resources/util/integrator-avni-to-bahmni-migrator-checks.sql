-- 1.1 Check concepts migrated properly
-- 1.1 *******START**************************
-- 1.1 Avni
select data_type, count(*)
from concept c
where 1=1
  and name not like '%[H]'
  and name not like '%[HP]'
  and name not like '%[Bahmni]'
group by data_type;

-- 1.1 Integration
select data_type_hint, count(*)
from mapping_metadata
where 1=1
  and mapping_group_name='Observation'
  and mapping_name='Concept'
  and avni_value not like '%[H]'
  and avni_value not like '%[HP]'
  and avni_value not like '%[Bahmni]'
  and avni_value not in ('Registration date', 'First name', 'Last name', 'Date of birth', 'Gender')
group by data_type_hint;
-- 1.1 *******END**************************
