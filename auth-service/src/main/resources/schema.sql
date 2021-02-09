CREATE TABLE IF NOT EXISTS users (
      id bigint NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT 1),
      user_name character varying(60) NOT NULL,
      password character varying(60) NOT NULL,
      email character varying(60) NOT NULL,
      role character varying(10) NOT NULL,
      PRIMARY KEY(id),
      CONSTRAINT unique_username UNIQUE (user_name),
      CONSTRAINT unique_email UNIQUE (email),
      check (role in ('CUSTOMER', 'EMPLOYEE', 'ADMIN')));

CREATE TABLE IF NOT EXISTS tokens (
      id bigint NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT 1),
      refresh-token character varying(512) NOT NULL,
      access-token character varying(512) NOT NULL,
      PRIMARY KEY(id),
      CONSTRAINT unique_rtoken UNIQUE (refresh-token),
      CONSTRAINT unique_atoken UNIQUE (access-token));
