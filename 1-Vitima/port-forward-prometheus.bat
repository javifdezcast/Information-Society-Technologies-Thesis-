@echo off
for /f "tokens=*" %%i in ('ipconfig ^| findstr /R "IPv4.*192.*"') do set "IP=%%i"
for /f "tokens=2 delims=:" %%a in ("%IP%") do set IP=%%a
set IP=%IP: =%
:loop
    kubectl port-forward --address %IP% svc/prometheus-1737411740-server 9091:80 &
    echo Port-forward connection lost, retrying in 2 seconds...
    timeout /t 2 /nobreak >nul
goto loop
