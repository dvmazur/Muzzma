import librosa
import numpy as np

N_FFT = 2048

# Reads wav file and produces spectrum
# Fourier phases are ignored
def read_audio_spectum(filename):
    x, fs = librosa.load(filename)
    S = librosa.stft(x, N_FFT)
    p = np.angle(S)
    return np.log1p(np.abs(S[np.newaxis,:,:430])), fs
