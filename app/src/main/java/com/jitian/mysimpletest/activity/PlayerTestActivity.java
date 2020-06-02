package com.jitian.mysimpletest.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jitian.mysimpletest.R;
import com.jitian.mysimpletest.utils.LogUtil;
import com.jitian.mysimpletest.utils.MediaPlayerUtil;

/**
 * @author YangDing
 * @date 2020/5/14
 */
public class PlayerTestActivity extends AppCompatActivity implements View.OnClickListener {

    private static int[] VOICES = new int[]{
            R.raw.charge,
            R.raw.tp001,
            R.raw.tp002,
            R.raw.tp003,
            R.raw.tp004,
            R.raw.tp005,
            R.raw.tp006,
            R.raw.tp007,
            R.raw.tp008,
            R.raw.tp009,
            R.raw.tp010,
            R.raw.tp011,
            R.raw.tp012
    };
    private int mIndex = 0;
    private MediaPlayerUtil mMediaPlayerUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_test);
        findViewById(R.id.playNext).setOnClickListener(this);
        findViewById(R.id.playStop).setOnClickListener(this);
        mMediaPlayerUtil = new MediaPlayerUtil();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.playNext) {
            mMediaPlayerUtil.playRawVoiceByType(VOICES[mIndex % VOICES.length], 0);
            mIndex++;
        } else if (view.getId() == R.id.playStop) {
            mMediaPlayerUtil.stopPlay();
            mIndex = 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("onDestroy");
        mMediaPlayerUtil.release();
    }
}
