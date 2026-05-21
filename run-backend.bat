@echo off
title Auto License Backend Runner
echo ====================================================
echo   AUTO LICENSE - SPRING BOOT BACKEND RUNNER
echo ====================================================
echo.
echo Database: auto_litsenziya
echo Password: isomiddin
set JAVA_HOME=C:\Program Files\Java\jdk-26.0.1
set PATH=%JAVA_HOME%\bin;%PATH%
echo Java: C:\Program Files\Java\jdk-26.0.1 (Configured)
echo.

:: 1. Check if global mvn command is available
where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 goto run_global

:: 2. Check if portable maven exists
echo Global Maven was not found on your system.
echo Checking for portable Maven...
if exist ".maven-portable\apache-maven-3.9.6\bin\mvn.cmd" goto run_portable

:: 3. Download portable maven
echo.
echo Portable Maven not found. Downloading Apache Maven 3.9.6 (approx 9MB)...
echo This is a one-time download and will configure itself automatically.
echo.

powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'maven-temp.zip'"

if %ERRORLEVEL% neq 0 goto download_failed

echo Extracting Maven binary archive...
powershell -Command "Expand-Archive -Path 'maven-temp.zip' -DestinationPath '.maven-portable'"

echo Cleaning up temporary archive...
del /q maven-temp.zip

echo Portable Maven installed successfully!
echo.
goto run_portable

:run_global
echo Global Maven detected! Running application...
echo.
call mvn spring-boot:run
goto end

:run_portable
echo Starting Auto License Backend using portable Maven...
set PATH=%CD%\.maven-portable\apache-maven-3.9.6\bin;%PATH%
echo.
call mvn spring-boot:run
goto end

:download_failed
echo.
echo [XATO] Maven yuklab olishda xatolik yuz berdi. Internet aloqasini tekshiring!
goto end

:end
pause
