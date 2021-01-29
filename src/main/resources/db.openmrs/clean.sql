delete from concept_name where date_created > '2020-12-01';
delete from concept where date_created > '2020-12-01';
delete from encounter_type where date_created > '2020-12-01';

DROP PROCEDURE IF EXISTS add_concept_abi;