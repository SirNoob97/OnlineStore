INSERT INTO main_categories (category_id, category_name) VALUES (1, 'Computer');
INSERT INTO main_categories (category_id, category_name) VALUES (2, 'CellPhone');
INSERT INTO main_categories (category_id, category_name) VALUES (3, 'Camera');


INSERT INTO products (product_id, product_bar_code, product_name, product_price, product_description, product_stock, product_status, create_at, category_id)
VALUES (1, 7812329632587, 'Iphone X', 1156.60, 'Apple IOS', 15, 'CREATED', '2001-01-01', 2);

INSERT INTO products (product_id, product_bar_code, product_name, product_price, product_description, product_stock, product_status, create_at, category_id)
VALUES (2, 1232584561287, 'Toshiba', 750.00, 'Core i7', 10, 'CREATED', '2006-01-01', 1);

INSERT INTO products (product_id, product_bar_code, product_name, product_price, product_description, product_stock, product_status, create_at, category_id)
VALUES (3, 9632632456512, 'Samsung', 300.45, 'Personal Camera', 20, 'CREATED', '2010-01-01', 3);

INSERT INTO sub_categories (sub_category_id, sub_category_name, category_id) VALUES (1, 'Laptop', 1);
INSERT INTO sub_categories (sub_category_id, sub_category_name, category_id) VALUES (2, '1600 Mpx', 3);
INSERT INTO sub_categories (sub_category_id, sub_category_name, category_id) VALUES (3, 'Iphone', 2);


INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 1 AND products.product_id = 2;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 2 AND products.product_id = 3;

INSERT INTO sub_categories_products (fk_sub_category, fk_product) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 3 AND products.product_id = 1;
