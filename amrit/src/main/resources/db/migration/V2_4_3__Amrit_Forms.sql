insert into integrating_entity_status values ( DEFAULT, 'BornBirth', 0, '2022-10-01 05:55:59.553', 3);
insert into integrating_entity_status values ( DEFAULT, 'CBAC', 0, '2022-10-01 05:55:59.553', 3);
insert into integrating_entity_status values ( DEFAULT, 'Household', 0, '2022-10-01 05:55:59.553', 3);

INSERT INTO public.mapping_group (name, integration_system_id) VALUES ('BornBirth'::varchar(250), 3::integer);
INSERT INTO public.mapping_group (name, integration_system_id) VALUES ('CBAC'::varchar(250), 3::integer);
INSERT INTO public.mapping_group (name, integration_system_id) VALUES ('Household'::varchar(250), 3::integer);

INSERT INTO public.mapping_type (name, integration_system_id) VALUES ('BornBirthRoot'::varchar(250), 3::integer);
INSERT INTO public.mapping_type (name, integration_system_id) VALUES ('CBACRoot'::varchar(250),3::integer);
INSERT INTO public.mapping_type (name, integration_system_id) VALUES ('HouseholdRoot'::varchar(250), 3::integer);

INSERT INTO public.mapping_metadata (int_system_value, avni_value, about, data_type_hint, integration_system_id,
                                     mapping_group_id, mapping_type_id)
VALUES
    ('mohallaName'::varchar(250), 'Mohalla name'::varchar(250), null::varchar(1000), null::varchar(100), 3,
     (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot')),
    ('bleedingAfterIntercourse'::varchar(250), 'Bleeding after intercourse'::varchar(250), null::varchar(1000), null::varchar(100), 3,
     (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),
    ('birthWeight'::varchar(250), 'Weight of first child'::varchar(250), null::varchar(1000), null::varchar(100), 3,
     (select id from public.mapping_group where name = 'BornBirth'), (select id from public.mapping_type where name = 'BornBirthRoot'))
;

INSERT INTO error_type (id, name, integration_system_id, comparison_operator, comparison_value)
VALUES (DEFAULT, 'BeneficiaryAmritIDFetchError', 3, '', ''),
       (DEFAULT, 'BeneficiaryCreationError', 3, '', ''),
       (DEFAULT, 'AmritEntityNotCreated', 3, '', ''),
       (DEFAULT, 'EntityIsDeleted', 3, '', '');

