@echo off
:loop
    kubectl port-forward svc/alertmanager -n openfaas 9093:9093 &
    echo "Port-forward connection lost, retrying in 2 seconds..."
    timeout /t 2 /nobreak >nul
goto loop
