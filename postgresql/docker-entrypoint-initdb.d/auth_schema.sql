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
  refresh_token character varying(1024) NOT NULL,
  access_token character varying(1024) NOT NULL,
  PRIMARY KEY(id),
  CONSTRAINT unique_rtoken UNIQUE (refresh_token),
  CONSTRAINT unique_atoken UNIQUE (access_token));


INSERT INTO users (user_name, password, email, role) VALUES ('martin','$2a$10$Skf1kNT607efc5W4duBvhu3XHKmkgRgP1LpsojsSm7/veunSY1GDW', 'martin@email.com', 'CUSTOMER');
INSERT INTO users (user_name, password, email, role) VALUES ('employee','$2a$10$ohLOHQxl6i9zhwHwgQI9U.qpRihIdQaBANWFqLV2cS67uHiwX5Kp.', 'useremail@mail.com', 'EMPLOYEE');
INSERT INTO users (user_name, password, email, role) VALUES ('test','$2a$10$Nku2UGt.uENIURSUl/3WHOZe8Sekf2jxoJiqZsjPNFZuJZCedmOsS', 'test@email.com', 'ADMIN');
