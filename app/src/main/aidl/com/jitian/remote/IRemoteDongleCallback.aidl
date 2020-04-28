package com.jitian.remote;

interface IRemoteDongleCallback {

    void onDongleAdded(in String dongleMac);

    void onDongleRemoved(in String dongleMac);
}
