package com.jitian.mysimpletest.activity;

import android.app.Instrumentation;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.jitian.mysimpletest.R;
import com.jitian.mysimpletest.utils.ThreadUtil;

import java.io.IOException;

/**
 * @author YangDing
 * @date 2020/3/26
 */
public class AudioTestActivity extends AppCompatActivity implements View.OnClickListener {

    private AudioManager mAudioManager;
    private int mMaxVolume;
    private int mCurrentVolume;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_test);
        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.sub).setOnClickListener(this);
        findViewById(R.id.change).setOnClickListener(this);
        findViewById(R.id.mute_on).setOnClickListener(this);
        findViewById(R.id.mute_on).setOnClickListener(this);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d("yangding", "mMaxVolume:" + mMaxVolume + " , mCurrentVolume:" + mCurrentVolume);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                addVolume();
                break;
            case R.id.sub:
                subVolume();
                break;
            case R.id.change:
                changeVolume(100);
                break;
            case R.id.mute_on:
            case R.id.mute_off:
                setMute();
                break;
            default:
                break;
        }
    }

    private void addVolume() {
        if (mAudioManager != null) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        }
    }

    private void subVolume() {
        if (mAudioManager != null) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        }
    }

    private void changeVolume(int volume) {
        if (mAudioManager != null) {
            if (volume < 0) {
                volume = 0;
            } else if (volume > mMaxVolume) {
                volume = mMaxVolume;
            }
            mCurrentVolume = volume;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
        }
    }

    private void setMute() {
        Log.d("yangding", "setMute");
        //sendKeyCode(KeyEvent.KEYCODE_MUTE);
        doExec(BACK_CMD);
    }

    private static final String BACK_CMD = "input keyevent 91";

    private static void doExec(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            Log.d("yangding", e.getMessage());
            e.printStackTrace();
        }
    }

    private static void sendKeyCode(final int keyCode) {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Instrumentation instrumentation = new Instrumentation();
                    instrumentation.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    Log.d("yangding", e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

}
