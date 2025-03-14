@echo off
:loop
    kubectl port-forward -n monitoring svc/my-grafana 3000:80
    echo Port-forward connection lost, retrying in 2 seconds...
    timeout /t 2 /nobreak >nul
goto loop