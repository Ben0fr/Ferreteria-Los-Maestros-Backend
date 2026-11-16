-- inventario-service | MySQL Workbench
-- Crea la base de datos y las tablas del microservicio de inventario.
-- Es opcional: si no se ejecuta, la aplicacion crea las tablas sola (spring.jpa.hibernate.ddl-auto=update).

CREATE DATABASE IF NOT EXISTS ferreteria_inventario
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Usuario que usa la aplicacion (el mismo de los otros microservicios)
CREATE USER IF NOT EXISTS 'ferreteria'@'localhost' IDENTIFIED BY 'Ferreteria123!';
GRANT ALL PRIVILEGES ON ferreteria_inventario.* TO 'ferreteria'@'localhost';
FLUSH PRIVILEGES;

USE ferreteria_inventario;

-- Historial de entradas y salidas de stock
CREATE TABLE IF NOT EXISTS movimiento_stock (
  id               BIGINT       NOT NULL AUTO_INCREMENT,
  producto_id      BIGINT       NOT NULL,   -- id del producto en productos-service (otra base de datos)
  producto_nombre  VARCHAR(100),
  tipo             VARCHAR(20)  NOT NULL,   -- ENTRADA, SALIDA
  cantidad         INT          NOT NULL,
  stock_resultante INT          NOT NULL,
  motivo           VARCHAR(200),
  fecha            DATETIME(6)  NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_movimiento_producto (producto_id)
) ENGINE=InnoDB;
