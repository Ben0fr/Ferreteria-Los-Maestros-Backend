-- contenido-service | MySQL Workbench
-- Crea la base de datos y las tablas del microservicio de contenido (blog y mensajes de contacto).
-- Es opcional: si no se ejecuta, la aplicacion crea las tablas sola (spring.jpa.hibernate.ddl-auto=update).

CREATE DATABASE IF NOT EXISTS ferreteria_contenido
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Usuario que usa la aplicacion (el mismo de los otros microservicios)
CREATE USER IF NOT EXISTS 'ferreteria'@'localhost' IDENTIFIED BY 'Ferreteria123!';
GRANT ALL PRIVILEGES ON ferreteria_contenido.* TO 'ferreteria'@'localhost';
FLUSH PRIVILEGES;

USE ferreteria_contenido;

-- Articulos del blog
CREATE TABLE IF NOT EXISTS articulo (
  id                BIGINT       NOT NULL AUTO_INCREMENT,
  titulo            VARCHAR(150) NOT NULL,
  resumen           VARCHAR(300) NOT NULL,
  contenido         TEXT         NOT NULL,   -- los parrafos se separan con una linea en blanco
  imagen            VARCHAR(255) NULL,
  imagen_alt        VARCHAR(150) NULL,
  autor             VARCHAR(100) NOT NULL,
  fecha_publicacion DATE         NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_articulo_fecha (fecha_publicacion)
) ENGINE=InnoDB;

-- Mensajes enviados desde el formulario de contacto de la tienda
CREATE TABLE IF NOT EXISTS mensaje_contacto (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  nombre     VARCHAR(100) NOT NULL,
  correo     VARCHAR(100) NOT NULL,
  comentario VARCHAR(500) NOT NULL,
  fecha      DATETIME(6)  NOT NULL,
  leido      TINYINT(1)   NOT NULL DEFAULT 0,   -- 0 = pendiente, 1 = ya revisado por el administrador
  PRIMARY KEY (id),
  INDEX idx_mensaje_leido (leido),
  INDEX idx_mensaje_fecha (fecha)
) ENGINE=InnoDB;
