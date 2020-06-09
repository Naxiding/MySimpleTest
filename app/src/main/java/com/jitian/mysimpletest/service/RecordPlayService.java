package com.jitian.mysimpletest.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jitian.mysimpletest.utils.LogUtil;
import com.jitian.mysimpletest.utils.PcmConfig;
import com.jitian.mysimpletest.utils.ThreadUtil;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author YangDing
 * @date 2020/6/2
 */
public class RecordPlayService extends Service {

    private AudioTrack mAudioTrack;
    private AudioRecord mAudioRecord;
    private volatile boolean mIsRecording = false;
    private volatile boolean mIsPlaying = false;
    private volatile ConcurrentLinkedQueue<byte[]> mBytesQueue;


    private static final int REC_RATE = 44100;
    private static final int PLAY_RATE = 48000;

    private static final int CHANNEL_MODE = PcmConfig.CHANNEL_MODE_PLAY;
    private static final int AUDIO_STREAM = PcmConfig.AUDIO_STREAM;
    private static final int PCM_BIT = PcmConfig.PCM_BIT;
    private static final int MODE_STREAM = PcmConfig.MODE_STREAM_PLAY;
    private static final int AUDIO_RES = PcmConfig.AUDIO_RES;
    private int mRecBufferSize;

    private static final int SIZE_LIMIT = 5;
    private boolean mUseQueue = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        int playBufferSize = AudioTrack.getMinBufferSize(PLAY_RATE, CHANNEL_MODE, PCM_BIT);
        mAudioTrack = new AudioTrack(AUDIO_STREAM, PLAY_RATE, CHANNEL_MODE, PCM_BIT, playBufferSize, MODE_STREAM);
        mRecBufferSize = AudioRecord.getMinBufferSize(REC_RATE, PcmConfig.CHANNEL_MODE_RECORD, PCM_BIT);
        mAudioRecord = new AudioRecord(AUDIO_RES, REC_RATE, PcmConfig.CHANNEL_MODE_RECORD, PCM_BIT, mRecBufferSize);
        mBytesQueue = new ConcurrentLinkedQueue<byte[]>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecordPlan();
        if (mUseQueue) {
            startPlayPlan();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsRecording = false;
    }

    private void release() {
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
        }
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
        }
        mBytesQueue = null;
    }

    private Runnable mRecordRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                byte[] buffer = new byte[mRecBufferSize];
                mAudioRecord.startRecording();
                mAudioTrack.play();
                while (mIsRecording && mBytesQueue != null) {
                    int bufferReadResult = mAudioRecord.read(buffer, 0, buffer.length);
                    if (bufferReadResult > 0) {
                        if (mUseQueue) {
                            byte[] tmpBuf = new byte[bufferReadResult];
                            System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
                            if (mBytesQueue.size() > SIZE_LIMIT) {
                                mBytesQueue.poll();
                            }
                            mBytesQueue.add(tmpBuf);
                        } else {
                            mAudioTrack.write(buffer, 0, buffer.length);
                        }
                    }
                }
                release();
            } catch (Throwable t) {
                LogUtil.d("" + t.getMessage());
            }
        }
    };

    private Runnable mPlayRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mAudioTrack.play();
                while (mIsPlaying && mBytesQueue != null) {
                    byte[] buffer = mBytesQueue.poll();
                    if (buffer != null && buffer.length > 0) {
                        mAudioTrack.write(buffer, 0, buffer.length);
                    }
                }
                release();
            } catch (Throwable t) {
                LogUtil.d("" + t.getMessage());
            }
        }
    };

    private synchronized void startRecordPlan() {
        if (!mIsRecording) {
            mIsRecording = true;
            ThreadUtil.execute(mRecordRunnable);
        }
    }

    private synchronized void startPlayPlan() {
        if (!mIsPlaying) {
            mIsPlaying = true;
            ThreadUtil.execute(mPlayRunnable);
        }
    }

}
