# Levanta los 5 microservicios, cada uno en su propia ventana de PowerShell.
# Uso: powershell -ExecutionPolicy Bypass -File .\levantar-todo.ps1
# Para detener un servicio, presiona Ctrl+C en su ventana (o cierrala).

$raiz = $PSScriptRoot

# Maven necesita JAVA_HOME. Si no esta definido, se busca un JDK (java en el PATH o los que instala IntelliJ en ~/.jdks).
# Las ventanas que se abren despues heredan esta variable.
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

function Puerto-Ocupado($puerto) {
    return [bool](Get-NetTCPConnection -LocalPort $puerto -State Listen -ErrorAction SilentlyContinue)
}

function Levantar($servicio, $puerto) {
    if (Puerto-Ocupado $puerto) {
        Write-Host "$servicio ya esta corriendo en el puerto $puerto, se omite." -ForegroundColor Yellow
        return
    }
    $carpeta = Join-Path $raiz $servicio
    $comando = "`$Host.UI.RawUI.WindowTitle = '$servicio ($puerto)'; Set-Location '$carpeta'; .\mvnw.cmd spring-boot:run"
    Start-Process powershell -ArgumentList '-NoExit', '-Command', $comando
    Write-Host "Levantando $servicio en el puerto $puerto..." -ForegroundColor Cyan
}

# MySQL tiene que estar arriba antes que todo
if (-not (Puerto-Ocupado 3306)) {
    Write-Host "MySQL no esta corriendo en el puerto 3306. Inicialo y vuelve a ejecutar el script." -ForegroundColor Red
    exit 1
}

# Usuarios emite los tokens JWT; productos va antes que inventario y ordenes porque le consultan los productos
Levantar 'usuarios-service' 8081
Levantar 'productos-service' 8082

Write-Host "Esperando a que productos-service este listo (puede tardar la primera vez)..."
$limite = (Get-Date).AddMinutes(3)
while (-not (Puerto-Ocupado 8082)) {
    if ((Get-Date) -gt $limite) {
        Write-Host "productos-service no arranco en 3 minutos. Revisa su ventana para ver el error." -ForegroundColor Red
        exit 1
    }
    Start-Sleep -Seconds 2
}
Write-Host "productos-service listo." -ForegroundColor Green

Levantar 'ordenes-service' 8083
Levantar 'contenido-service' 8084
Levantar 'inventario-service' 8085

Write-Host ""
Write-Host "Listo. Swagger de cada servicio:" -ForegroundColor Green
Write-Host "  Usuarios    http://localhost:8081/swagger-ui.html"
Write-Host "  Productos   http://localhost:8082/swagger-ui.html"
Write-Host "  Ordenes     http://localhost:8083/swagger-ui.html"
Write-Host "  Contenido   http://localhost:8084/swagger-ui.html"
Write-Host "  Inventario  http://localhost:8085/swagger-ui.html"
