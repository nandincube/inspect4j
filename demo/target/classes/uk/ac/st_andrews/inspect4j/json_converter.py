import json
from json2html import *
import sys

def convert_json_to_html(json_file_path):
    print("hi")
    print(json_file_path)
    try:
        with open(json_file_path, 'r') as file:
            json_data = json.read(file)

        html = json2html.convert(json=json_data)
        return html
    except FileNotFoundError:
        print(f"File not found!: {json_file_path}")
        return None
    except json.JSONDecodeError as e:
        print(f"Error decoding JSON: {e}")
        return None
    
if __name__ == "__main__":
# Get the JSON file path from command-line arguments
    if len(sys.argv) != 2:
        print("Usage: python convert_json_to_html.py <json_file_path>")
        sys.exit(1)
    json_file_path = sys.argv[1]

    # Call the conversion function with the provided file path
    convert_json_to_html(json_file_path)