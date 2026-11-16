-- Ferreteria Los Maestros | MySQL Workbench (ejecutar con el usuario root)
-- Crea las 5 bases de datos y el usuario 'ferreteria' que usan los microservicios.
-- Las tablas y los datos de ejemplo los crea cada servicio la primera vez que arranca.
-- Si prefieres crear tambien las tablas a mano, usa el script db/<servicio>-schema.sql de cada servicio.

CREATE DATABASE IF NOT EXISTS ferreteria_usuarios   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ferreteria_productos  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ferreteria_ordenes    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ferreteria_contenido  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ferreteria_inventario CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'ferreteria'@'localhost' IDENTIFIED BY 'Ferreteria123!';
GRANT ALL PRIVILEGES ON ferreteria_usuarios.*   TO 'ferreteria'@'localhost';
GRANT ALL PRIVILEGES ON ferreteria_productos.*  TO 'ferreteria'@'localhost';
GRANT ALL PRIVILEGES ON ferreteria_ordenes.*    TO 'ferreteria'@'localhost';
GRANT ALL PRIVILEGES ON ferreteria_contenido.*  TO 'ferreteria'@'localhost';
GRANT ALL PRIVILEGES ON ferreteria_inventario.* TO 'ferreteria'@'localhost';
FLUSH PRIVILEGES;
