INSERT INTO addresses (address_id, address, zip_code) VALUES (1, 'first red door of that street', 74859);
INSERT INTO addresses (address_id, address, zip_code) VALUES (2, 'second red door of that street', 74568);
INSERT INTO addresses (address_id, address, zip_code) VALUES (3, 'TEST FETCH BY ADDRESS', 14253);


INSERT INTO users (user_id, user_dni, user_name, first_name, last_name, user_email, status, address_id)
VALUES (1, 'Q9632587W', 'usna','First', 'Last Name', 'first@email.test','CREATED', 2);

INSERT INTO users (user_id, user_dni, user_name, first_name, last_name, user_email, status, address_id)
VALUES (2, 'V7453651F', 'TEST FETCH BY EMAIL','name', 'test fetch by email', 'TEST@email.com','CREATED', 3);

INSERT INTO users (user_id, user_dni, user_name, first_name, last_name, user_email, status, address_id)
VALUES (3, 'A1234567Z', 'third user','new user', 'Last Name', 'third@email.test','CREATED', 3);