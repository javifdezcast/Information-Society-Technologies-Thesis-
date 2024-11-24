import csv
import requests
from requests.auth import HTTPBasicAuth
from datetime import datetime
import platforms

# Initialize platform objects
platform1 = platforms.AWS()
platform2 = platforms.Google()
platform3 = platforms.Azure()
platform4 = platforms.IBM()


def convert_to_unix(timestamp_str):
    # Convert the "dd/mm/yyyy HH:mm:ss" format to a UNIX timestamp
    dt = datetime.strptime(timestamp_str, "%d/%m/%Y %H:%M:%S")
    return int(dt.timestamp())


def calculate_costs_between(start_timestamp, end_timestamp):
    # URL templates using start and end timestamps for request range
    expression1 = f"http://192.168.1.34:3000/api/datasources/proxy/1/api/v1/query_range?query=scalar(sum(gateway_function_invocation_total{{code='200'}}))&start={start_timestamp}&end={end_timestamp}&step=1"
    expression2 = f"http://192.168.1.34:3000/api/datasources/proxy/1/api/v1/query_range?query=scalar(sum(gateway_functions_seconds_sum))&start={start_timestamp}&end={end_timestamp}&step=1"

    # Request for total number of requests within the given time range
    res1 = requests.get(expression1, auth=HTTPBasicAuth('admin', 'devops123'))
    out1 = res1.json()
    request_count = int(out1["data"]["result"][0]["values"][0][1])

    # Request for total execution time within the given time range
    res2 = requests.get(expression2, auth=HTTPBasicAuth('admin', 'devops123'))
    out2 = res2.json()
    execution_time = float(out2["data"]["result"][0]["values"][0][1]) * 1000  # Convert to milliseconds

    # Calculate costs for each platform
    aws_price = platform1.calculatePrice(request_count, execution_time, 128)
    google_price = platform2.calculatePrice(request_count, execution_time, 128)
    azure_price = platform3.calculatePrice(request_count, execution_time, 128)
    ibm_price = platform4.calculatePrice(request_count, execution_time, 128)

    return aws_price, google_price, azure_price, ibm_price


def process_csv(input_file_path, output_file_path):
    with open(input_file_path, mode='r', encoding='utf-8-sig') as infile, open(output_file_path, mode='w', newline='') as outfile:
        csv_reader = csv.DictReader(infile)
        csv_writer = csv.writer(outfile)

        # Write header to output file
        csv_writer.writerow(["Time", "AWS", "Google", "Azure", "IBM"])

        default_date = "10/10/2024"  # Adjust this date as needed

        for row in csv_reader:
            # Add default date to start and end times
            start_time_str = f"{default_date} {row['START_TIME']}"
            end_time_str = f"{default_date} {row['END_TIME']}"

            # Convert start and end times to UNIX timestamps
            start_timestamp = convert_to_unix(start_time_str)
            end_timestamp = convert_to_unix(end_time_str)

            # Calculate costs
            aws_price, google_price, azure_price, ibm_price = calculate_costs_between(start_timestamp, end_timestamp)

            # Write the results to the output CSV file
            csv_writer.writerow([start_time_str, aws_price, google_price, azure_price, ibm_price])


# File paths for input and output CSV files
input_csv_path = "tiempos.csv"  # Replace with the path to your input CSV file
output_csv_path = "precios.csv"  # Replace with the path to your desired output CSV file

# Process the CSV and write results to output file
process_csv(input_csv_path, output_csv_path)
