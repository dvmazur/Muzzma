from flask import Flask, url_for, send_from_directory, request
import logging, os
from werkzeug.utils import secure_filename
from networks.network import AudioNet
import random

app = Flask(__name__, static_url_path="/output/")
file_handler = logging.FileHandler("server.log")
app.logger.addHandler(file_handler)
app.logger.setLevel(logging.INFO)

PROJECT_HOME = os.path.dirname(os.path.realpath(__file__))
UPLOAD_FOLDER = "{}/uploads/".format(PROJECT_HOME)
app.config["UPLOAD_FOLDER"] = UPLOAD_FOLDER

net = AudioNet()

lines = open("shaq").readlines()

def create_new_folder(local_dir):
    newpath = local_dir
    if not os.path.exists(newpath):
        os.makedirs(newpath)
    return newpath

def transfer(content, style):
    net.transfer_style(content, style, UPLOAD_FOLDER+"/out.mp3")

    return "localhost:5000/output/out.mp3"


# receive file and transfer style
@app.route("/input", methods=["POST"])
def api_root():
    app.logger.info(PROJECT_HOME)

    if request.method == "POST" and request.files["audio"]:
        app.logger.info(app.config["UPLOAD_FOLDER"])
        track = request.files["audio"]
        img_name = secure_filename(track.filename)
        create_new_folder(app.config["UPLOAD_FOLDER"])
        saved_path = os.path.join(app.config["UPLOAD_FOLDER"], img_name)
        app.logger.info("saving {}".format(saved_path))
        track.save(saved_path)

        output_file = transfer(saved_path, "../audio/futurama.mp3")

        return str({"response": {
            "output_file_path": output_file
        }})

    else:
        return str({"response": "Error"})

@app.route("/output/<path:path>", methods=["GET"])
def serve_static(path):
    return send_from_directory(UPLOAD_FOLDER, path)

@app.route("/rhyme", methods=["GET"])
def hande_rhymes():
    if request.args["phrase"]:
        return random.choice(lines)



if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=False)
