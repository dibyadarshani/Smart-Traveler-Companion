from urllib.request import urlopen
from bs4 import BeautifulSoup

import argparse
import json
import os
import time

import requests
import tqdm
from pexels_api import API


page = urlopen('https://www.bestcities.org/rankings/worlds-best-cities/')
soup = BeautifulSoup(page.read())
page_find=soup.findAll('h1')
city_List = []
for page_data in page_find:
    city=page_data.text
    x,cityname=city.split(" ",1)
    city_List.append(cityname)
print(city_List)

'''

['London', 'Paris', 'New York', 'Moscow', 'Dubai', 'Tokyo', 'Singapore', 'Los Angeles', 'Barcelona', 'Madrid', 'Rome', 'Doha', 
'Chicago', 'Abu Dhabi', 'San Francisco', 'Amsterdam', 'St. Petersburg', 'Toronto', 'Sydney', 'Berlin', 'Las Vegas', 'Washington', 'Istanbul', 'Vienna', 'Beijing', 'Prague', 'Milan', 'San Diego', 'Hong Kong', 'Melbourne', 'Boston', 'Houston', 'Dublin', 'Miami', 'Zurich', 'Seattle', 'Budapest', 'Sao Paulo', 'Munich', 'Bangkok', 'Orlando', 'Seoul', 'Atlanta', 'Dallas', 'Frankfurt', 'Vancouver', 'Austin', 'Montreal', 'Calgary', 'Delhi', 'Lisbon', 'Naples', 'Osaka', 'San Jose', 'Riyadh', 'Denver', 'Philadelphia', 'Tel Aviv', 'Copenhagen', 'Brussels', 'Brisbane', 'Valencia', 'Buenos Aires', 'Taipei', 'Rio de Janeiro', 'Portland', 'Hamburg', 'Kuwait City', 'Warsaw', 'Athens', 'Perth', 'Helsinki', 'Minneapolis', 'Oslo', 'Shanghai', 'Phoenix', 'Auckland', 'New 
Orleans', 'Jerusalem', 'Muscat', 'Nashville', 'Stockholm', 'Santiago', 'Ottawa', 'Baltimore', 'Edmonton', 'Lyon', 'Marseille', 
'Adelaide', 'Gothenburg', 'Bilbao', 'Mexico City', 'Salt Lake City', 'Mumbai', 'Sacramento', 'San Antonio', 'Tucson', 'Seville', 'Charlotte', 'Nanjing']

'''


'''

for el in city_List:
    data =  { 'cityname': el }  
    result = firebase.post('/smarttravelercompanion-default-rtdb/PopularCities/',data)  
    print(result)

'''

PAGE_LIMIT = 1
RESULTS_PER_PAGE = 1

PEXELS_API_KEY = "563492ad6f91700001000001b586da6b8e974b8281c78863c56cb1bd"
api = API(PEXELS_API_KEY)
photos_dict = {}
page = 0
counter = 0

for query in city_List:
# Step 1: Getting urls and meta information
    page=0
    photos_dict = {}
    while page < PAGE_LIMIT:
        api.search(query, page=page, results_per_page=RESULTS_PER_PAGE)
        photos = api.get_entries()
        for photo in tqdm.tqdm(photos):
            photos_dict[photo.id] = vars(photo)['_Photo__photo']
            counter += 1
            if not api.has_next_page:
                break
            page += 1

    print(f"Finishing at page: {page}")
    print(f"Images were processed: {counter}")

    # Step 2: Downloading
    PATH = './cityimages'
    RESOLUTION = 'original'

    if photos_dict:
        os.makedirs(PATH, exist_ok=True)
        
        # Saving dict
        #with open(os.path.join(PATH, f'{query}.json'), 'w') as fout:
            #json.dump(photos_dict, fout)
        
        for val in tqdm.tqdm(photos_dict.values()):
            url = val['src'][RESOLUTION]
            fname = os.path.basename(val['src']['original'])
            image_path = os.path.join(PATH, fname)
            if not os.path.isfile(image_path):
                response = requests.get(url, stream=True)
                with open(image_path, 'wb') as outfile:
                    outfile.write(response.content)
            else:
                # ignore if already downloaded
                print(f"File {image_path} exists")

