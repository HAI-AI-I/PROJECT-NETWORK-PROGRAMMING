@echo off
echo [RUN] Starting ClientApp...

java -cp "bin;lib\jnativehook-2.2.2.jar;lib\bridj-0.7.0-android.jar" ^
    main.ClientStart

pause