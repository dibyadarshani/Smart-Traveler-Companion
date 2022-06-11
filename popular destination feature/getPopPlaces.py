
import requests
from firebase import firebase

citylist=['London', 'Paris', 'New York', 'Moscow', 'Dubai', 'Tokyo', 'Singapore', 'Los Angeles', 'Barcelona', 'Madrid', 'Rome', 'Doha', 
'Chicago', 'Abu Dhabi', 'San Francisco', 'Amsterdam', 'St. Petersburg', 'Toronto', 'Sydney', 'Berlin', 'Las Vegas', 'Washington', 'Istanbul', 'Vienna',
 'Beijing', 'Prague', 'Milan', 'San Diego', 'Hong Kong', 'Melbourne', 'Boston', 'Houston', 'Dublin', 'Miami', 'Zurich', 'Seattle', 'Budapest', 'Sao Paulo',
  'Munich', 'Bangkok', 'Orlando', 'Seoul', 'Atlanta', 'Dallas', 'Frankfurt', 'Vancouver', 'Austin', 'Montreal', 'Calgary', 'Delhi', 'Lisbon', 'Naples', 
  'Osaka', 'San Jose', 'Riyadh', 'Denver', 'Philadelphia', 'Tel Aviv', 'Copenhagen', 'Brussels', 'Brisbane', 'Valencia', 'Buenos Aires', 'Taipei', 
  'Rio de Janeiro', 'Portland', 'Hamburg', 'Kuwait City', 'Warsaw', 'Athens', 'Perth', 'Helsinki', 'Minneapolis', 'Oslo', 'Shanghai', 'Phoenix',
   'Auckland', 'New Orleans', 'Jerusalem', 'Muscat', 'Nashville', 'Stockholm', 'Santiago', 'Ottawa', 'Baltimore', 'Edmonton', 'Lyon', 'Marseille', 
'Adelaide', 'Gothenburg', 'Bilbao', 'Mexico City', 'Salt Lake City', 'Mumbai', 'Sacramento', 'San Antonio', 'Tucson', 'Seville', 'Charlotte', 'Nanjing']


clientId = 'HMTYW22YKAJ4XBTYTHWPFHEIACSMDPFH5SL1X0KZ5JT2OK0C'
clientSecret = 'E5JNUFSA1QD2HP2NBRTVWOETGX1THPSPOTMBDAX4KLSQTB3Y'
version = 20180323

firebase = firebase.FirebaseApplication('https://smarttravelercompanion-default-rtdb.firebaseio.com/', None)  

venues_list=dict()
for city in citylist:
   url = 'https://api.foursquare.com/v2/venues/explore?client_id={}&client_secret={}&near={}&v={}&limit={}'.format(clientId, clientSecret, city, version, 4)
   results = requests.get(url).json()
   items = results['response']['groups'][0]['items']
   #print(items)
   venues=[]
   for item in items:
      venues.append(item['venue']['name']+", "+item['venue']['categories'][0]['name'])
   venues_list[city]=venues
   data =  { 'cityname': city,
            'venue1': venues[0],
            'venue2': venues[1],
            'venue3': venues[2],
            'venue4': venues[3] }  
   result = firebase.post('smarttravelercompanion-default-rtdb/CityVenues',data)  
   print(result)
print(venues_list)


