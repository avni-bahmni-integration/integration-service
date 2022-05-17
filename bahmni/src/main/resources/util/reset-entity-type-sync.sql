-- Bahmni to Avni
-- Encounter
update bahmni_entity_status
set read_upto = 0
where entity_type = 'Encounter';
delete
from error_record_log
where id in (
    select error_record_log.id
    from error_record_log
             join error_record on error_record_log.error_record_id = error_record.id
    where error_record.bahmni_entity_type = 'Encounter'
);
delete
from error_record
where id in (
    select id
    from error_record
    where error_record.bahmni_entity_type = 'Encounter'
);