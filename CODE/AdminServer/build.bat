@echo off
echo [BUILD] Compiling AdminServer...

if not exist bin mkdir bin

javac -encoding UTF-8 -sourcepath src -d bin src\AdminServerApp.java

if %ERRORLEVEL% == 0 (
    echo [BUILD] Compile thanh cong!
) else (
    echo [BUILD] Compile that bai!
)
pause
