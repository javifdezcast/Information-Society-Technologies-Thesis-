import sched
import threading
import time as time_module
import datetime
import subprocess
from flask import Flask, request, jsonify
import ctypes

app = Flask(__name__)

def is_admin():
    """Check if the script is running as Administrator"""
    try:
        return ctypes.windll.shell32.IsUserAnAdmin()
    except:
        return False

if not is_admin():
    print("Restarting with admin privileges...")
    subprocess.run(["powershell", "-Command", "Start-Process python -ArgumentList '{} {}' -Verb RunAs".format(__file__, " ".join(subprocess.list2cmdline([])))], shell=True)
    exit()

@app.route('/block', methods=['POST'])
def block_ip():
    print("Solicitud de bloqueo recibida.")
    try:
        result = subprocess.run(
            ["powershell", "-ExecutionPolicy", "Bypass", "-File", "block-ip.ps1", "192.168.1.48"],
            capture_output=True, text=True
        )
        return jsonify({"message": result.stdout.strip()}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/unblock', methods=['POST'])
def unblock_ip():
    scheduler = sched.scheduler(time_module.time, time_module.sleep)

    data = request.get_json()
    if not data or 'time' not in data:
        return jsonify({"error": "Missing 'time' in request body."}), 400

    try:
        timestamp = float(data['time'])
        delay = timestamp - time_module.time()
        if delay < 0:
            return jsonify({"error": "Timestamp is in the past."}), 400

        def run_script():
            print("Running unblock-ip.ps1 script...")
            subprocess.run(
                ["powershell", "-ExecutionPolicy", "Bypass", "-File", "unblock-ip.ps1", "192.168.1.48"],
                capture_output=True, text=True
            )

        scheduler.enter(delay, 1, run_script)
        threading.Thread(target=scheduler.run, daemon=True).start()

        return jsonify({"message": "Unblock script scheduled."}), 200

    except (ValueError, TypeError) as e:
        return jsonify({"error": f"Invalid timestamp: {str(e)}"}), 400
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
    while True:
        now = datetime.datetime.now()
        print("The time is:", now.strftime("%H:%M:%S"))
        time_module.sleep(60)