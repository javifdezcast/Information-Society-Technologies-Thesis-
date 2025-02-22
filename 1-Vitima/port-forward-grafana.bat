@echo off
:loop
    kubectl port-forward -n monitoring --address 192.168.1.45 svc/my-grafana 3000:80
    echo Port-forward connection lost, retrying in 2 seconds...
    timeout /t 2 /nobreak >nul
goto loop