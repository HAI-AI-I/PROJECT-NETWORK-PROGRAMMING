@echo off
echo [BUILD] Compiling AdminServer...

if exist bin (
    echo [*] Removing old build...
    rmdir /s /q bin
)

if not exist bin mkdir bin

echo [*] Creating subdirectories...
if not exist bin\config mkdir bin\config
if not exist bin\features mkdir bin\features
if not exist bin\network mkdir bin\network
if not exist bin\ui mkdir bin\ui
if not exist bin\ui\taskmanager mkdir bin\ui\taskmanager

echo [*] Copying config files...
copy /Y src\config\config.properties bin\config\ >nul

echo [*] Compiling all Java files...
javac -encoding UTF-8 -sourcepath src -d bin ^
  src\AdminServerApp.java ^
  src\AdminServerController.java ^
  src\config\ConfigManager.java ^
  src\features\WebcamServerDemo.java ^
  src\features\TaskManagerServerDemo.java ^
  src\network\KeyloggerClient.java ^
  src\ui\UIKeylogger.java ^
  src\ui\taskmanager\TaskManagerPanel.java

if %ERRORLEVEL% == 0 (
    echo [BUILD] Compile thanh cong!
) else (
    echo [BUILD] Compile that bai!
)

pause