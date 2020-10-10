CREATE TABLE IF NOT EXISTS users (
      id identity NOT NULL,
      user_name character varying(60) NOT NULL,
      password character varying(60) NOT NULL,
      role character varying(10) NOT NULL,
      check (role in ('CUSTOMER', 'EMPLOYEE', 'ADMIN')));
