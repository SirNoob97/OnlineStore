CREATE TABLE IF NOT EXISTS products(
  product_id bigint NOT NULL,
  product_bar_code bigint NOT NULL,
  product_name character varying(130) NOT NULL,
  product_price real NOT NULL,
  product_description text,
  product_stock integer NOT NULL,
  product_status character varying(10) NOT NULL,
  created_date date NOT NULL DEFAULT CURRENT_DATE,
  last_modified_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  main_category_id bigint NOT NULL,
  PRIMARY KEY (product_id),
  CONSTRAINT unique_product_name UNIQUE (product_name),
  CONSTRAINT unique_product_bar_code UNIQUE (product_bar_code));

ALTER TABLE products ALTER COLUMN product_id ADD GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT 1);

CREATE EXTENSION moddatetime;

CREATE TRIGGER products_moddatetime
  BEFORE UPDATE ON products
  FOR EACH ROW
  EXECUTE PROCEDURE moddatetime(last_modified_date);


CREATE TABLE IF NOT EXISTS main_categories(
  category_id bigint NOT NULL,
  category_name character varying(50) NOT NULL,
  PRIMARY KEY (category_id),
  CONSTRAINT unique_category_name UNIQUE (category_name));

ALTER TABLE main_categories ALTER COLUMN category_id ADD GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT 1);


CREATE TABLE IF NOT EXISTS sub_categories(
  sub_category_id bigint NOT NULL,
  sub_category_name character varying(50) NOT NULL,
  main_category_id bigint NOT NULL,
  PRIMARY KEY (sub_category_id),
  CONSTRAINT unique_sub_category_name UNIQUE (sub_category_name));

ALTER TABLE sub_categories ALTER COLUMN sub_category_id ADD GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT 1);


ALTER TABLE products ADD CONSTRAINT products_fk_main_category FOREIGN KEY(main_category_id) REFERENCES main_categories(category_id) ON DELETE CASCADE;

ALTER TABLE sub_categories ADD CONSTRAINT sub_categories_fk_main_category FOREIGN KEY(main_category_id) REFERENCES main_categories(category_id) ON DELETE CASCADE;


CREATE TABLE sub_categories_products(
  fk_sub_category BIGINT REFERENCES sub_categories(sub_category_id) ON DELETE CASCADE,
  fk_product BIGINT REFERENCES products(product_id),
  CONSTRAINT sub_category_product_pkey PRIMARY KEY(fk_sub_category, fk_product));


INSERT INTO main_categories (category_name) VALUES ('Computers & Accessories');
INSERT INTO main_categories (category_name) VALUES ('Electronics');
INSERT INTO main_categories (category_name) VALUES ('Software');

INSERT INTO sub_categories (sub_category_name, main_category_id) VALUES ('Mice', 1);
INSERT INTO sub_categories (sub_category_name, main_category_id) VALUES ('Smartwatches', 2);
INSERT INTO sub_categories (sub_category_name, main_category_id) VALUES ('Monitors', 2);
INSERT INTO sub_categories (sub_category_name, main_category_id) VALUES ('Web Cameras', 1);
INSERT INTO sub_categories (sub_category_name, main_category_id) VALUES ('Operating System', 3);
INSERT INTO sub_categories (sub_category_name, main_category_id) VALUES ('Image Edition', 3);
INSERT INTO sub_categories (sub_category_name, main_category_id) VALUES ('Laptops', 1);
INSERT INTO sub_categories (sub_category_name, main_category_id) VALUES ('Keyboards', 1);
INSERT INTO sub_categories (sub_category_name, main_category_id) VALUES ('Smartphones', 2);
INSERT INTO sub_categories (sub_category_name, main_category_id) VALUES ('Portable Chargers', 2);
INSERT INTO sub_categories (sub_category_name, main_category_id) VALUES ('Audio Mixer', 3);
INSERT INTO sub_categories (sub_category_name, main_category_id) VALUES ('Smart Divices', 2);
INSERT INTO sub_categories (sub_category_name, main_category_id) VALUES ('Gaming Accessories', 1);


INSERT INTO products (product_bar_code, product_name, product_price, product_description, product_stock, product_status, main_category_id)
VALUES (6012329632587, 'Samsung Gear S3 Frontier Smartwatch (Bluetooth), SM-R760NDAAXAR', 178.99, 'A distinctive steel bezel that you can rotate to access apps and notifications, strap with buckle: 2.76 inches, large strap with holes: 5.12 inches, small strap with holes: 4.33 inches', 15, 'CREATED', 2);

INSERT INTO products (product_bar_code, product_name, product_price, product_description, product_stock, product_status, main_category_id)
VALUES (1232584561287, 'Logitech G203 Prodigy RGB Wired Gaming Mouse – Black', 48.35, 'Prodigy Series Logitech G mouse for advanced gaming grade performance up-to 8 times faster response than standard mice so every mouse click and move is near instantaneous from hand to screen', 10, 'CREATED', 1);

INSERT INTO products (product_bar_code, product_name, product_price, product_description, product_stock, product_status, main_category_id)
VALUES (9632632456512, 'Sceptre IPS 24” Gaming Monitor 165Hz 144Hz Full HD (1920 x 1080)', 179.45, 'Up to 165Hz Refresh Rate: Pushing beyond the standard 144Hz, 165Hz gives gamers an edge in visibility as frames transition instantly, leaving behind no blurred images', 20, 'CREATED', 2);

