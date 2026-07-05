@echo off
echo [BUILD] Compiling ClientApp...

if exist bin (
    echo [*] Removing old build...
    rmdir /s /q bin
)

if not exist bin mkdir bin

echo [*] Creating subdirectories...
if not exist bin\config mkdir bin\config
if not exist bin\network mkdir bin\network
if not exist bin\features\taskmanager mkdir bin\features\taskmanager
if not exist bin\main mkdir bin\main
if not exist bin\UI mkdir bin\UI
if not exist bin\service\keylogger mkdir bin\service\keylogger

REM Copy file config
    if not exist bin\config mkdir bin\config
    copy /Y src\config\config.properties bin\config\ >nul
echo [*] Compiling ClientApp...
javac -encoding UTF-8 -cp "libs/*;src" ^
    -d bin ^
    src\config\ClientConfig.java ^
    src\network\Screen.java ^
    src\network\SocketClient.java ^
    src\service\keylogger\KeyloggerService.java ^
    src\features\taskmanager\ProcessService.java ^
    src\features\taskmanager\TaskCommandHandler.java ^
    src\features\StressTestService.java ^
    src\UI\UIClient.java ^
    src\main\ClientStart.java

if %ERRORLEVEL% == 0 (
    echo [BUILD] Compile thanh cong!
) else (
    echo [BUILD] Compile that bai!
)

pause