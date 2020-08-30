INSERT INTO categories (category_id, category_name) VALUES (1, 'Computer');
INSERT INTO categories (category_id, category_name) VALUES (2, 'CellPhone');
INSERT INTO categories (category_id, category_name) VALUES (3, 'Camera');


INSERT INTO products (product_id, product_number, product_name, product_price, product_description, product_stock, create_at, category_id)
VALUES (1, 9632587, 'Iphone X', 1156.60, 'Apple IOS', 15, '01-01-2001', 2);

INSERT INTO products (product_id, product_number, product_name, product_price, product_description, product_stock, create_at, category_id)
VALUES (2, 1232587, 'Toshiba', 750.00, 'Core i7', 10, '01-01-2006', 1);

INSERT INTO products (product_id, product_number, product_name, product_price, product_description, product_stock, create_at, category_id)
VALUES (3, 9632512, 'Samsung', 300.45, 'Personal Camera', 20, '01-01-2010', 3);

INSERT INTO sub_categories (sub_category_id, sub_category_name, category_id) VALUES (1, 'Laptop', 1);
INSERT INTO sub_categories (sub_category_id, sub_category_name, category_id) VALUES (2, '1600 Mpx', 3);
INSERT INTO sub_categories (sub_category_id, sub_category_name, category_id) VALUES (3, 'Iphone', 2)


INSERT INTO sub_categories_products (sub_category_id, product_id) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 1 AND products.product_id = 2;

INSERT INTO sub_categories_products (sub_category_id, product_id) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 2 AND products.product_id = 3;

INSERT INTO sub_categories_products (sub_category_id, product_id) SELECT sub_categories.sub_category_id, products.product_id
FROM sub_categories inner join products ON sub_categories.sub_category_id = 3 AND products.product_id = 1;
