@echo off
title UDM_04 Auto Run Tool
echo ==================================================
echo       UDM_04 AUTO BUILD AND RUN
echo ==================================================
echo.

echo [+] 1. Dang bien dich Admin Server...
javac -encoding UTF-8 -sourcepath CODE/AdminServer/src -d CODE/AdminServer/bin CODE/AdminServer/src/AdminServerApp.java
if %errorlevel% neq 0 (
    echo [Loi] Khong the bien dich Admin Server!
    pause
    exit /b
)

echo [+] 2. Dang bien dich Client App...
javac -encoding UTF-8 -cp "CODE/ClientApp/libs/*;CODE/ClientApp/src" -d CODE/ClientApp/bin CODE/ClientApp/src/main/ClientStart.java
if %errorlevel% neq 0 (
    echo [Loi] Khong the bien dich Client App!
    pause
    exit /b
)

echo [+] 3. Khoi chay ung dung...
start "Admin Server" java -cp CODE/AdminServer/bin AdminServerApp
start "Client App" java -cp "CODE/ClientApp/libs/*;CODE/ClientApp/bin" main.ClientStart

echo [XONG] Da mo ca hai ung dung! Cua so nay se tu dong dong.
timeout /t 2 > nul
exit
