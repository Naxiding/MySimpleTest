package com.jitian.mysimpletest.utils;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import com.jitian.mysimpletest.MyApplication;

import java.io.IOException;

/**
 * @author YangDing
 * @date 2020/5/14
 */
public class MediaPlayerUtil {

    private MediaPlayer mMediaPlayer;
    private SoundPool mSoundPool;
    public static final int POOL_TYPE = 2;
    private int mCurrentVoidId = 0;
    private AssetManager mAssetManager;

    public MediaPlayerUtil() {
        mAssetManager = MyApplication.getInstance().getAssets();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入最多播放音频数量,
            builder.setMaxStreams(1);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build());
            mSoundPool = builder.build();
        } else {
            mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
    }

    public void playRawVoiceByType(int rawId, int type) {
        if (type == POOL_TYPE) {
            playRawVoiceByPool(rawId);
        } else {
            playRawVoice(rawId);
        }
    }

    private void playRawVoiceByPool(int rawId) {
        mCurrentVoidId = mSoundPool.load(MyApplication.getInstance(), rawId, 1);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (soundPool != null && status == 0) {
                    soundPool.play(mCurrentVoidId, 1, 1, 1, 0, 1);
                }
            }
        });
    }

    private void playRawVoice(int rawId) {
        if (rawId == -1) {
            return;
        }
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        try {
            mMediaPlayer.reset();
            AssetFileDescriptor file = MyApplication.getInstance().getResources().openRawResourceFd(rawId);
            mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.prepareAsync();
        } catch (IOException | IllegalStateException e) {
            LogUtil.d(e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        if (mSoundPool != null) {
            mSoundPool.stop(mCurrentVoidId);
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
    }

}
