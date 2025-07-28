import os
import time
import json
import zoneinfo
from dataclasses import replace
from zoneinfo import ZoneInfo

import requests
from requests.auth import HTTPBasicAuth
from datetime import datetime, timezone

import platforms

def calcula_precios(timestamp):
    end = int(timestamp) + 3600
    expression1 = "http://" + IP + ":3000/api/datasources/proxy/1/api/v1/query_range?query=scalar(%20sum(%20gateway_function_invocation_total%20%7B%20%20code%3D%22200%22%7D%20))&start=" + timestamp + "&end=" + str(end) + "&step=" + step + "".format(
        int(time.time()))
    expression2 = "http://" + IP + ":3000/api/datasources/proxy/1/api/v1/query_range?query=scalar(%20sum(%20gateway_functions_seconds_sum%20))&start=" + timestamp + "&end=" + str(end) + "&step=" + step + "".format(
        int(time.time()))

    res = requests.get(expression1, auth=HTTPBasicAuth(user, password))
    out = res.json()
    # out = replace_nan_with_zero(out)
    request = int(out["data"]["result"][0]["values"][0][1])

    res = requests.get(expression2, auth=HTTPBasicAuth(user, password))
    out = res.json()
    # out = replace_nan_with_zero(out)
    compute = float(out["data"]["result"][0]["values"][0][1]) * 1000

    print("" + str(request) + "," + str(compute))

    AWS = platform1.calculatePrice(request, compute, 128)
    Google = platform2.calculatePrice(request, compute, 128)
    Azure = platform3.calculatePrice(request, compute, 128)
    IBM = platform4.calculatePrice(request, compute, 128)
    print (f"{AWS:.4f},{Google:.4f},{Azure:.4f},{IBM:.4f}")

def replace_nan_with_zero(obj):
    if isinstance(obj, dict):
        return {k: replace_nan_with_zero(v) for k, v in obj.items()}
    elif isinstance(obj, list):
        return [replace_nan_with_zero(elem) for elem in obj]
    elif isinstance(obj, str) and obj == "NaN":
        return 0
    else:
        return obj


platform1 = platforms.AWS()
platform2 = platforms.Google()
platform3 = platforms.Azure()
platform4 = platforms.IBM()

IP = "192.168.1.40"
user = "admin"
password = "GrafanaTFG2025"
step = "1"

iteraciones = [
    "01/07/2025 12:48:00",
    "09/07/2025 00:15:00"]

print("AWS,Google,Azure,IMB")
for iteracion in iteraciones:
    local_tz = datetime.now().astimezone().tzinfo
    parsedDateTime = datetime.strptime(iteracion, "%d/%m/%Y %H:%M:%S").replace(tzinfo=local_tz)
    timestamp = str(int(parsedDateTime.timestamp()))
    calcula_precios(timestamp)
