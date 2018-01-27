from pydub import AudioSegment


def convert_3gp_to_mp(input_3gp, output_mp3):
    AudioSegment.from_file(input_3gp).export(output_mp3, format="mp3")
