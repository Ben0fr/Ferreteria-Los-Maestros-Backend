-- usuarios-service | MySQL Workbench
-- Crea la base de datos y las tablas del microservicio de usuarios.
-- Es opcional: si no se ejecuta, la aplicacion crea las tablas sola (spring.jpa.hibernate.ddl-auto=update).

CREATE DATABASE IF NOT EXISTS ferreteria_usuarios
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Usuario que usa la aplicacion (el mismo de los otros microservicios)
CREATE USER IF NOT EXISTS 'ferreteria'@'localhost' IDENTIFIED BY 'Ferreteria123!';
GRANT ALL PRIVILEGES ON ferreteria_usuarios.* TO 'ferreteria'@'localhost';
FLUSH PRIVILEGES;

USE ferreteria_usuarios;

-- Usuarios del sistema (clientes, vendedores y administradores)
CREATE TABLE IF NOT EXISTS usuario (
  id               BIGINT       NOT NULL AUTO_INCREMENT,
  run              VARCHAR(10),
  nombre           VARCHAR(100) NOT NULL,
  apellidos        VARCHAR(100),
  correo           VARCHAR(120) NOT NULL,
  clave            VARCHAR(100) NOT NULL,   -- hash BCrypt, nunca la clave en texto plano
  rol              VARCHAR(20)  NOT NULL,   -- ADMINISTRADOR, VENDEDOR, CLIENTE
  fecha_nacimiento DATE,
  region           VARCHAR(100),
  comuna           VARCHAR(100),
  direccion        VARCHAR(200),
  activo           BIT(1)       NOT NULL,
  fecha_creacion   DATETIME(6)  NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_usuario_run (run),
  UNIQUE KEY uk_usuario_correo (correo)
) ENGINE=InnoDB;
