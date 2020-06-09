package com.jitian.mysimpletest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jitian.mysimpletest.R;
import com.jitian.mysimpletest.service.RecordPlayService;

/**
 * @author YangDing
 * @date 2020/6/2
 */
public class RecordPlayActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_play);
        findViewById(R.id.start_record_play).setOnClickListener(this);
        findViewById(R.id.stop_record_play).setOnClickListener(this);
        mIntent = new Intent(this, RecordPlayService.class);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_record_play) {
            startService(mIntent);
        } else if (v.getId() == R.id.stop_record_play) {
            stopService(mIntent);
        }
    }

}
