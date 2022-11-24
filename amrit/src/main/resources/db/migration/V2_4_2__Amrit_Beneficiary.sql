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
  ('maritalStatusID'::varchar(250), 'Marital status'::varchar(250), null::varchar(1000), 'Numeric'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
  ('maritalStatusName'::varchar(250), 'Marital status'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
  ('genderID'::varchar(250), 'Gender'::varchar(250), null::varchar(1000), 'Numeric'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
  ('genderName'::varchar(250), 'Gender'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
  ('occupationID'::varchar(250), 'Occupation'::varchar(250), null::varchar(1000), 'Numeric'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryDemographics')),
  ('educationID'::varchar(250), 'Educational qualification'::varchar(250), null::varchar(1000), 'Numeric'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryDemographics')),
  ('educationName'::varchar(250), 'Educational qualification'::varchar(250), null::varchar(1000), 'Coded'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryDemographics')),
  ('communityID'::varchar(250), 'Caste'::varchar(250), null::varchar(1000), 'Numeric'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryDemographics')),
  ('religionID'::varchar(250), 'Religion'::varchar(250), null::varchar(1000), 'Numeric'::varchar(100), 3,
         (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryDemographics')),
  ('govtIdentityNo'::varchar(250), 'Aadhar number'::varchar(250), null::varchar(1000), null::varchar(100), 3,
   (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryIdentity')),
  ('govtIdentityTypeID'::varchar(250), 'Enrol with'::varchar(250), null::varchar(1000), 'Numeric'::varchar(100), 3,
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
 ('Govt employee', 'Government', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Private employee', 'Private', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Other', 'Self', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Not Applicable', 'Unemployed', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Primary (1st to 5th std)', 'Illiterate', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Upper Primary (6th to 8th std)', 'Non-metric', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Secondary (9th to 10th)', 'Metric', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
 ('Senior Secondary (11th to 12th/Intermediate)', 'Inter', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations')),
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
 ('Aadhar', 'Aadhar No', null, null, 3, (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryObservations'))
;


INSERT INTO public.mapping_group (name, integration_system_id)
VALUES ('MasterId'::varchar(250), 3::integer);
---------------------------Mapping Type------------------
INSERT INTO public.mapping_type (name, integration_system_id)
VALUES ('occupationID'::varchar(250), 3::integer);
INSERT INTO public.mapping_type (name, integration_system_id)
VALUES ('incomeID'::varchar(250), 3::integer);
INSERT INTO public.mapping_type (name, integration_system_id)
VALUES ('maritalStatusID'::varchar(250), 3::integer);
INSERT INTO public.mapping_type (name, integration_system_id)
VALUES ('religionID'::varchar(250), 3::integer);
INSERT INTO public.mapping_type (name, integration_system_id)
VALUES ('communityID'::varchar(250), 3::integer);
INSERT INTO public.mapping_type (name, integration_system_id)
VALUES ('educationID'::varchar(250), 3::integer);
INSERT INTO public.mapping_type (name, integration_system_id)
VALUES ('govtIdentityTypeID'::varchar(250), 3::integer);
INSERT INTO public.mapping_type (name, integration_system_id)
VALUES ('genderID'::varchar(250), 3::integer);
INSERT INTO public.mapping_type (name, integration_system_id)
VALUES ('otherGovIdEntityMaster'::varchar(250), 3::integer);

-----------------------Metadata--------------
INSERT INTO public.mapping_metadata (int_system_value, avni_value, about, data_type_hint, integration_system_id,
                                     mapping_group_id, mapping_type_id)
VALUES
('3'::varchar(250), 'Self'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'occupationID')),
('5'::varchar(250), 'Government'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'occupationID')),
('6'::varchar(250), 'Private'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'occupationID')),
('4'::varchar(250), 'Unemployed'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'occupationID')),


('1'::varchar(250), 'APL'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'incomeID')),
('2'::varchar(250), 'BPL'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'incomeID')),
('3'::varchar(250), 'Don''t Know'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'incomeID')),


('3'::varchar(250), 'Divorced'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'maritalStatusID')),
('2'::varchar(250), 'Married'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'maritalStatusID')),
('4'::varchar(250), 'Separated'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'maritalStatusID')),
('1'::varchar(250), 'Unmarried'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'maritalStatusID')),
('5'::varchar(250), 'Widow'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'maritalStatusID')),
('6'::varchar(250), 'Widower'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'maritalStatusID')),


('5'::varchar(250), 'Buddhism'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'religionID')),
('3'::varchar(250), 'Christian'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'religionID')),
('1'::varchar(250), 'Hindu'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'religionID')),
('6'::varchar(250), 'Jainism'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'religionID')),
('2'::varchar(250), 'Muslim'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'religionID')),
('9'::varchar(250), 'Not disclosed'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'religionID')),
('7'::varchar(250), 'Other'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'religionID')),
('8'::varchar(250), 'Parsi'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'religionID')),
('4'::varchar(250), 'Sikh'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'religionID')),


