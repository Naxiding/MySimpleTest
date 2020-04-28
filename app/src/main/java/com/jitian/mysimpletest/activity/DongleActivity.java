package com.jitian.mysimpletest.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jitian.mysimpletest.LogUtil;
import com.jitian.mysimpletest.R;
import com.jitian.remote.IRemoteDongleCallback;
import com.jitian.remote.IRemoteDongleCallbackService;

/**
 * @author YangDing
 * @date 2019/11/21
 */
public class DongleActivity extends AppCompatActivity {

    private static final String TAG = "LogUtil";
    private static final String TARGET_PACKAGE_NAME = "com.jitian.remotecontrol2";
    private static final String TARGET_DONGLE_SERVICE_NAME = "com.jitian.remotecontrol2.service.RemoteDongleListenerService";
    private IRemoteDongleCallbackService mRemoteDongleCallbackService;
    private OnRemoteDongleCallback mRemoteDongleCallback = new OnRemoteDongleCallback();
    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "DeathRecipient binderDied");
            if (mRemoteDongleCallbackService == null) {
                return;
            }
            mRemoteDongleCallbackService.asBinder().unlinkToDeath(deathRecipient, 0);
            mRemoteDongleCallbackService = null;
            //rebind remote service
            bindToService();
        }
    };
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            mRemoteDongleCallbackService = IRemoteDongleCallbackService.Stub.asInterface(iBinder);
            if (mRemoteDongleCallbackService != null) {
                try {
                    mRemoteDongleCallbackService.asBinder().linkToDeath(deathRecipient, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    mRemoteDongleCallbackService.registerCallback(mRemoteDongleCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bindToService();
    }

    private void bindToService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(TARGET_PACKAGE_NAME, TARGET_DONGLE_SERVICE_NAME));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindToService() {
        if (mRemoteDongleCallbackService != null) {
            try {
                mRemoteDongleCallbackService.unregisterCallback(mRemoteDongleCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(mServiceConnection);
            mRemoteDongleCallbackService = null;
        }
    }

    class OnRemoteDongleCallback extends IRemoteDongleCallback.Stub {

        @Override
        public void onDongleAdded(String dongleMac) throws RemoteException {
            LogUtil.d("DongleActivity --> onDongleAdded dongleMac:" + dongleMac);
        }

        @Override
        public void onDongleRemoved(String dongleMac) throws RemoteException {
            LogUtil.d("DongleActivity --> onDongleRemoved dongleMac:" + dongleMac);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindToService();
    }
}
