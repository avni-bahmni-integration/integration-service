-- Concepts for core fields in Avni
call add_concept_abi('Avni Entity UUID', 'Avni Entity UUID', 'Text', 'Misc', false, 'a1f16e61-065d-4fda-a31e-9c4737793249');
call add_concept_abi('CHW created by', 'CHW created by', 'Text', 'Misc', false, '8bda510a-53d9-4adc-960c-19d1733c48dd');
call add_concept_abi('CHW last changed by', 'CHW last changed by', 'Text', 'Misc', false, '00c2b2b4-97cb-435f-954b-51b8a733bc84');
call add_concept('Community registration date [Avni]', 'Community registration date', 'Text', 'Misc', false, 'c283a983-5db0-42b7-aa60-8ced1734eae6');

-- Encounter types
insert into encounter_type (name, date_created, uuid, changed_by, date_changed)
    values ('Community Registration', curdate(), '18c9a1d6-c4f5-4b64-8dbb-cf8ded8b9552', @user, curdate());

-- Concepts for Avni forms
