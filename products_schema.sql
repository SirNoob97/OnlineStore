CREATE TABLE IF NOT EXISTS products(
  product_id bigint NOT NULL,
  product_number integer NOT NULL,
  product_name character varying(30) NOT NULL,
  product_price real NOT NULL,
  product_description text,
  product_stock integer NOT NULL,
  create_at date NOT NULL DEFAULT CURRENT_DATE,
  last_modified_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  category_id bigint NOT NULL,
  PRIMARY KEY (product_id),
  CONSTRAINT unique_product_name UNIQUE (product_name),
  CONSTRAINT unique_product_number UNIQUE (product_number));

CREATE SEQUENCE sequence_product_id INCREMENT 1 MINVALUE 1;
ALTER TABLE products ALTER COLUMN product_id SET DEFAULT NEXTVAL('sequence_product_id');

CREATE EXTENSION moddatetime;

CREATE TRIGGER products_moddatetime
  BEFORE UPDATE ON products
  FOR EACH ROW
  EXECUTE PROCEDURE moddatetime(last_modified_date);


CREATE TABLE IF NOT EXISTS main_categories(
  category_id bigint NOT NULL,
  category_name character varying(30) NOT NULL,
  PRIMARY KEY (category_id),
  CONSTRAINT unique_category_name UNIQUE (category_name));

CREATE SEQUENCE sequence_main_category_id INCREMENT 1 MINVALUE 1;
ALTER TABLE main_categories ALTER COLUMN category_id SET DEFAULT NEXTVAL('sequence_main_category_id');


CREATE TABLE IF NOT EXISTS sub_categories(
  sub_category_id bigint NOT NULL,
  sub_category_name character varying(30) NOT NULL,
  category_id bigint NOT NULL,
  PRIMARY KEY (sub_category_id),
  CONSTRAINT unique_sub_category_name UNIQUE (sub_category_name));

CREATE SEQUENCE sequence_sub_category_id INCREMENT 1 MINVALUE 1;
ALTER TABLE sub_categories ALTER COLUMN sub_category_id SET DEFAULT NEXTVAL('sequence_sub_category_id');


ALTER TABLE products ADD CONSTRAINT products_fk_main_category FOREIGN KEY(category_id) REFERENCES main_categories(category_id);

ALTER TABLE sub_categories ADD CONSTRAINT sub_categories_fk_main_category FOREIGN KEY(category_id) REFERENCES main_categories(category_id);


CREATE TABLE sub_categories_products(
  sub_category_id BIGINT REFERENCES sub_categories(sub_category_id) ON DELETE CASCADE,
  product_id BIGINT REFERENCES products(product_id),
  CONSTRAINT sub_category_product_pkey PRIMARY KEY(sub_category_id, product_id));
