create table integration_system_config
(
    id                    SERIAL PRIMARY KEY,
    integration_system_id int not null references integration_system (id),
    key                   CHARACTER VARYING(255),
    value                 CHARACTER VARYING(10000)
);

alter table integration_system add column system_type character varying(255) not null default '';
update integration_system set system_type = name where 1 = 1;
