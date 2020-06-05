####### TB_ROLES
INSERT INTO tb_roles (id, authority) VALUES ('1', 'ROLE_SYSADMIN');
INSERT INTO tb_roles (id, authority) VALUES ('2', 'ROLE_ADMIN');
INSERT INTO tb_roles (id, authority) VALUES ('3', 'ROLE_EMPLEADO');
INSERT INTO tb_roles (id, authority) VALUES ('4', 'ROLE_USER');
####### TB_EMPRESAS
INSERT INTO tb_empresas (id, razon_social, ruc, direccion, estado) VALUES ('1', 'Biblioteca2020', 11111111111, 'Av. Lima 456', true);
####### TB_LOCALES
INSERT INTO tb_locales (id, direccion, estado, observaciones, fecha_registro, empresa_id) VALUES ('1', '--', 1, 'LOCAL VIRTUAL VÁLIDO SOLAMENTE PARA REGISTRO DE USUARIOS (NO TRABAJADORES)', '2020-06-04', 1);
INSERT INTO tb_locales (id, direccion, estado, observaciones, fecha_registro, empresa_id) VALUES ('2', 'Av. Puno 888', 1, 'Sede central de Biblioteca2020', '2020-06-04', 1);
####### TB_USUARIOS
INSERT INTO tb_usuarios (id, apellido_materno, apellido_paterno, celular, direccion, email, estado, fecha_registro, foto_usuario, nombres, nro_documento, password, usuario, local_id, rol_id) VALUES ('1', 'Apellido Materno', 'Apellido Paterno', 656565656, 'Av. Lima 789', 'edi@live.it', 1, '2019-08-19', 'no-image.jpg', 'Nombre Usuario', 44444444, '$2a$10$86PKoDGKFkbtmEXQuUaQbuJ0vzRGY/tLyUiOsu9VExoajCFsqV5yC', 'edmech', 2, 1);