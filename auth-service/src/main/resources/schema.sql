CREATE TABLE IF NOT EXISTS users (
      id identity NOT NULL,
      user_name character varying(60) NOT NULL,
      user_password character varying(60) NOT NULL,
      user_role character varying(10) NOT NULL,
      check (user_role in ('CUSTOMER', 'EMPLOYEE', 'ADMIN')));
