create table users
(
    id       SERIAL PRIMARY KEY,
    email    CHARACTER VARYING(250) not null,
    password CHARACTER VARYING(250) not null
);
