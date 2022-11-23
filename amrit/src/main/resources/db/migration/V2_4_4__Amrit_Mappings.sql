alter table public.mapping_metadata
    alter column int_system_value type varchar(1000) using int_system_value::varchar(1000),
    alter column avni_value type varchar(1000) using avni_value::varchar(1000);

INSERT INTO public.mapping_metadata (int_system_value, avni_value, about, data_type_hint, integration_system_id,
                                     mapping_group_id, mapping_type_id)
VALUES
    ('anyNodulesOnSkin', 'Any nodules on skin', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('anyThickenedSkin', 'Any thickened skin', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('bleedingAfterMenopause', 'Bleeding after menopause', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('bleedingBetweenPeriods', 'Bleeding between periods', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('bloodInSputum', 'Blood in sputum', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('bloodStainedDischargeFromNipple', 'Blood stained discharge from the nipple', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('cbacFamilyhistory', 'Do you have a family history (any one of your parents or siblings) of high blood pressure, diabetes and heart disease?', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('changeInShapeAndSizeOfBreast', 'Change in shape and size of the breast', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('changeInToneOfyourVoice', 'Any change in tone of your voice', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('clawingOfFingers', 'Clawing of fingers in hand(s) or feet', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('cloudyOrBlurredVision', 'Cloudy or blurred vision', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('collectSputumSample', 'Collect Sputum sample and transport to nearest TB testing center', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('consumeAlcoholDaily', 'Do you consume alcohol daily?', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('coughingMoreThanTwoweeks', 'Coughing more than 2 weeks', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('currentlySufferingFromTB', 'Anyone in family currently suffering from TB?**', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('currentlyTakingAntiTBDrugs', 'Are you currently taking anti-TB drugs?', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('difficultyInBreathing', 'Difficulty in breathing', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('difficultyInHearing', 'Difficulty in hearing', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('difficultyInHoldingObject', 'Difficulty in holding object with fingers', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('difficultyInOpeningMouth', 'Difficulty in opening mouth', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('feelingDownDpressedHopeless', 'Feeling down, depressed or hopeless', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('feverForTwoWeeks', 'Fever for > 2 weeks', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('foulSmellingVaginalDischarge', 'Foul smelling vaginal discharge', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('historyOfFits', 'History of fits', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('historyOfTB', 'History of TB', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('hyperPigmentedPatchORDiscoloration', 'Any hyper pigmented patch or discoloration on skin with loss of sensation', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('inabilityToCloseEyelid', 'Inability to close eyelid', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('littleInterestOrPleasure', 'Little interest or pleasure in doing things', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('lossOfWeight', 'Loss of weight', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('lumpInBreast', 'Lump in breast', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('measurementOfWaistInCM', 'Measurement of waist in cm', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('nightSweats', 'Night sweats', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('occupationalExposure', 'Occupational exposure', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('painInEyes', 'Pain in eyes lasting for more than a week', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('painWhileChewing', 'Pain while chewing', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('personAtRiskPrioritizedForNCD', 'Person at risk and needs to be prioritized for attending the weekly NCD day', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('recurrentNumbnessOnPalmSole', 'Recurrent numbness on palm(s) or sole(s)', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('recurrentTinglingOnPalmOrsole', 'Recurrent tingling on palm(s) or sole(s)', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('recurrentUlcerationOnPalmOrSole', 'Recurrent ulceration on palm or sole', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('rednessInEyes', 'Redness in eyes lasting for more than a week', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('referThePatientImmediately', 'Refer the patient immediately to the nearest facility where a Medical Officer is available', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('shortnessOfBreath', 'Shortness of breath', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('smokeOrConsumeGutkaOrKhaini', 'Do you smoke or consume smokeless products such as gutka or khaini?', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('totalCbacRiskAssessmentScore', 'Total CBAC Risk Assessment Score', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('tracingFamilyMembers', 'Tracing of all family members to be done by ANM/MPW', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('typeOfFuelUsedForCooking', 'Type of fuel used for cooking', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('udertakeAnyPhysicalActivities', 'Do you undertake any physical activities for minimum of 150 minutes in a week?', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('ulcerPatchGrowthInMouth', 'Ulcer/patch/growth in mouth that has not healed in two weeks', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('weaknessInFeet', 'Weakness in feet that causes difficulty in walking', null, null, 3, (select id from public.mapping_group where name = 'CBAC'), (select id from public.mapping_type where name = 'CBACRoot')),

    ('birthOrder', 'Birth order of the child', null, null, 3, (select id from public.mapping_group where name = 'BornBirth'), (select id from public.mapping_type where name = 'BornBirthRoot')),

    ('colostrum', 'Colostrum given to the child', null, null, 3, (select id from public.mapping_group where name = 'BornBirth'), (select id from public.mapping_type where name = 'BornBirthRoot')),

    ('nutritionalStatus', 'Nutritional Status', null, null, 3, (select id from public.mapping_group where name = 'BornBirth'), (select id from public.mapping_type where name = 'BornBirthRoot')),

    ('weightAtBirth', 'Birth weight of the baby in kgs', null, null, 3, (select id from public.mapping_group where name = 'BornBirth'), (select id from public.mapping_type where name = 'BornBirthRoot')),

    ('addressOne', 'Address 1', null, null, 3, (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot')),

    ('addressTwo', 'Address 2', null, null, 3, (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot')),

    ('availabilityOfElectricity', 'Availability of electricity', null, 'Text', 3, (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot')),

    ('availabilityOfToilet', 'Availability of toilet', null, null, 3, (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot')),

    ('fuelType', 'Type of fuel used for cooking', null, 'Text', 3, (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot')),

    ('houseOwnership', 'House ownership', null, null, 3, (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot')),

    ('houseType', 'Type of house', null, null, 3, (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot')),

    ('otherAvailabilityOfElectricity', 'Other availability of electricity', null, null, 3, (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot')),

    ('otherAvailabilityOfToilet', 'Other availability of toilet', null, null, 3, (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot')),

    ('otherHouseType', 'Other type of house', null, null, 3, (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot')),

    ('otherSourceOfWater', 'Other source of water', null, null, 3, (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot')),

    ('otherTypeOfFuelUsed', 'Other type of fuel used for cooking', null, null, 3, (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot')),

    ('primarySourceOfWater', 'Primary source of water', null, 'Text', 3, (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot')),

    ('separateKitchen', 'Separate kitchen', null, null, 3, (select id from public.mapping_group where name = 'Household'), (select id from public.mapping_type where name = 'HouseholdRoot'))

;