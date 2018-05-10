package com.amyrobotics.serialporta2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.amyrobotics.serialport.LogUtils;
import com.amyrobotics.serialport.head.ControlHeadChannel;
import com.amyrobotics.serialporta2.job.JobSchedulerManager;

import static com.amyrobotics.serialporta2.job.JobSchedulerManager.getJobSchedulerInstance;

public class ShutDownReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onReceive(Context context, Intent intent) {
        LogUtils.i("ShutDownReceiver", "received robot shutdown broadcast");
        boolean flag = ControlHeadChannel.getInstance().turnHeadReset();
        LogUtils.i("ShutDownReceiver", "send head reset end. result = "+flag);
        JobSchedulerManager.getJobSchedulerInstance(context).stopJobScheduler();
    }
}