('4'::varchar(250), 'BC'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'communityID')),
('1'::varchar(250), 'General'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'communityID')),
('7'::varchar(250), 'Not given'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'communityID')),
('5'::varchar(250), 'OBC'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'communityID')),
('6'::varchar(250), 'OC'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'communityID')),
('2'::varchar(250), 'SC'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'communityID')),
('3'::varchar(250), 'ST'::varchar(250), null::varchar(1000), null::varchar(100), 3,
(select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'communityID')),

('7'::varchar(250), 'Graduate'::varchar(250), null::varchar(1000), null::varchar(100), 3,
    (select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'educationID')),
('8'::varchar(250), 'Post graduate'::varchar(250), null::varchar(1000), null::varchar(100), 3,
    (select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'educationID')),
('2'::varchar(250), 'Illiterate'::varchar(250), null::varchar(1000), null::varchar(100), 3,
    (select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'educationID')),
('4'::varchar(250), 'Metric'::varchar(250), null::varchar(1000), null::varchar(100), 3,
    (select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'educationID')),
('5'::varchar(250), 'Inter'::varchar(250), null::varchar(1000), null::varchar(100), 3,
    (select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'educationID')),
('3'::varchar(250), 'Non-metric'::varchar(250), null::varchar(1000), null::varchar(100), 3,
    (select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'educationID')),

('1'::varchar(250), 'Aadhar No'::varchar(250), null::varchar(1000), null::varchar(100), 3,
 (select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'govtIdentityTypeID')),

('2'::varchar(250), 'Female'::varchar(250), null::varchar(1000), null::varchar(100), 3,
 (select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'genderID')),
('1'::varchar(250), 'Male'::varchar(250), null::varchar(1000), null::varchar(100), 3,
 (select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'genderID')),
('3'::varchar(250), 'Other'::varchar(250), null::varchar(1000), null::varchar(100), 3,
 (select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'genderID'))

;

INSERT INTO public.mapping_metadata (int_system_value, avni_value, about, data_type_hint, integration_system_id,
                                     mapping_group_id, mapping_type_id)
VALUES
    ('fatherName'::varchar(250), 'Father''s name'::varchar(250), null::varchar(1000), null::varchar(100), 3,
     (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
    ('bankName'::varchar(250), 'Bank name'::varchar(250), null::varchar(1000), null::varchar(100), 3,
     (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
    ('ifscCode'::varchar(250), 'IFSC Code'::varchar(250), null::varchar(1000), null::varchar(100), 3,
     (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
    ('accountNo'::varchar(250), 'Bank Account No.'::varchar(250), null::varchar(1000), null::varchar(100), 3,
     (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
    ('ageAtMarriage'::varchar(250), 'Age at marriage'::varchar(250), null::varchar(1000), null::varchar(100), 3,
     (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryRoot')),
    ('phoneNo'::varchar(250), 'Phone number'::varchar(250), null::varchar(1000), null::varchar(100), 3,
        (select id from public.mapping_group where name = 'Beneficiary'), (select id from public.mapping_type where name = 'BeneficiaryPhoneMaps')),
    ('6'::varchar(250), 'EBC'::varchar(250), null::varchar(1000), null::varchar(100), 3,
        (select id from public.mapping_group where name = 'MasterId'), (select id from public.mapping_type where name = 'communityID'));
