####### TB_ROLES
INSERT INTO tb_roles (id, authority) VALUES ('1', 'ROLE_SYSADMIN');
INSERT INTO tb_roles (id, authority) VALUES ('2', 'ROLE_ADMIN');
INSERT INTO tb_roles (id, authority) VALUES ('3', 'ROLE_EMPLEADO');
INSERT INTO tb_roles (id, authority) VALUES ('4', 'ROLE_USUARIO');
INSERT INTO tb_roles (id, authority) VALUES ('5', 'ROLE_PRUEBA');
####### TB_EMPRESAS
INSERT INTO tb_empresas (id, razon_social, ruc, direccion, estado) VALUES ('1', 'Biblioteca2020', 11111111111, 'Av. Lima 456', true);
####### TB_LOCALES
INSERT INTO tb_locales (id, direccion, estado, observaciones, fecha_registro, empresa_id) VALUES ('1', '--', 1, 'LOCAL VIRTUAL VÁLIDO SOLAMENTE PARA REGISTRO DE USUARIOS (NO TRABAJADORES)', '2020-06-04', 1);
INSERT INTO tb_locales (id, direccion, estado, observaciones, fecha_registro, empresa_id) VALUES ('2', 'Av. Puno 888', 1, 'Sede central de Biblioteca2020', '2020-06-04', 1);
INSERT INTO tb_locales (id, direccion, fecha_registro, estado, observaciones, empresa_id) VALUES ('3', 'Av. Del Sol 111', '2020-06-12', 1, 'Local anexo de San Juan de Lurigancho', 1);
####### TB_USUARIOS
INSERT INTO tb_usuarios (id, apellido_materno, apellido_paterno, celular, direccion, email, estado, fecha_registro, foto_usuario, nombres, nro_documento, password, usuario, local_id, rol_id) VALUES ('1', '--', '--', 000000000, '--', '--', 1, '2019-06-05', 'no-image.jpg', '--', 00000000, '', 'prueba', 2, 5);
INSERT INTO tb_usuarios (id, apellido_materno, apellido_paterno, celular, direccion, email, estado, fecha_registro, foto_usuario, nombres, nro_documento, password, usuario, local_id, rol_id) VALUES ('2', 'Chinga', 'Medina', 983489303, 'Jr. Los Oligistos 2339', 'edi@live.it', 1, '2019-06-05', 'no-image.jpg', 'Edinson', 47111025, '$2a$10$86PKoDGKFkbtmEXQuUaQbuJ0vzRGY/tLyUiOsu9VExoajCFsqV5yC', 'edmech', 2, 1);
INSERT INTO tb_usuarios (id, apellido_materno, apellido_paterno, celular, direccion, email, estado, fecha_registro, foto_usuario, nombres, nro_documento, password, usuario, local_id, rol_id) VALUES ('3', 'La Del Barrio', 'Fino', 999333444, 'Av. Cuzco 111', 'maria2020@gmail.com', 1, '2019-06-05', 'no-image.jpg', 'Maria', 99999999, '$2a$10$86PKoDGKFkbtmEXQuUaQbuJ0vzRGY/tLyUiOsu9VExoajCFsqV5yC', 'maria', 1, 4);
INSERT INTO tb_usuarios (id, apellido_materno, apellido_paterno, celular, direccion, email, estado, fecha_registro, foto_usuario, nombres, nro_documento, password, usuario, local_id, rol_id) VALUES ('4', 'Paredes', 'Rojas', 987342333, 'Av . Arequipa 555', 'pepe2020@gmail.com', 1, '2019-06-05', 'no-image.jpg', 'Pepito', 22222222, '$2a$10$86PKoDGKFkbtmEXQuUaQbuJ0vzRGY/tLyUiOsu9VExoajCFsqV5yC', 'pepe', 3, 3);
####### TB_CATEGORIAS
INSERT INTO tb_categorias (id, nombre, fecha_registro, estado) VALUES ('1', 'Fantasía', '2020-06-12', 1);
####### TB_LIBROS
INSERT INTO tb_libros (id, autor, descripcion, fecha_publicacion, fecha_registro, foto_libro, estado, stock, titulo, categoria_id, local_id) VALUES ('1', 'Brandon Sanderson', 'Primer libro de una decalogía del género fantasía en un universo totalmente imaginario', '2012-04-11', '2020-06-12', 'el-camino-de-los-reyes.png', 1, 100, 'El camino de los reyes', 1, 3); 