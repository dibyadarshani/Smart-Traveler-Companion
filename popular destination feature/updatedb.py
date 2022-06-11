from firebase import firebase 
import os

dirpath= "./cityimages"
a = [s for s in os.listdir(dirpath) if os.path.isfile(os.path.join(dirpath, s))]
a.sort(key=lambda s: os.path.getctime(os.path.join(dirpath, s)))

print(a)

citylist=['London', 'Paris', 'New York', 'Moscow', 'Dubai', 'Tokyo', 'Singapore', 'Los Angeles', 'Barcelona', 'Madrid', 'Rome', 'Doha', 
'Chicago', 'Abu Dhabi', 'San Francisco', 'Amsterdam', 'St. Petersburg', 'Toronto', 'Sydney', 'Berlin', 'Las Vegas', 'Washington', 'Istanbul', 'Vienna',
 'Beijing', 'Prague', 'Milan', 'San Diego', 'Hong Kong', 'Melbourne', 'Boston', 'Houston', 'Dublin', 'Miami', 'Zurich', 'Seattle', 'Budapest', 'Sao Paulo',
  'Munich', 'Bangkok', 'Orlando', 'Seoul', 'Atlanta', 'Dallas', 'Frankfurt', 'Vancouver', 'Austin', 'Montreal', 'Calgary', 'Delhi', 'Lisbon', 'Naples', 
  'Osaka', 'San Jose', 'Riyadh', 'Denver', 'Philadelphia', 'Tel Aviv', 'Copenhagen', 'Brussels', 'Brisbane', 'Valencia', 'Buenos Aires', 'Taipei', 
  'Rio de Janeiro', 'Portland', 'Hamburg', 'Kuwait City', 'Warsaw', 'Athens', 'Perth', 'Helsinki', 'Minneapolis', 'Oslo', 'Shanghai', 'Phoenix',
   'Auckland', 'New Orleans', 'Jerusalem', 'Muscat', 'Nashville', 'Stockholm', 'Santiago', 'Ottawa', 'Baltimore', 'Edmonton', 'Lyon', 'Marseille', 
'Adelaide', 'Gothenburg', 'Bilbao', 'Mexico City', 'Salt Lake City', 'Mumbai', 'Sacramento', 'San Antonio', 'Tucson', 'Seville', 'Charlotte', 'Nanjing']



firebase = firebase.FirebaseApplication('https://smarttravelercompanion-default-rtdb.firebaseio.com/', None)  
for i in range(0,100):
    city=citylist[i]
    imgfile=str(a[i])
    data =  { 'cityname': city,
            'imgfilename': imgfile }  
    result = firebase.post('smarttravelercompanion-default-rtdb/PopularCities',data)  
    print(result)

