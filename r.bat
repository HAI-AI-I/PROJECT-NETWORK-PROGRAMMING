@echo off
start "Admin Server" java -cp CODE/AdminServer/bin AdminServerApp
start "Client App" java -cp "CODE/ClientApp/libs/*;CODE/ClientApp/lib/*;CODE/ClientApp/bin" main.ClientStart
exit
