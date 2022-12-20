alter table error_record_log
    add column IF NOT EXISTS error_msg TEXT DEFAULT null;

alter table error_type
    add column IF NOT EXISTS comparison_operator VARCHAR(255) DEFAULT null,
    add column IF NOT EXISTS comparison_value TEXT DEFAULT null;

