package com.jitian.mysimpletest.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaRecorder;

/**
 * @author YangDing
 * @date 2019/11/8
 */
public class PcmConfig {

    public static final int RATE = 48000;
    public static final int CHANNEL_MODE_PLAY = AudioFormat.CHANNEL_OUT_MONO;
    public static final int CHANNEL_MODE_RECORD = AudioFormat.CHANNEL_IN_MONO;
    public static final int AUDIO_STREAM = AudioManager.STREAM_MUSIC;
    public static final int PCM_BIT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int AUDIO_RES = MediaRecorder.AudioSource.MIC;
    public static final int MODE_STREAM_PLAY = AudioTrack.MODE_STREAM;
}
