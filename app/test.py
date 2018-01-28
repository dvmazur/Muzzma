import requests

files = {"audio": open("/home/deniska/muzzma/audio/fire.mp3")}

response = requests.post("http://localhost:5000/input", files=files)

print response.text