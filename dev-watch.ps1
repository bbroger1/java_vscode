param(
    [switch]$watch
)

$tomcat = "C:\Apache\tomcat-9"
$proj   = "C:\Users\bbrog\OneDrive\Desktop\java_vscode\avaliacao-riscos"
$webapp = "$tomcat\webapps\avaliacao-riscos"

Write-Host "=== Hot Reload Watcher ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "XHTML:  Edite e salve -> refresh no navegador (ja configurado)" -ForegroundColor Green
Write-Host "Java:   Script compila e copia automaticamente" -ForegroundColor Green
Write-Host "CSS/JS: Salve em src/main/webapp/resources/ -> refresh" -ForegroundColor Green
Write-Host ""

if (-not (Test-Path $webapp)) {
    Write-Host "AVISO: Aplicacao nao esta explodida no Tomcat." -ForegroundColor Yellow
    Write-Host "Copie o WAR primeiro ou execute deploy-avaliacao.bat" -ForegroundColor Yellow
    exit
}

function Build-Java {
    Write-Host "[$(Get-Date -Format HH:mm:ss)] Compilando Java..." -ForegroundColor Cyan
    $result = & {
        cd $proj
        mvn compile -q 2>&1
    }
    if ($LASTEXITCODE -eq 0) {
        Copy-Item "$proj\target\classes\com" "$webapp\WEB-INF\classes\" -Recurse -Force
        Write-Host "[$(Get-Date -Format HH:mm:ss)] Java compilado e copiado!" -ForegroundColor Green
    } else {
        Write-Host "[$(Get-Date -Format HH:mm:ss)] ERRO na compilacao!" -ForegroundColor Red
        Write-Host $result
    }
}

if ($watch) {
    Write-Host "Modo WATCH ativado. Monitorando src/main/java..." -ForegroundColor Cyan
    Write-Host "Pressione Ctrl+C para parar." -ForegroundColor Cyan
    Write-Host ""

    $watcher = New-Object System.IO.FileSystemWatcher
    $watcher.Path = "$proj\src\main\java"
    $watcher.IncludeSubdirectories = $true
    $watcher.EnableRaisingEvents = $true
    $watcher.NotifyFilter = [System.IO.NotifyFilters]'LastWrite,FileName,DirectoryName'

    $timer = $null
    Register-ObjectEvent $watcher "Changed" -Action {
        if ($timer) { $timer.Dispose() }
        $timer = New-Object System.Timers.Timer(2000)
        $timer.AutoReset = $false
        Register-ObjectEvent $timer "Elapsed" -Action {
            cd $event.MessageData
            & { Build-Java }
        } -MessageData $proj | Out-Null
        $timer.Start()
    } | Out-Null

    while ($true) { Start-Sleep 10 }
} else {
    Build-Java
}
