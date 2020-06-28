package com.jitian.mysimpletest.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jitian.mysimpletest.R;
import com.jitian.mysimpletest.service.RecordPlayService;

/**
 * @author YangDing
 * @date 2020/6/2
 */
public class RecordPlayActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 1;
    private Intent mIntent;
    private int permissionCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_play);
        findViewById(R.id.start_record_play).setOnClickListener(this);
        findViewById(R.id.stop_record_play).setOnClickListener(this);
        mIntent = new Intent(this, RecordPlayService.class);
        permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_record_play) {
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                startService(mIntent);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
            }
        } else if (v.getId() == R.id.stop_record_play) {
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                stopService(mIntent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                startService(mIntent);
            }
        }
    }
}
