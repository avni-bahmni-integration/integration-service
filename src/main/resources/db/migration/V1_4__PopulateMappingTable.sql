insert into mapping_metadata (mapping_group_name, mapping_name, bahmni_value, avni_value, about)
values ('PatientSubject', 'PatientIdentifierConcept', 'Patient Identifier', 'Sangam Number',
    'Subject type should be inferred from the metadata mapping name Patient Subject Type.');

insert into mapping_metadata (mapping_group_name, mapping_name, bahmni_value, avni_value, about)
values ('PatientSubject', 'AvniRegistrationEncounter', null, 'Community Registration',
        'Bahmni value is null because there is only type of subject in Bahmni which is patient');
