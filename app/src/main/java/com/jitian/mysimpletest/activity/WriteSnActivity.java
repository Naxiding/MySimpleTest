package com.jitian.mysimpletest.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.allwinnertech.dragonsn.jni.ReadPrivateJNI;
import com.jitian.mysimpletest.R;
import com.jitian.mysimpletest.utils.LogUtil;
import com.jitian.mysimpletest.utils.SecureZoneUtil;

/**
 * @author YangDing
 */
public class WriteSnActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mResultView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_sn);
        mResultView = findViewById(R.id.read_result);
        findViewById(R.id.start_read).setOnClickListener(this);
        findViewById(R.id.start_write).setOnClickListener(this);
        LogUtil.d("ReadPrivateJNI:" + ReadPrivateJNI.nativeGetParameter("WMAC"));
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_read) {
            startRead();
        } else if (v.getId() == R.id.start_write) {
            startWrite();
        }
    }

    private void startRead() {
        String result = SecureZoneUtil.getInstance().getDeviceNumber("EEPRPM | TID");
        if (result == null) {
            mResultView.setText("result:null");
        } else {
            mResultView.setText("result:" + result);
        }
    }

    private void startWrite() {
        boolean result = SecureZoneUtil.getInstance().writeTestNumber("456789");
        mResultView.setText("result:" + result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SecureZoneUtil.getInstance().release();
    }
}
