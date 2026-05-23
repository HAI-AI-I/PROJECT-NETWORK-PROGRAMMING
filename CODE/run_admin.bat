@echo off
chcp 65001 > nul
title Run Admin Server

echo ================================
echo   RUN ADMIN SERVER APPLICATION
echo ================================
echo.

cd /d "%~dp0AdminServer"

echo Current folder:
cd
echo.

echo Compiling AdminServerApp.java...
javac -encoding UTF-8 -d out src\AdminServerApp.java

if errorlevel 1 (
    echo.
    echo [ERROR] Compile failed!
    echo Please check your Java code.
    pause
    exit /b
)

echo.
echo Compile successfully!
echo Running AdminServerApp...
echo.

java -cp out AdminServerApp

echo.
pause