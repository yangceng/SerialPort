package com.amyrobotics.serialporta2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.amyrobotics.serialport.LogUtils;
import com.amyrobotics.serialport.head.ControlHeadChannel;
import com.amyrobotics.serialport.mic.ControlMicChannel;

public class SerialPortService extends Service {
    private static final String TAG = "SerialPortService";
    public SerialPortService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, "onCreate: ");
        ControlHeadChannel.getInstance().connect();
        ControlMicChannel.getInstance().connect(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        LogUtils.i(TAG, "onStartCommand: ");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "onDestroy: ");
        ControlHeadChannel.getInstance().disconnect();
        ControlMicChannel.getInstance().disconnect();

        Intent localIntent = new Intent();
        localIntent.setClass(this, SerialPortService.class);  //销毁时重新启动Service
        this.startService(localIntent);
    }

}
