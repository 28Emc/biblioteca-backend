# INIT DATABASE
CREATE DATABASE IF NOT EXISTS bd_biblioteca;
USE bd_biblioteca;

# TB_LIBRARY

# TB_CATEGORY
-- INSERT INTO tb_category (id, name, status, creation_date) VALUES (1, 'Fantasy', 'A', current_timestamp());

# TB_BOOK
-- INSERT INTO tb_book (isbn, title, author, publishing_house, stock, image, status, publish_date, creation_date,
-- synopsis, category_id) VALUES ('123-4-567', 'El camino de los reyes', 'Brandon Sanderson', 'No se sabe', 100,
-- 'https://www.example.com/el-camino-de-los-reyes.png', 'A', '1991-02-10', NOW(), 'Sin sinopsis', 1);

# TB_BOOK_COPY
-- INSERT INTO tb_book_copy (isbn, status, creation_date, book_id) VALUES ('123-4-567', 'D', NOW(), 1);

# TB_MEMBER

# TB_EMPLOYEE

# TB_BOOK_LOAN