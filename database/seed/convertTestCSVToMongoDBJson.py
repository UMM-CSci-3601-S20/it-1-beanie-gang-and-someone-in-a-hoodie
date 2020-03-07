import json

with open('ownersTest.json') as jsonFile:
  json_data = json.load(jsonFile)

for record in json_data:
  print(record)
