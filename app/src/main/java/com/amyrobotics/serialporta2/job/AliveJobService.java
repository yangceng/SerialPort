package com.amyrobotics.serialporta2.job;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.amyrobotics.serialport.LogUtils;
import com.amyrobotics.serialporta2.SerialPortService;


@TargetApi(21)
public class AliveJobService extends JobService {
    private static final String SERIALPORT_SERVICE_NAME = "com.amyrobotics.serialporta2.SerialPortService";
    private final static String TAG = "AliveJobService";
    // 告知编译器，这个变量不能被优化
    private volatile static Service mKeepAliveService = null;

    public static boolean isJobServiceAlive() {
        return mKeepAliveService != null;
    }

    private static final int MESSAGE_ID_TASK = 0x01;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // 具体任务逻辑
            startServer();
            // 通知系统任务执行结束
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }
    });

    @Override
    public boolean onStartJob(JobParameters params) {
        mKeepAliveService = this;
        // 返回false，系统假设这个方法返回时任务已经执行完毕；
        // 返回true，系统假定这个任务正要被执行
        Message msg = Message.obtain(mHandler, MESSAGE_ID_TASK, params);
        mHandler.sendMessage(msg);
        return true;
    }

    private void startServer() {
        if (!SystemUtils.isServiceRunning(this,SERIALPORT_SERVICE_NAME )) {
            Intent serviceIntent = new Intent(this, SerialPortService.class);
            startService(serviceIntent);
            LogUtils.i(TAG, "startServer: is no running");
        } else {
            LogUtils.i(TAG, "startServer: isrunning");
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeMessages(MESSAGE_ID_TASK);
        return false;
    }
}
