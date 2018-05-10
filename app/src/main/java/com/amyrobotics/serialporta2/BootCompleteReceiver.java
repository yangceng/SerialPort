package com.amyrobotics.serialporta2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.amyrobotics.serialport.LogUtils;
import com.amyrobotics.serialporta2.job.JobSchedulerManager;

public class BootCompleteReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i("SerialPort", "on boot completed. BootCompletedReceiver in");
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(context, SerialPortService.class);
        context.startService(serviceIntent);

        JobSchedulerManager.getJobSchedulerInstance(context).startJobScheduler();
        LogUtils.i("SerialPort", "start SerialPortService");
    }
}
