-- ordenes-service | MySQL Workbench
-- Crea la base de datos y las tablas del microservicio de ordenes.
-- Es opcional: si no se ejecuta, la aplicacion crea las tablas sola (spring.jpa.hibernate.ddl-auto=update).

CREATE DATABASE IF NOT EXISTS ferreteria_ordenes
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Usuario que usa la aplicacion (el mismo de los otros microservicios)
CREATE USER IF NOT EXISTS 'ferreteria'@'localhost' IDENTIFIED BY 'Ferreteria123!';
GRANT ALL PRIVILEGES ON ferreteria_ordenes.* TO 'ferreteria'@'localhost';
FLUSH PRIVILEGES;

USE ferreteria_ordenes;

-- Una orden de compra
CREATE TABLE IF NOT EXISTS orden (
  id                BIGINT       NOT NULL AUTO_INCREMENT,
  cliente_nombre    VARCHAR(100) NOT NULL,
  cliente_correo    VARCHAR(100) NOT NULL,
  direccion_entrega VARCHAR(300) NOT NULL,
  fecha             DATETIME(6)  NOT NULL,
  estado            VARCHAR(20)  NOT NULL,   -- PENDIENTE, CONFIRMADA, EN_PREPARACION, ENVIADA, ENTREGADA, CANCELADA
  total             INT          NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_orden_estado (estado),
  INDEX idx_orden_correo (cliente_correo)
) ENGINE=InnoDB;

-- Los productos de cada orden (una fila por producto)
CREATE TABLE IF NOT EXISTS detalle_orden (
  id              BIGINT       NOT NULL AUTO_INCREMENT,
  orden_id        BIGINT       NOT NULL,
  producto_id     BIGINT       NOT NULL,       -- id del producto en productos-service (otra base de datos)
  producto_nombre VARCHAR(100) NOT NULL,
  precio_unitario INT          NOT NULL,
  cantidad        INT          NOT NULL,
  subtotal        INT          NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_detalle_orden (orden_id),
  CONSTRAINT fk_detalle_orden
    FOREIGN KEY (orden_id) REFERENCES orden (id)
    ON DELETE CASCADE
) ENGINE=InnoDB;
