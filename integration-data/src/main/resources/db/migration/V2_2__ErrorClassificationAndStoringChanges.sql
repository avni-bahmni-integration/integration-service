alter table error_record_log
    add column IF NOT EXISTS  error_msg TEXT DEFAULT null;

alter table avni_int.public.error_type
    add column IF NOT EXISTS comparison_operator VARCHAR(255) DEFAULT null,
    add column IF NOT EXISTS comparison_value TEXT DEFAULT null;

INSERT INTO avni_int.public.error_type (name, integration_system_id, comparison_operator, comparison_value)
VALUES ('NoDemandWithId'::varchar(250), 2::integer, 2,
        'Individual not found with UUID ''null'' or External ID ''.*''');

