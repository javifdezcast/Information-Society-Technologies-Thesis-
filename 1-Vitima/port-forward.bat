@echo off
:loop
    kubectl port-forward -n openfaas --address 192.168.1.34	svc/gateway 8080:8080 &
    echo Port-forward connection lost, retrying in 2 seconds...
    timeout /t 2 /nobreak >nul
goto loop