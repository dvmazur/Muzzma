import lasagne
from lasagne.layers import InputLayer, Conv1DLayer as ConvLayer, get_output
from lasagne.utils import floatX

import theano
import theano.tensor as T

import numpy as np

from scipy import optimize

import librosa

from util import read_audio_spectum


N_CHAN = 1025
N_SAMPLES = 430
N_FILTERS = 4096
STYLE_ALPHA = 1e-2
N_FFT = 2048


class AudioNet:
    def __init__(self, n_chan=N_CHAN, n_samples=N_SAMPLES, n_filters=N_FILTERS, style_alpha=STYLE_ALPHA):
        """
        Build an AuidNet instance
        :param n_chan: number of channels
        :param n_samples: number of samples
        """
        self.n_chan = n_chan
        self.n_samples = n_samples
        self.n_filt = n_filters

        self.content_input = T.tensor3("input content")
        self.style_input = T.tensor3("input style")
        self.generated = T.tensor3("generated image")

        self.net = self._build_net()

        self.content_features = get_output(self.net.conv, {self.net.inputs: self.content_input})
        self.style_features = get_output(self.net.conv, {self.net.inputs: self.style_input})
        self.gen_features = get_output(self.net.conv, {self.net.inputs: self.generated})

        loss = style_loss(self.style_features, self.gen_features) + \
            STYLE_ALPHA * content_loss(self.content_features, self.gen_features)

        grad = T.grad(loss, self.generated)

        self.loss_func = theano.function([self.generated, self.style_input, self.content_input], loss)
        self.grad_func = theano.function([self.generated, self.style_input, self.content_input], grad)

    def _build_net(self):
        class net:
            inputs = InputLayer((1, self.n_chan, self.n_samples))
            conv = ConvLayer(inputs, self.n_filt, 11, W=lasagne.init.GlorotNormal(gain='relu'))

        return net

    def _optimize(self, pic, style):
        t = floatX(np.zeros((1, self.n_chan, self.n_samples)))
        content = pic

        eval_loss = EvalFunc(style, content, self.loss_func, self.n_chan, self.n_samples)
        eval_grad = EvalFunc(style, content, self.grad_func, self.n_chan, self.n_samples)

        res = optimize.fmin_l_bfgs_b(eval_loss, t.flatten(), fprime=eval_grad, maxfun=500)
        t = res[0].reshape((1, self.n_chan, self.n_samples))

        a = np.zeros_like(pic[0])
        a[:self.n_chan, :] = np.exp(t[0]) - 1

        p = 2 * np.pi * np.random.random_sample(a.shape) - np.pi
        for i in range(500):
            print "Iteration ", i
            S = a * np.exp(1j * p)
            x = librosa.istft(S)
            p = np.angle(librosa.stft(x, N_FFT))

        return x

    def transfer_style(self, content_path, style_path, save_path="output.mp3"):
        content, fs = read_audio_spectum(content_path)
        style, fs = read_audio_spectum(style_path)

        out = self._optimize(content, style)

        librosa.output.write_wav(save_path, out, fs)




class EvalFunc:
    def __init__(self, style, content, func, n_chan, n_samples):
        self.style = style
        self.content = content
        self.n_chan = n_chan
        self.n_samples = n_samples
        self.func = func

    def __call__(self, generated):
        generated = floatX(generated.reshape((1, self.n_chan, self.n_samples)))
        return np.array(self.func(generated, self.style, self.content).flatten().astype('float64'))


def gram_matrix(x):
    g = T.tensordot(x, x, axes=([2], [2])) / x.shape[2]
    return g

def style_loss(A, X):
    G1 = gram_matrix(A)
    G2 = gram_matrix(X)
    loss = ((G1 - G2)**2).sum()
    return loss

def content_loss(A, X):
    return ((A - X)**2).sum()


if __name__ == "__main__":
    # Your tests here

    CONTENT_PATH = "audio/imperial.mp3"
    STYLE_PATH = "audio/futurama.mp3"

    net = AudioNet()
    print "It compilled"

    net.transfer_style(CONTENT_PATH, STYLE_PATH)
