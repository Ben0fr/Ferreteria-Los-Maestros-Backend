# Compila los 5 microservicios y el frontend, y sube los jars, el frontend, los scripts .sql,
# ferreteria.sh y la configuracion de nginx a la EC2.
# Uso: powershell -ExecutionPolicy Bypass -File .\ec2\subir-a-ec2.ps1 -Ip <IP-publica-EC2> -Llave <ruta\labsuser.pem> [-Frontend <carpeta-del-frontend>]

param(
    [Parameter(Mandatory = $true)] [string] $Ip,
    [Parameter(Mandatory = $true)] [string] $Llave,
    [string] $Frontend = "$env:USERPROFILE\Documents\proyecto fullstack completo\proyecto fullstack completo\Ferreteria-Los-Maestros-Frontend"
)

$raiz = Split-Path $PSScriptRoot -Parent
$servicios = 'usuarios-service', 'productos-service','ordenes-service', 'contenido-service', 'inventario-service'
$destino = "ec2-user@${Ip}"

# Maven necesita JAVA_HOME. Si no esta definido, se busca un JDK (java en el PATH o los que instala IntelliJ en ~/.jdks)
if (-not $env:JAVA_HOME -or -not (Test-Path (Join-Path $env:JAVA_HOME 'bin\javac.exe'))) {
    $candidatos = @()
    $java = Get-Command java -ErrorAction SilentlyContinue
    if ($java) { $candidatos += Split-Path (Split-Path $java.Source -Parent) -Parent }
    $candidatos += Get-ChildItem "$env:USERPROFILE\.jdks" -Directory -ErrorAction SilentlyContinue | Sort-Object Name | ForEach-Object { $_.FullName }
    $jdk = $candidatos | Where-Object { Test-Path (Join-Path $_ 'bin\javac.exe') } | Select-Object -First 1
    if (-not $jdk) {
        Write-Host "No se encontro un JDK. Instala Java 17 o superior, o define JAVA_HOME." -ForegroundColor Red
        exit 1
    }
    $env:JAVA_HOME = $jdk
    Write-Host "JAVA_HOME no estaba definido, se usa $jdk" -ForegroundColor Yellow
}

foreach ($s in $servicios) {
    Write-Host "Compilando $s..." -ForegroundColor Cyan
    Push-Location (Join-Path $raiz $s)
    .\mvnw.cmd -q clean package -DskipTests
    $ok = $?
    Pop-Location
    if (-not $ok) {
        Write-Host "Fallo la compilacion de $s." -ForegroundColor Red
        exit 1
    }
}

# Frontend: en la EC2 nginx atiende la pagina y la API en la misma direccion, asi que las URLs de la API son relativas
$dist = $null
if (Test-Path (Join-Path $Frontend 'package.json')) {
    Write-Host "Compilando el frontend..." -ForegroundColor Cyan
    Push-Location $Frontend
    if (-not (Test-Path 'node_modules')) { npm install }
    foreach ($api in 'USUARIOS', 'PRODUCTOS', 'ORDENES', 'CONTENIDO', 'INVENTARIO') {
        Set-Item "env:VITE_API_${api}_URL" '/api/v1'
    }
    npm run build
    $ok = $?
    Pop-Location
    if (-not $ok) {
        Write-Host "Fallo la compilacion del frontend." -ForegroundColor Red
        exit 1
    }
    $dist = Join-Path $Frontend 'dist'
} else {
    Write-Host "No se encontro el frontend en $Frontend, se sube solo el backend." -ForegroundColor Yellow
}

$archivos = @()
foreach ($s in $servicios) {
    $archivos += Get-ChildItem (Join-Path $raiz "$s\target\$s-*.jar") | Where-Object { $_.Name -notlike '*plain*' } | ForEach-Object { $_.FullName }
    $archivos += Get-ChildItem (Join-Path $raiz "$s\db\*.sql") -ErrorAction SilentlyContinue | ForEach-Object { $_.FullName }
}
$archivos += Join-Path $PSScriptRoot 'ferreteria.sh'
$archivos += Join-Path $PSScriptRoot 'ferreteria.env.ejemplo'
$archivos += Join-Path $PSScriptRoot 'nginx-ferreteria.conf'

Write-Host "Subiendo archivos a $Ip..." -ForegroundColor Cyan
ssh -i $Llave -o StrictHostKeyChecking=accept-new $destino 'mkdir -p ~/ferreteria; rm -rf ~/ferreteria/frontend'
scp -i $Llave $archivos "${destino}:~/ferreteria/"
if (-not $?) {
    Write-Host "Fallo la subida. Revisa la IP, la llave y que el puerto 22 este abierto." -ForegroundColor Red
    exit 1
}
if ($dist) {
    scp -i $Llave -r $dist "${destino}:~/ferreteria/frontend"
    if (-not $?) {
        Write-Host "Fallo la subida del frontend." -ForegroundColor Red
        exit 1
    }
}
ssh -i $Llave $destino 'chmod +x ~/ferreteria/ferreteria.sh'

Write-Host ""
Write-Host "Listo. Conectate con:  ssh -i $Llave $destino" -ForegroundColor Green
Write-Host "y ejecuta:  cd ferreteria; ./ferreteria.sh instalar; ./ferreteria.sh reiniciar"
