INSERT INTO error_type (name, integration_system_id, comparison_operator, comparison_value)
VALUES ('NoDemandWithId'::varchar(250), 2::integer, 2,
        'Individual not found with UUID ''null'' or External ID ''.*''');

INSERT INTO error_type (name, integration_system_id, comparison_operator, comparison_value)
VALUES ('BadValueForRestrictedPicklist'::varchar(250), 2::integer, 1,
        'INVALID_OR_NULL_FOR_RESTRICTED_PICKLIST');

INSERT INTO error_type (name, integration_system_id, comparison_operator, comparison_value)
VALUES ('MustNotHave2SimilarElements'::varchar(250), 2::integer, 1,
        'System.ListException: Before Insert or Upsert list must not have two identically equal elements');

INSERT INTO error_type (name, integration_system_id, comparison_operator, comparison_value)
VALUES ('FieldCustomValidationException'::varchar(250), 2::integer, 1,
        'FIELD_CUSTOM_VALIDATION_EXCEPTION');



