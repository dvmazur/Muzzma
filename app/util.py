from pydub import AudioSegment


def convert_3gp_to_mp(input_3gp, output_mp3):
    AudioSegment.from_file(input_3gp).export(output_mp3, format="mp3")

def overlap(file_a, file_b, output_f="combined.mp3"):
    sound1 = AudioSegment.from_file(file_a)
    sound2 = AudioSegment.from_file(file_b)

    combined = sound1.overlay(sound2)

    combined.export(output_f, format='mp3')
