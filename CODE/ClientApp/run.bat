@echo off
echo [RUN] Starting ClientApp...

java -cp "bin;libs/*;lib/*" ^
    main.ClientStart

pause