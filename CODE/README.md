@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

title Admin Server - Network Monitoring

cd /d "%~dp0"

echo ============================================
echo   ADMIN SERVER - COMPILATION & RUN
echo ============================================
echo.

REM ← Xoá toàn bộ folder out cũ
if exist "out" (
    echo [*] Removing old output...
    rmdir /s /q out
)

echo [*] Creating output directories...
if not exist "out\config" mkdir out\config
if not exist "out\features" mkdir out\features

echo [*] Compiling AdminServer...
javac -d out ^
  src\AdminServerApp.java ^
  src\AdminServerController.java ^
  src\config\ConfigManager.java ^
  src\features\WebcamServerDemo.java ^
  src\features\TaskManagerServerDemo.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed
    pause
    exit /b 1
)

echo [OK] Compilation successful!
echo.
echo [*] Starting Admin Server...
echo.

java -cp "out" AdminServerApp

pause



@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

title Client App - Network Client

cd /d "%~dp0"

echo ============================================
echo   CLIENT APP - COMPILATION
echo ============================================
echo.

echo [*] Creating output directories...
if not exist "out\config" mkdir out\config
if not exist "out\network" mkdir out\network
if not exist "out\features\taskmanager" mkdir out\features\taskmanager
if not exist "out\main" mkdir out\main
if not exist "out\UI" mkdir out\UI

echo [*] Copying config files...
copy src\config\client.properties out\config\ >nul

echo [*] Compiling ClientApp...
javac -cp "libs/*" -d out ^
  src\config\ClientConfig.java ^
  src\network\SocketClient.java ^
  src\network\Screen.java ^
  src\features\WebcamClientDemo.java ^
  src\features\taskmanager\ProcessService.java ^
  src\features\taskmanager\TaskCommandHandler.java ^
  src\main\ClientStart.java ^
  src\UI\UIClient.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed
    pause
    exit /b 1
)

echo [OK] Compilation successful!
echo.
echo [*] Starting Client App...
echo.

java -cp "out;libs/*" main.ClientStart

pause