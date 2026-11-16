# Levanta los 4 microservicios, cada uno en su propia ventana de PowerShell.
# Uso: powershell -ExecutionPolicy Bypass -File .\levantar-todo.ps1
# Para detener un servicio, presiona Ctrl+C en su ventana (o cierrala).

$raiz = $PSScriptRoot

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

# Productos va primero: inventario y ordenes le consultan los productos
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
Write-Host "  Productos   http://localhost:8082/swagger-ui.html"
Write-Host "  Ordenes     http://localhost:8083/swagger-ui.html"
Write-Host "  Contenido   http://localhost:8084/swagger-ui.html"
Write-Host "  Inventario  http://localhost:8085/swagger-ui.html"
