CREATE TABLE IF NOT EXISTS invoices (
  invoice_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT 1),
  invoice_number bigint NOT NULL,
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  user_name character varying(60) NOT NULL,
  email character varying(60) NOT NULL,
  total real NOT NULL,
  PRIMARY KEY (invoice_id) ,
  CONSTRAINT unique_user_name UNIQUE (user_name),
  CONSTRAINT unique_email UNIQUE (email),
  CONSTRAINT unique_number_invoice UNIQUE (invoice_number)
);

CREATE TABLE IF NOT EXISTS items (
  item_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT 1),
  quantity integer NOT NULL,
  product_name character varying(130) NOT NULL,
  product_price real NOT NULL,
  sub_total real,
  PRIMARY KEY (item_id),
  CONSTRAINT unique_product_name UNIQUE (product_name)
);
