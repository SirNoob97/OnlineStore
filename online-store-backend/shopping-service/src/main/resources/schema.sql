CREATE TABLE IF NOT EXISTS invoices (
  invoice_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT 1),
  invoice_number bigint NOT NULL,
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  user_name character varying(60) NOT NULL,
  user_email character varying(60) NOT NULL,
  total decimal(10,2) NOT NULL,
  PRIMARY KEY (invoice_id) ,
  CONSTRAINT unique_number_invoice UNIQUE (invoice_number)
);

CREATE TABLE IF NOT EXISTS items (
  item_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT 1),
  quantity integer NOT NULL,
  product_bar_code bigint NOT NULL,
  product_name character varying(130) NOT NULL,
  product_price decimal(5,2) NOT NULL,
  sub_total decimal(7,2) NOT NULL,
  fk_invoice_id bigint,
  PRIMARY KEY (item_id)
);

CREATE TABLE invoices_items(
  fk_invoice BIGINT REFERENCES invoices(invoice_id) ON DELETE CASCADE,
  fk_item BIGINT REFERENCES items(item_id),
  CONSTRAINT invoices_items_pkey PRIMARY KEY(fk_invoice, fk_item));