INSERT INTO products (product_bar_code, product_name, product_price, product_description, product_stock, product_status, main_category_id)
VALUES (1632632456512, 'PICTEK Gaming Mouse Wired [7200 DPI] [Programmable] [Breathing Light] Ergonomic Game USB Computer Mice', 15.99, '[Excellent gaming performance with 7200 DPI and 4 polling rate] PICTEK T7 programmable gaming mice, default five DPI levels available from 1200/2400/3500/5500/7200 DPI. With two DPI button, you can adjust the dpi easily to get high accuracy and consistent responsiveness at any speed. 4 polling rate is adjustable: 125Hz/250Hz/500Hz/1000Hz, polling rate ensures smooth AND high-speed movement, up to 1000Hz makes it faster and more accurate than ordinary mouse', 20, 'CREATED', 1);

INSERT INTO products (product_bar_code, product_name, product_price, product_description, product_stock, product_status, main_category_id)
VALUES (2106326324565, 'AOC C24G1 24" Curved Frameless Gaming Monitor, FHD 1080p, 1500R VA panel, 1ms 144Hz', 14.99, 'AOC Gaming 24" Class, 23. 6" Viewable AOC Gaming monitor with 1920 x 1080 Full HD resolution', 20, 'CREATED', 2);

INSERT INTO products (product_bar_code, product_name, product_price, product_description, product_stock, product_status, main_category_id)
VALUES (3632632456512, 'Garmin vívoactive 3, GPS Smartwatch Contactless Payments Built-In Sports Apps, Black/Slate', 118.98, 'More than 15 pre loaded GPS and indoor sports apps, including yoga, running, swimming and more; Lens material: chemically strengthened glass, bezel material: stainless steel, case material: fiber reinforced polymer with stainless steel rear cover', 20, 'CREATED', 2);

INSERT INTO products (product_bar_code, product_name, product_price, product_description, product_stock, product_status, main_category_id)
VALUES (3600326456512, '2020 AutoFocus 1080p Streaming Webcam with Stereo Microphone and Privacy Cover, NexiGo FHD USB Web Camera', 54.99, '[HD Webcam 1080p Autofocus] The NexiGo PC Webcam with a 2-megapixel CMOS features up to a 1080p resolution at 30 fps. With a 7cm starting point and a nearly infinite range, the autofocus feature is able to ensure your videos are always sharp and crystal clear. Facial enhancement features ensure your best side is always the one in the image', 20, 'CREATED', 1);

INSERT INTO products (product_bar_code, product_name, product_price, product_description, product_stock, product_status, main_category_id)
VALUES (4812329632587, 'Windows 10 Professional - 32/64 bit - USB - Full Product - 2020', 139.99, 'Windows 10 Pro will be delivered by Amazon courier in an official sealed box with USB flash drive inside', 15, 'CREATED', 3);

INSERT INTO products (product_bar_code, product_name, product_price, product_description, product_stock, product_status, main_category_id)
VALUES (5812329632587, 'Adobe Photoshop Elements 2020', 99.99, 'Adobe Sensei AI technology powers automated options that make it a snap to get started, and there’s always room to add your personal touch', 15, 'CREATED', 3);

INSERT INTO products (product_bar_code, product_name, product_price, product_description, product_stock, product_status, main_category_id)
VALUES (6812329632587, 'Lenovo IdeaPad 3 14" Laptop, 14.0" FHD (1920 x 1080) Display, AMD Ryzen 5 3500U', 449.99, 'AMD Ryzen 5 3500U Mobile Processors with Radeon Graphics deliver powerful performance for everyday tasks', 15, 'CREATED', 1);

INSERT INTO products (product_bar_code, product_name, product_price, product_description, product_stock, product_status, main_category_id)
VALUES (7812329632587, 'Redragon K552 Mechanical Gaming Keyboard RGB LED Rainbow Backlit Wired Keyboard', 37.99, 'Tenkeyless compact mechanical gaming keyboard Redragon k552 tkl small compact with dust proof mechanical switches cherry mx red equivalent Linear switches quiet click sound fast action with minimal resistance without a tactile bump feel', 15, 'CREATED', 1);

INSERT INTO products (product_bar_code, product_name, product_price, product_description, product_stock, product_status, main_category_id)
VALUES (8812329632587, 'Samsung Galaxy S9, 64GB, Midnight Black', 294.99, 'This pre-owned product has been professionally inspected, tested and cleaned by Amazon-qualified suppliers', 15, 'CREATED', 2);

INSERT INTO products (product_bar_code, product_name, product_price, product_description, product_stock, product_status, main_category_id)
VALUES (9812329632587, 'Anker PowerCore 10000 Portable Charger', 21.99, 'The Anker Advantage: Join the 50 million+ powered by our leading technology', 15, 'CREATED', 2);

INSERT INTO products (product_bar_code, product_name, product_price, product_description, product_stock, product_status, main_category_id)
VALUES (1012329632587, 'Image Line FL Studio 20 Producer Edition', 199.99, 'Powerful mixing and automation', 15, 'CREATED', 3);


INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 2 AND products.product_id = 1;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 1 AND products.product_id = 2;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 3 AND products.product_id = 3;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 1 AND products.product_id = 4;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 3 AND products.product_id = 5;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 2 AND products.product_id = 6;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 4 AND products.product_id = 7;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 5 AND products.product_id = 8;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 6 AND products.product_id = 9;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 7 AND products.product_id = 10;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 8 AND products.product_id = 11;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 9 AND products.product_id = 12;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 10 AND products.product_id = 13;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 11 AND products.product_id = 14;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 12 AND products.product_id = 12;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 12 AND products.product_id = 6;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 12 AND products.product_id = 1;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 13 AND products.product_id = 2;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 13 AND products.product_id = 3;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 13 AND products.product_id = 4;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 13 AND products.product_id = 5;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 13 AND products.product_id = 11;
