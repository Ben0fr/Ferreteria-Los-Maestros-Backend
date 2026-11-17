#!/bin/bash
# Administra los 5 microservicios en la EC2 (Amazon Linux 2023) como servicios de systemd,
# asi se levantan solos cada vez que el Learner Lab vuelve a encender la instancia.
#
# Uso (dentro de la carpeta ~/ferreteria de la EC2):
#   ./ferreteria.sh instalar        instala Java 17, registra los servicios y monta el frontend en nginx
#                                   (una vez, o cada vez que subas jars o frontend nuevos)
#   ./ferreteria.sh bd <usuario>    ejecuta los scripts .sql en RDS con el usuario admin de RDS
#   ./ferreteria.sh iniciar | detener | reiniciar | estado
#   ./ferreteria.sh logs <servicio> muestra el log en vivo (usuarios, productos, ordenes, contenido, inventario)

set -e

DIR="$(cd "$(dirname "$0")" && pwd)"
ENV_FILE="$DIR/ferreteria.env"
SERVICIOS="usuarios productos ordenes contenido inventario"
PUERTOS="usuarios:8081 productos:8082 ordenes:8083 contenido:8084 inventario:8085"

cargar_env() {
    if [ ! -f "$ENV_FILE" ]; then
        echo "Falta $ENV_FILE. Copia ferreteria.env.ejemplo a ferreteria.env y completa los datos de RDS."
        exit 1
    fi
    # Quita los saltos de linea de Windows (\r) si el archivo se edito o se subio desde Windows
    sed -i 's/\r$//' "$ENV_FILE"
    set -a; . "$ENV_FILE"; set +a
}

instalar() {
    cargar_env
    sudo dnf install -y java-17-amazon-corretto-headless mariadb105

    for s in $SERVICIOS; do
        jar=$(ls "$DIR"/$s-service-*.jar 2>/dev/null | head -1)
        if [ -z "$jar" ]; then
            echo "Falta el jar de $s-service en $DIR"
            exit 1
        fi

        # Ordenes, contenido e inventario arrancan despues de productos
        despues="network-online.target"
        [ "$s" != "productos" ] && [ "$s" != "usuarios" ] && despues="$despues ferreteria-productos.service"

        sudo tee /etc/systemd/system/ferreteria-$s.service > /dev/null <<EOF
[Unit]
Description=Ferreteria $s-service
After=$despues
Wants=network-online.target

[Service]
User=$USER
WorkingDirectory=$DIR
EnvironmentFile=$ENV_FILE
ExecStart=/usr/bin/java -Xmx256m -jar $jar
Restart=on-failure
RestartSec=15

[Install]
WantedBy=multi-user.target
EOF
    done

    sudo systemctl daemon-reload
    for s in $SERVICIOS; do sudo systemctl enable ferreteria-$s; done
    instalar_frontend
    echo "Servicios instalados. Ejecuta: ./ferreteria.sh iniciar"
}

# nginx sirve el frontend (carpeta frontend/, la sube subir-a-ec2.ps1) en el puerto 80
# y reenvia /api/v1/... a los microservicios
instalar_frontend() {
    if [ ! -f "$DIR/frontend/index.html" ]; then
        echo "No se encontro $DIR/frontend: se instala solo el backend."
        return
    fi
    sudo dnf install -y nginx
    sudo rm -rf /usr/share/nginx/ferreteria
    sudo cp -r "$DIR/frontend" /usr/share/nginx/ferreteria
    # scp desde Windows puede dejar los archivos legibles solo por ec2-user; nginx corre con su propio usuario
    sudo chmod -R a+rX /usr/share/nginx/ferreteria
    sed 's/\r$//' "$DIR/nginx-ferreteria.conf" | sudo tee /etc/nginx/conf.d/ferreteria.conf > /dev/null
    sudo nginx -t
    sudo systemctl enable nginx
    sudo systemctl restart nginx
    echo "Frontend instalado en nginx (puerto 80)."
}

bd() {
    cargar_env
    usuario="$1"
    if [ -z "$usuario" ]; then
        echo "Uso: ./ferreteria.sh bd <usuario-admin-de-RDS>"
        exit 1
    fi
    read -r -s -p "Contrasena de $usuario en RDS: " MYSQL_PWD; echo
    export MYSQL_PWD

    # En RDS la conexion llega desde la EC2, no desde localhost: el usuario se crea como 'ferreteria'@'%'
    # y con la contrasena de ferreteria.env
    pw_sql=${DB_PASSWORD//\'/\'\'}
    for sql in "$DIR"/*-schema.sql; do
        echo "Ejecutando $(basename "$sql")..."
        {
            sed "s/'ferreteria'@'localhost'/'ferreteria'@'%'/g" "$sql"
            echo "ALTER USER 'ferreteria'@'%' IDENTIFIED BY '$pw_sql';"
        } | mysql -h "$DB_HOST" -u "$usuario"
    done
    echo "Bases de datos listas."
}

estado() {
    for s in $SERVICIOS; do
        printf "%-12s %s\n" "$s" "$(systemctl is-active ferreteria-$s)"
    done
    printf "%-12s %s\n" "nginx" "$(systemctl is-active nginx 2>/dev/null)"
    ip=$(curl -s -m 2 http://checkip.amazonaws.com || echo "<IP-publica>")
    echo
    echo "Pagina web:  http://$ip"
    echo
    echo "Swagger (requiere abrir los puertos 8081-8085 en el Security Group):"
    for p in $PUERTOS; do
        echo "  ${p%%:*}  http://$ip:${p##*:}/swagger-ui.html"
    done
}

case "$1" in
    instalar)  instalar ;;
    bd)        bd "$2" ;;
    iniciar)   for s in $SERVICIOS; do sudo systemctl start ferreteria-$s; done; estado ;;
    detener)   for s in $SERVICIOS; do sudo systemctl stop ferreteria-$s; done; estado ;;
    reiniciar) for s in $SERVICIOS; do sudo systemctl restart ferreteria-$s; done; estado ;;
    estado)    estado ;;
    logs)      sudo journalctl -u ferreteria-${2:-productos} -f ;;
    *)         sed -n '2,11p' "$0" ;;
esac
