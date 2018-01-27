import requests

url="http://localhost:5000/upload"

files = {'audio': open('/home/deniska/muzzma/audio/eminem.mp3', 'rb')}

print requests.post(url, files=files).text