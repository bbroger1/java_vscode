@echo off
setlocal

REM ========================================
REM CONFIGURACAO DO AMBIENTE
REM ========================================

REM Forcar uso do Java 8 (ajuste conforme sua instalacao)
set JAVA_HOME=C:\jdk8
set PATH=%JAVA_HOME%\bin;%PATH%

title Deploy - Avaliacao Riscos

REM ========================================
REM CONFIGURACAO
REM ========================================

REM ========================================
REM CONFIGURACAO
REM ========================================

set PROJ=C:\Users\bbrog\OneDrive\Desktop\java_vscode\avaliacao-riscos
set TOMCAT=C:\Apache\tomcat-9
set WAR=%PROJ%\target\avaliacao-riscos.war

REM Verificar se JAVA_HOME esta configurado corretamente
if not defined JAVA_HOME (
    echo [ERRO] JAVA_HOME nao esta definido! Configure para Java 8.
    pause
    exit /b 1
)

echo JAVA_HOME: %JAVA_HOME%
echo Java version:
"%JAVA_HOME%\bin\java" -version
echo.

setlocal enabledelayedexpansion

echo ========================================
echo  Rebuild e Deploy - Avaliacao Riscos
echo ========================================
echo.

REM ========================================
REM BUILD
REM ========================================

echo [1/4] Rebuilding WAR...

cd /d "%PROJ%"

call mvn clean package -DskipTests

if errorlevel 1 (
    echo.
    echo [ERRO] Falha no build Maven.
    pause
    exit /b 1
)

if not exist "%WAR%" (
    echo.
    echo [ERRO] WAR nao encontrado:
    echo %WAR%
    pause
    exit /b 1
)

echo [OK] WAR gerado com sucesso.
echo.

REM ========================================
REM PARAR TOMCAT
REM ========================================

echo [2/4] Parando Tomcat...

call "%TOMCAT%\bin\shutdown.bat"

timeout /t 5 /nobreak >nul

echo [OK] Tomcat parado.
echo.

REM ========================================
REM LIMPAR DEPLOY ANTIGO
REM ========================================

echo [3/4] Removendo deploy anterior...

if exist "%TOMCAT%\webapps\avaliacao-riscos.war" (
    del /f /q "%TOMCAT%\webapps\avaliacao-riscos.war"
)

if exist "%TOMCAT%\webapps\avaliacao-riscos" (
    rmdir /s /q "%TOMCAT%\webapps\avaliacao-riscos"
)

copy /y "%WAR%" "%TOMCAT%\webapps\"

if errorlevel 1 (
    echo.
    echo [ERRO] Falha ao copiar WAR.
    pause
    exit /b 1
)

echo [OK] WAR copiado.
echo.

REM ========================================
REM INICIAR TOMCAT
REM ========================================

echo [4/4] Iniciando Tomcat...

call "%TOMCAT%\bin\startup.bat"

timeout /t 5 /nobreak >nul

echo.
echo ========================================
echo  Deploy concluido com sucesso
echo ========================================
echo.
echo Aplicacao:
echo http://localhost:8080/avaliacao-riscos
echo.

pause