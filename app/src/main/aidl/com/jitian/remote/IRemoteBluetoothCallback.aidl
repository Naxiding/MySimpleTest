package com.jitian.remote;

interface IRemoteBluetoothCallback {

    void onStartReceiveData();

    void onReceivingData(in byte[] bytes);

    void onFinishReceiveData();
}
