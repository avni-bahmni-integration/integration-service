insert into integrating_entity_status values ( DEFAULT, 'Beneficiary', 0, '2022-10-01 05:55:59.553', 3);
insert into integrating_entity_status values ( DEFAULT, 'BeneficiaryScan', 0, '2022-10-01 05:55:59.553', 3);


INSERT INTO public.mapping_group (name, integration_system_id) VALUES ('Beneficiary'::varchar(250), 3::integer);


INSERT INTO public.mapping_type (name, integration_system_id) VALUES ('BeneficiaryRoot'::varchar(250), 3::integer);
INSERT INTO public.mapping_type (name, integration_system_id) VALUES ('BeneficiaryDemographics'::varchar(250),3::integer);
INSERT INTO public.mapping_type (name, integration_system_id) VALUES ('BeneficiaryPhoneMaps'::varchar(250), 3::integer);
INSERT INTO public.mapping_type (name, integration_system_id) VALUES ('BeneficiaryIdentity'::varchar(250), 3::integer);
INSERT INTO public.mapping_type (name, integration_system_id) VALUES ('BeneficiaryObservations'::varchar(250), 3::integer);


 INSERT INTO public.mapping_metadata (int_system_value, avni_value, about, data_type_hint, integration_system_id,
                                      mapping_group_id, mapping_type_id)
 VALUES ('avniBeneficiaryID'::varchar(250), 'ID'::varchar(250), null::varchar(1000), null::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
  ('firstName'::varchar(250), 'First name'::varchar(250), null::varchar(1000), null::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
  ('lastName'::varchar(250), 'Last name'::varchar(250), null::varchar(1000), null::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
  ('dOB'::varchar(250), 'Date of birth'::varchar(250), null::varchar(1000), null::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
  ('spouseName'::varchar(250), 'Spouse name'::varchar(250), null::varchar(1000), null::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
  ('maritalStatusID'::varchar(250), 'Marital status'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
  ('maritalStatusName'::varchar(250), 'Marital status'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
  ('genderID'::varchar(250), 'Gender'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
  ('genderName'::varchar(250), 'Gender'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
  ('occupationID'::varchar(250), 'Occupation'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryDemographics')),
  ('educationID'::varchar(250), 'Educational qualification'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryDemographics')),
  ('educationName'::varchar(250), 'Educational qualification'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryDemographics')),
  ('communityID'::varchar(250), 'Caste'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryDemographics')),
  ('religionID'::varchar(250), 'Religion'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryDemographics')),
  ('districtBranchID'::varchar(250), 'Village'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryDemographics')),
  ('govtIdentityTypeID'::varchar(250), 'Enrol with'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryIdentity')),
  ('govtIdentityTypeName'::varchar(250), 'Enrol with'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryIdentity'));




INSERT INTO mapping_metadata (int_system_value, avni_value, about, data_type_hint, integration_system_id,
                                     mapping_group_id, mapping_type_id)
VALUES
 ('Unmarried', 'Unmarried', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Married', 'Married', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Divorced', 'Divorced', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Separated', 'Separated', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Widow', 'Widow', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Widower', 'Widower', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Male', 'Male', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Female', 'Female', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Other', 'Transgender', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Cultivation(Agriculture)', 'Self', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Agricultural labour', 'Private', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Business', 'Self', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Student', 'Unemployed', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Govt employee', 'Government', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Private employee', 'Private', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Other', 'Self', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Not Applicable', 'Unemployed', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Housewife or Homemaker', 'Unemployed', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Primary (1st to 5th std)', 'Illiterate', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Primary (1st to 5th std)', 'Non-metric', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Upper Primary (6th to 8th std)', 'Non-metric', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Secondary (9th to 10th)', 'Metric', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Senior Secondary (11th to 12th/Intermediate)', 'Inter', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Diploma/Under Graduate', 'Graduate', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Graduate', 'Graduate', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Post Graduate', 'Post graduate', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('General', 'General', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('SC', 'SC', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('ST', 'ST', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('EBC', 'OC', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('OBC', 'OBC', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Not given', 'Not given', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Hindu', 'Hindu', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Muslim', 'Muslim', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Christian', 'Christian', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Sikh', 'Sikh', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Buddism', 'Buddhism', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Jainism', 'Jainism', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Parsi', 'Parsi', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Other', 'Other', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Not Disclosed', 'Not disclosed', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Aadhar', 'Aadhar No', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('ABHA id', null, null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('None', null, null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations'));



