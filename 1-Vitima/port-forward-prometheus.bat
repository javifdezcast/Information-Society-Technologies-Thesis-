@echo off
:loop
    kubectl port-forward --address 192.168.1.39 svc/prometheus-1737411740-server 9091:80 &
    echo Port-forward connection lost, retrying in 2 seconds...
    timeout /t 2 /nobreak >nul
goto loop
