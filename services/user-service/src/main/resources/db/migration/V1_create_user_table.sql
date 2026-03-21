CREATE SCHEMA IF NOT EXISTS users;

CREATE TABLE users."user"(
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email character varying(40) NOT NULL,
    name character varying(10) NOT NULL,
    surname character varying(15) NOT NULL,
    password  character varying(255) NOT NULL,
    CONSTRAINT user_email_key UNIQUE (email)
);
