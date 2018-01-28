import tensorflow as tf
import librosa
import numpy as np


N_FFT = 2048
N_FILTERS = 4096
N_CHAN = 1025
N_SAMPLES = 430
ALPHA= 1e-2
learning_rate= 1e-3
iterations = 100


class AudioNet:

    def __init__(self, n_chan=N_CHAN, n_filters=N_FILTERS, n_samp=N_SAMPLES):
        self.n_chan = n_chan
        self.n_samp = n_samp
        self.n_filters = n_filters

        std = np.sqrt(2) * np.sqrt(2.0 / ((self.n_chan + self.n_filters) * 11))
        self.kernel = np.random.randn(1, 11, self.n_chan, self.n_filters) * std

    def transfer_style(self, content_file, style_file, output_file="out.mp3"):
        a_content, fs = read_audio_spectum(content_file)
        a_style, fs = read_audio_spectum(style_file)

        a_content_tf = np.ascontiguousarray(a_content.T[None, None, :, :])
        a_style_tf = np.ascontiguousarray(a_style.T[None, None, :, :])

        content_features, style_gram = self._compute_features(a_content_tf, a_style_tf)

        res = self._optimize(content_features, style_gram)

        x = self._invert_spectrogram(a_content, res)
        librosa.output.write_wav(output_file, x, fs)

    def _compute_features(self, content, style):
        g = tf.Graph()
        with g.as_default(), g.device('/cpu:0'), tf.Session() as sess:
            # data shape is "[batch, in_height, in_width, in_channels]",
            x = tf.placeholder('float32', [1, 1, self.n_samp, self.n_chan], name="x")

            kernel_tf = tf.constant(self.kernel, name="kernel", dtype='float32')
            conv = tf.nn.conv2d(
                x,
                kernel_tf,
                strides=[1, 1, 1, 1],
                padding="VALID",
                name="conv")

            net = tf.nn.relu(conv)

            content_features = net.eval(feed_dict={x: content})
            style_features = net.eval(feed_dict={x: style})

            features = np.reshape(style_features, (-1, self.n_filters))
            style_gram = np.matmul(features.T, features) / self.n_samp

        return content_features, style_gram

    def _optimize(self, content_features, style_gram):
        with tf.Graph().as_default():
            # Build graph with variable input
            #     x = tf.Variable(np.zeros([1,1,N_SAMPLES,N_CHANNELS], dtype=np.float32), name="x")
            x = tf.Variable(np.random.randn(1, 1, self.n_samp, self.n_chan).astype(np.float32) * 1e-3, name="x")

            kernel_tf = tf.constant(self.kernel, name="kernel", dtype='float32')
            conv = tf.nn.conv2d(
                x,
                kernel_tf,
                strides=[1, 1, 1, 1],
                padding="VALID",
                name="conv")

            net = tf.nn.relu(conv)

            content_loss = ALPHA * 2 * tf.nn.l2_loss(
                net - content_features)

            _, height, width, number = map(lambda i: i.value, net.get_shape())

            size = height * width * number
            feats = tf.reshape(net, (-1, number))
            gram = tf.matmul(tf.transpose(feats), feats) / N_SAMPLES
            style_loss = 2 * tf.nn.l2_loss(gram - style_gram)

            # Overall loss
            loss = content_loss + style_loss

            opt = tf.contrib.opt.ScipyOptimizerInterface(
                loss, method='L-BFGS-B', options={'maxiter': 300})

            # Optimization
            with tf.Session() as sess:
                sess.run(tf.initialize_all_variables())

                opt.minimize(sess)
                result = x.eval()

        return result

    def _invert_spectrogram(self, content, result):
        a = np.zeros_like(content)
        a[:self.n_chan, :] = np.exp(result[0, 0].T) - 1

        # This code is supposed to do phase reconstruction
        p = 2 * np.pi * np.random.random_sample(a.shape) - np.pi
        for i in range(500):
            S = a * np.exp(1j * p)
            x = librosa.istft(S)
            p = np.angle(librosa.stft(x, N_FFT))

        return x


def read_audio_spectum(filename):
    x, fs = librosa.load(filename)
    S = librosa.stft(x, N_FFT)
    p = np.angle(S)
    S = np.log1p(np.abs(S[:, :430]))
    return S, fs


if __name__ == "__main__":
    net = AudioNet()
    net.transfer_style("inputs/eminem.mp3", "inputs/imperial.mp3", "penis.mp3")
