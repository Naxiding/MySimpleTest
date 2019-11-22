package com.jitian.mysimpletest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.jitian.remote.IRemoteBluetoothCallback;
import com.jitian.remote.IRemoteCallbackService;

import java.util.ArrayList;

/**
 * @author YangDing
 * @date 2019/11/21
 */
public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "LogUtil";
    private static final String TARGET_PACKAGE_NAME = "com.jitian.remotecontrol2";
    private static final String TARGET_SERVICE_NAME = "com.jitian.remotecontrol2.service.RemoteBluetoothService";
    private IRemoteCallbackService mRemoteCallbackService;
    private String mFilePath = null;
    private OnRemoteDataCallback mRemoteDataCallback = new OnRemoteDataCallback();
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            mRemoteCallbackService = IRemoteCallbackService.Stub.asInterface(iBinder);
            if (mRemoteCallbackService != null) {
                try {
                    mRemoteCallbackService.registerCallback(mRemoteDataCallback);
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
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindToService();
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindToService();
            }
        });
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPcm();
            }
        });

    }

    private void playPcm() {
        Log.d(TAG, "mFilePath:" + mFilePath);
        if (mFilePath != null) {
            PcmPlayerHelper.getInstance().play(mFilePath);
        }
    }

    private void bindToService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(TARGET_PACKAGE_NAME, TARGET_SERVICE_NAME));
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindToService() {
        if (mRemoteCallbackService != null) {
            try {
                mRemoteCallbackService.unregisterCallback(mRemoteDataCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(mServiceConnection);
            mRemoteCallbackService = null;
        }
    }

    private ArrayList<byte[]> mAllData = new ArrayList<>();

    class OnRemoteDataCallback extends IRemoteBluetoothCallback.Stub {

        @Override
        public void onStartReceiveData() throws RemoteException {
            Log.d(TAG, "onStartReceiveData");
        }

        @Override
        public void onReceivingData(byte[] bytes) throws RemoteException {
            Log.d(TAG, "onReceivingData:" + bytes.length);
            mAllData.add(bytes);
        }

        @Override
        public void onFinishReceiveData() throws RemoteException {
            Log.d(TAG, "onFinishReceiveData");
            if (mAllData.size() > 0) {
                ThreadUtil.execute(new Runnable() {
                    @Override
                    public void run() {
                        mFilePath = FileUtil.saveDataToPcmFile(mAllData, MainActivity2.this);
                        Log.d(TAG, "save file name:" + mFilePath);
                        mAllData.clear();
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PcmPlayerHelper.getInstance().release();
        unbindToService();
    }
}
