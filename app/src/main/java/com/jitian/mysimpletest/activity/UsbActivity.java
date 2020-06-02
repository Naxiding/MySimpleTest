package com.jitian.mysimpletest.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jitian.jni.UsbDongleController;
import com.jitian.mysimpletest.utils.LogUtil;
import com.jitian.mysimpletest.R;

/**
 * @author YangDing
 * @date 2020/4/10
 */
public class UsbActivity extends AppCompatActivity implements UsbDongleController.OnChangeCallback {

    private UsbDongleController mUsbDongleController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb);
        mUsbDongleController = new UsbDongleController();
        mUsbDongleController.bindThis(this, this);
    }

    @Override
    public void onStateChanged(int currentState) {
        switch (currentState) {
            case UsbDongleController.STATE_DONGLE_ERROR:
            default:
                break;
            case UsbDongleController.STATE_DONGLE_ADDED:
                mUsbDongleController.sendData();
                break;
            case UsbDongleController.STATE_DONGLE_REMOVED:
                //Todo disable voice service
                break;
        }
    }

    @Override
    public void onMacResponse(String dongleMac) {
        LogUtil.d("dongleMac:" + dongleMac);
        mUsbDongleController.releaseUsbResources();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUsbDongleController.unBindThis();
    }
}
