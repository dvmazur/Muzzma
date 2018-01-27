from flask import Flask, url_for, send_from_directory, request
import logging, os
from werkzeug.utils import secure_filename
from util import convert_3gp_to_mp

app = Flask(__name__)
file_handler = logging.FileHandler('server.log')
app.logger.addHandler(file_handler)
app.logger.setLevel(logging.INFO)

PROJECT_HOME = os.path.dirname(os.path.realpath(__file__))
UPLOAD_FOLDER = '{}/uploads/'.format(PROJECT_HOME)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER


def create_new_folder(local_dir):
    newpath = local_dir
    if not os.path.exists(newpath):
        os.makedirs(newpath)
    return newpath

def transfer(file):
    return "/uploads/eminem.mp3"


@app.route('/output', methods=['POST'])
def api_root():
    app.logger.info(PROJECT_HOME)

    if request.method == 'POST' and request.files["audio"]:
        app.logger.info(app.config['UPLOAD_FOLDER'])
        track = request.files['audio']
        img_name = secure_filename(track.filename)
        create_new_folder(app.config['UPLOAD_FOLDER'])
        saved_path = os.path.join(app.config['UPLOAD_FOLDER'], img_name)
        app.logger.info("saving {}".format(saved_path))
        track.save(saved_path)

        mp3_path = convert_3gp_to_mp(saved_path, app.config["UPLOAD_FOLDER"])

        output_file = transfer(mp3_path)

        return str({"response": {
            "output_file_path": output_file
        }})

    else:
        return str({"response": "Error"})


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=False)
