@echo off
:loop
    kubectl port-forward --address 192.168.1.39 svc/prometheus -n openfaas 9090:9090 &
    echo Port-forward connection lost, retrying in 2 seconds...
    timeout /t 2 /nobreak >nul
goto loop
