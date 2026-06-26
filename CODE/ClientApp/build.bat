@echo off
echo [BUILD] Compiling ClientApp...

if not exist bin mkdir bin

javac -encoding UTF-8 -cp "libs/*;lib/*;src" ^
    -d bin ^
    src\config\ClientConfig.java ^
    src\network\Screen.java ^
    src\network\SocketClient.java ^
    src\service\keylogger\KeyloggerService.java ^
    src\service\taskmanager\ProcessService.java ^
    src\service\taskmanager\TaskCommandHandler.java ^
    src\UI\UIClient.java ^
    src\main\ClientStart.java

if %ERRORLEVEL% == 0 (
    echo [BUILD] Compile thanh cong!
) else (
    echo [BUILD] Compile that bai!
)
pause