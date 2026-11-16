#!/bin/bash
# Administra los 5 microservicios en la EC2 (Amazon Linux 2023) como servicios de systemd,
# asi se levantan solos cada vez que el Learner Lab vuelve a encender la instancia.
#
# Uso (dentro de la carpeta ~/ferreteria de la EC2):
#   ./ferreteria.sh instalar        instala Java 17 y registra los servicios (una vez, o tras subir jars nuevos)
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
    echo "Servicios instalados. Ejecuta: ./ferreteria.sh iniciar"
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
    ip=$(curl -s -m 2 http://checkip.amazonaws.com || echo "<IP-publica>")
    echo
    echo "Swagger:"
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
    *)         sed -n '2,10p' "$0" ;;
esac
