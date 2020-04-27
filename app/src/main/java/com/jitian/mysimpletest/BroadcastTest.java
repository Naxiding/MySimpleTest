package com.jitian.mysimpletest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author YangDing
 * @date 2020/4/26
 */
public class BroadcastTest extends BroadcastReceiver {

    private static final String TV_PACKAGE = "com.ktcp.tvvideo";
    private static final String TV_SERVICE = "com.tencent.qqlivetv.tvplayer.VoiceControl";
    private static final String EXTRA_COMMAND = "_command";
    private static final String EXTRA_ACTION = "_action";

    private static final String EXTRA_COMMAND_VALUE = "0_episode";
    private static final String EXTRA_ACTION_VALUE_NEXT = "NEXT";
    private static final String EXTRA_ACTION_VALUE_PREV = "PREV";


    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d("intent:" + intent.getAction());
        Intent serviceIntent = new Intent();
        serviceIntent.setAction("com.iflytek.xiri2.scenes.EXECUTE");
        serviceIntent.setFlags(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
        serviceIntent.setPackage(TV_PACKAGE);
        serviceIntent.putExtra(EXTRA_COMMAND, EXTRA_COMMAND_VALUE);
        serviceIntent.putExtra("_scene","com.tencent.qqlivetv.tvplayer.VoiceControl");
        serviceIntent.putExtra("_token","2");
        serviceIntent.putExtra("_objhash","1115338496");
        if (EXTRA_ACTION_VALUE_NEXT.equals(intent.getAction())) {
            serviceIntent.putExtra(EXTRA_ACTION, EXTRA_ACTION_VALUE_NEXT);
        } else {
            serviceIntent.putExtra(EXTRA_ACTION, EXTRA_ACTION_VALUE_PREV);
        }
        context.sendBroadcast(serviceIntent);
    }

}
