@echo off
echo [BUILD] Compiling ClientApp...

if not exist bin mkdir bin

javac -encoding UTF-8 -cp "libs/*;src" ^
    -d bin ^
    src\config\ClientConfig.java ^
    src\network\Screen.java ^
    src\network\SocketClient.java ^
    src\service\keylogger\KeyloggerService.java ^
    src\features\taskmanager\ProcessService.java ^
    src\features\taskmanager\TaskCommandHandler.java ^
    src\UI\UIClient.java ^
    src\main\ClientStart.java

if %ERRORLEVEL% == 0 (

    REM Copy file config
    if not exist bin\config mkdir bin\config
    copy /Y src\config\config.properties bin\config\ >nul

    echo [BUILD] Compile thanh cong!

) else (

    echo [BUILD] Compile that bai!

)

pause