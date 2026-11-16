-- productos-service | MySQL Workbench
-- Crea la base de datos y las tablas del microservicio de productos.
-- Es opcional: si no se ejecuta, la aplicacion crea las tablas sola (spring.jpa.hibernate.ddl-auto=update).

CREATE DATABASE IF NOT EXISTS ferreteria_productos
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Usuario que usa la aplicacion (el mismo de los otros microservicios)
CREATE USER IF NOT EXISTS 'ferreteria'@'localhost' IDENTIFIED BY 'Ferreteria123!';
GRANT ALL PRIVILEGES ON ferreteria_productos.* TO 'ferreteria'@'localhost';
FLUSH PRIVILEGES;

USE ferreteria_productos;

-- Categorias de productos (herramientas, pinturas, etc.)
CREATE TABLE IF NOT EXISTS categoria (
  id     BIGINT       NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(255) NOT NULL,
  slug   VARCHAR(255) NOT NULL,
  icono  VARCHAR(255),
  PRIMARY KEY (id),
  UNIQUE KEY uk_categoria_nombre (nombre),
  UNIQUE KEY uk_categoria_slug (slug)
) ENGINE=InnoDB;

-- Productos del catalogo
CREATE TABLE IF NOT EXISTS producto (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  codigo        VARCHAR(30)  NOT NULL,
  nombre        VARCHAR(100) NOT NULL,
  descripcion   VARCHAR(500),
  precio        INT          NOT NULL,
  stock         INT          NOT NULL,
  stock_critico INT,
  icono         VARCHAR(255),
  categoria_id  BIGINT       NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_producto_codigo (codigo),
  INDEX idx_producto_categoria (categoria_id),
  CONSTRAINT fk_producto_categoria
    FOREIGN KEY (categoria_id) REFERENCES categoria (id)
) ENGINE=InnoDB;
