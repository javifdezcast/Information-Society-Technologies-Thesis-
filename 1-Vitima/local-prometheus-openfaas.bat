@echo off
:loop
    kubectl port-forward svc/prometheus -n openfaas 9090:9090 &
    echo Port-forward connection lost, retrying in 2 seconds...
    timeout /t 2 /nobreak >nul
goto loop
