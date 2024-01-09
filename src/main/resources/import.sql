# INIT DATABASE
CREATE DATABASE IF NOT EXISTS bd_biblioteca;
USE bd_biblioteca;

# TB_CATEGORY
INSERT INTO tb_category (id, name, status, creation_date) VALUES (1, 'Fantasía', 'A', current_timestamp());