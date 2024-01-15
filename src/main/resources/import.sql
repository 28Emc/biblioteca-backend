# DATABASE INITIALIZATION
CREATE DATABASE IF NOT EXISTS emc_library_bd;
USE emc_library_bd;

# FAKE DATA REGISTRATION
## TB_LIBRARY
INSERT INTO tb_library (address, status, creation_date, address_reference, image_reference) VALUES ('Av. Lima 123', 'A', NOW(), NULL, NULL);

## TB_CATEGORY
INSERT INTO tb_category (id, name, status, creation_date) VALUES (1, 'Fantasy', 'A', current_timestamp());

## TB_BOOK
INSERT INTO tb_book (isbn, title, author, publishing_house, stock, image, status, publish_date, creation_date, synopsis, category_id) VALUES ('123-4-567', 'El camino de los reyes', 'Brandon Sanderson', 'No se sabe', 100, 'https://www.example.com/el-camino-de-los-reyes.png', 'A', '1991-02-10', NOW(), 'Sin sinopsis', 1);

## TB_BOOK_COPY
INSERT INTO tb_book_copy (isbn, status, creation_date, book_id, id) VALUES ('123-4-567', 'D', NOW(), 1, 1);

## TB_MEMBER
INSERT INTO tb_member (uuid, name, last_name, doc_type, doc_nro, phone_number, email, status, creation_date, address, address_reference, alias, avatar_img) VALUES ('d6404afd-5961-464f-b7c0-72ccecb803e9', 'John', 'Doe', '1', '12345678', '999333444', 'jdoe@email.com', 'A', NOW(), 'Av. Arequipa 456', '', 'jdoe', '');

## TB_EMPLOYEE
INSERT INTO tb_employee (uuid, name, last_name, position, doc_nro, phone_number, email, status, creation_date, library_id) VALUES ('EM416812', 'Manolito', 'Paredes Rojas', 'EMPLOYEE', '33322211', '912432520', 'mrojas@email.com', 'A', NOW(), 1);

## TB_BOOK_LOAN
INSERT INTO tb_book_loan (code, loan_date, return_date, status, creation_date, member_id, employee_id, book_id) VALUES ('BL832581', current_date, date_add(curdate(), interval 30 day), 'A', NOW(), 1, 1, 1);

# TB_OPERATION_LOG
INSERT INTO tb_operation_log (operation_type, entity_name, entity_id, user_id, creation_date) VALUES ('CREATE_BOOK_LOAN', 'BOOK_LOAN', 1, 1, NOW());
