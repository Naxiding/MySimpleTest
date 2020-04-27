package com.jitian.mysimpletest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.jitian.mysimpletest.R;

import java.io.File;

/**
 * @author YangDing
 * @date 2019/11/18
 */
public class MainActivity extends AppCompatActivity {

    private static final String RECORD_ACTION = "remote_record_action";
    private static final String RECORD_RATE = "record_rate";
    private static final String RECORD_FILE_PATH = "record_file_path";

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && RECORD_ACTION.equals(intent.getAction())) {
                Log.d("yangding", RECORD_RATE + " : " + intent.getIntExtra(RECORD_RATE, -1));
                String filePath = intent.getStringExtra(RECORD_FILE_PATH);
                if (filePath != null) {
                    getPath(filePath);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECORD_ACTION);
        registerReceiver(mBroadcastReceiver, intentFilter);
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord();
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord();
            }
        });
        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("simple_start_service");
                sendBroadcast(intent);
            }
        });
    }

    private void stopRecord() {
        Intent intent = new Intent(AUDIO_RECORD_ACTION);
        intent.putExtra(AUDIO_RECORD_COMMAND, 2);
        sendBroadcast(intent);
    }

    private static final String AUDIO_RECORD_ACTION = "record_action";
    private static final String AUDIO_RECORD_COMMAND = "record_command";

    private void startRecord() {
        Intent intent = new Intent(AUDIO_RECORD_ACTION);
        intent.putExtra(AUDIO_RECORD_COMMAND, 1);
        sendBroadcast(intent);
    }

    private void getPath(String filePath) {
        File file = new File(filePath);
        Log.d("yangding", "" + file.exists() +
                " , " + file.getPath() +
                " , " + file.canRead() +
                " , " + file.canWrite());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
