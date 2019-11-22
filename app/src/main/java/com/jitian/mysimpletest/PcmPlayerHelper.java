package com.jitian.mysimpletest;

import android.media.AudioTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author YangDing
 * @date 2019/11/8
 */
class PcmPlayerHelper {

    private static final int RATE = PcmConfig.RATE;
    private static final int CHANNEL_MODE = PcmConfig.CHANNEL_MODE_PLAY;
    private static final int AUDIO_STREAM = PcmConfig.AUDIO_STREAM;
    private static final int PCM_BIT = PcmConfig.PCM_BIT;
    private static final int MODE_STREAM = PcmConfig.MODE_STREAM_PLAY;
    private volatile boolean isPlaying = false;
    private int mBufferSize;
    private AudioTrack mAudioTrack;

    private PcmPlayerHelper() {
        mBufferSize = AudioTrack.getMinBufferSize(RATE, CHANNEL_MODE, PCM_BIT);
        mAudioTrack = new AudioTrack(AUDIO_STREAM, RATE, CHANNEL_MODE, PCM_BIT, mBufferSize, MODE_STREAM);
    }

    static PcmPlayerHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final PcmPlayerHelper INSTANCE = new PcmPlayerHelper();
    }

    void play(String pcmFilePath) {
        if (isPlaying) {
            return;
        }
        if (mAudioTrack == null || mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
            mAudioTrack = new AudioTrack(AUDIO_STREAM, RATE, CHANNEL_MODE, PCM_BIT, mBufferSize, MODE_STREAM);
        }
        if (pcmFilePath != null) {
            File pcmFile = new File(pcmFilePath);
            if (pcmFile.exists() && pcmFile.canRead() && pcmFile.length() > 0) {
                isPlaying = true;
                ThreadUtil.execute(new PlayRunnable(pcmFile));
            }
        }
    }

    private void stop() {
        isPlaying = false;
        if (mAudioTrack != null) {
            mAudioTrack.stop();
        }
    }

    void release() {
        isPlaying = false;
        if (mAudioTrack != null) {
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    class PlayRunnable implements Runnable {

        private File mPcmFile;

        PlayRunnable(File pcmFile) {
            mPcmFile = pcmFile;
        }

        @Override
        public void run() {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(mPcmFile);
                byte[] buffer = new byte[mBufferSize];
                int len;
                mAudioTrack.play();
                while (((len = fileInputStream.read(buffer)) == mBufferSize) && isPlaying) {
                    mAudioTrack.write(buffer, 0, len);
                }
                stop();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
