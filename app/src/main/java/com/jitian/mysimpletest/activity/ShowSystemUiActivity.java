package com.jitian.mysimpletest.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jitian.mysimpletest.R;

/**
 * @author YangDing
 * @date 2020/5/19
 */
public class ShowSystemUiActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_system_ui);
        findViewById(R.id.show).setOnClickListener(this);
        findViewById(R.id.hidden).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.show) {

        } else if (v.getId() == R.id.hidden) {

        }
    }
}
