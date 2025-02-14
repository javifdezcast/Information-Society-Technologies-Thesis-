import requests
from requests.auth import HTTPBasicAuth
from datetime import datetime, timedelta
import platforms

# Initialize platform objects
platform1 = platforms.AWS()
platform2 = platforms.Google()
platform3 = platforms.Azure()
platform4 = platforms.IBM()


def convert_to_unix(timestamp_str, current_date):
    """Convert 'HH:MM:SS' time format to a UNIX timestamp with a given date."""
    dt = datetime.strptime(f"{current_date} {timestamp_str}", "%d/%m/%Y %H:%M:%S")
    return int(dt.timestamp()), dt


def calculate_costs_between(start_timestamp, end_timestamp):
    """Fetch request count and execution time from API and calculate costs."""
    base_url = "http://192.168.1.39:3000/api/datasources/proxy/1/api/v1/query_range"

    # API queries
    query1 = f"{base_url}?query=sum_over_time(gateway_function_invocation_total{{code='200'}}[{end_timestamp - start_timestamp}s])&start={start_timestamp}&end={end_timestamp}&step={end_timestamp - start_timestamp}"
    query2 = f"{base_url}?query=sum_over_time(gateway_functions_seconds_sum[{end_timestamp - start_timestamp}s])&start={start_timestamp}&end={end_timestamp}&step={end_timestamp - start_timestamp}"

    # Make API requests
    res1 = requests.get(query1, auth=HTTPBasicAuth('admin', 'GrafanaTFG2025'))
    res2 = requests.get(query2, auth=HTTPBasicAuth('admin', 'GrafanaTFG2025'))

    # Extract values
    request_count = int(res1.json()["data"]["result"][0]["values"][0][1])
    execution_time = float(res2.json()["data"]["result"][0]["values"][0][1]) * 1000  # Convert to milliseconds

    # Calculate costs
    aws_price = platform1.calculatePrice(request_count, execution_time, 128)
    google_price = platform2.calculatePrice(request_count, execution_time, 128)
    azure_price = platform3.calculatePrice(request_count, execution_time, 128)
    ibm_price = platform4.calculatePrice(request_count, execution_time, 128)

    return aws_price, google_price, azure_price, ibm_price


def process_file(input_file_path):
    """Process the input file and print results."""
    default_date = "29/01/2025"  # Default date (adjust as needed)

    with open(input_file_path, mode='r', encoding='utf-8-sig') as infile:
        for line in infile:
            parts = line.strip().split(" ")
            if len(parts) != 5:
                continue  # Skip invalid lines

            size = parts[1]
            file_format = parts[3]
            start_time = parts[4]

            # Convert start time to UNIX timestamp
            start_timestamp, start_datetime = convert_to_unix(start_time, default_date)

            # Add 3 minutes
            end_datetime = start_datetime + timedelta(minutes=3)

            # If the time crosses 00:00, adjust the date
            if end_datetime.day > start_datetime.day:
                default_date = (start_datetime + timedelta(days=1)).strftime("%d/%m/%Y")

            # Convert new end time to UNIX timestamp
            end_timestamp, _ = convert_to_unix(end_datetime.strftime("%H:%M:%S"), default_date)

            # Calculate costs
            aws_price, google_price, azure_price, ibm_price = calculate_costs_between(start_timestamp, end_timestamp)

            # Print results in the required format
            print(
                f"Size {size}, {file_format}, AWS: {aws_price}, Google: {google_price}, Azure: {azure_price}, IBM: {ibm_price}")


# File path for input data
input_file_path = "C:\\Users\\jfdez\\Information-Society-Technologies-Thesis-\\3-Documentos\\Experimentos\\Experimento2.2\\Resultados2025-01-29-20-04.csv"  # Replace with the actual file name

# Process the file and print results
process_file(input_file_path)
