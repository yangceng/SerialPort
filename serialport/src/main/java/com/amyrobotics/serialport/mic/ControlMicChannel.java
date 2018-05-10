package com.amyrobotics.serialport.mic;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.amyrobotics.serialport.Constant;
import com.amyrobotics.serialport.DataUtils;
import com.amyrobotics.serialport.LogUtils;
import com.amyrobotics.serialport.R;
import com.amyrobotics.serialport.head.SerialPortHeadHelper;

import java.io.IOException;

public class ControlMicChannel implements Constant, SerialPortMicHelper.OnMicComDataChangeListener {
    private static final String TAG = "ControlMicChannel";
    private static ControlMicChannel instance = null;
    private Context context;
    private String appVer;
    private String libVer;

    public static synchronized ControlMicChannel getInstance() {
        if (instance == null) {
            synchronized (ControlMicChannel.class) {
                instance = new ControlMicChannel();
            }
        }
        return instance;
    }

    public boolean connect(Context context) {
        this.context = context;
        if (SerialPortMicHelper.getInstance().isOpen())
            return true;
        try {
            SerialPortMicHelper.getInstance().open();
            SerialPortMicHelper.getInstance().setListener(this);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void disconnect() {
        SerialPortMicHelper.getInstance().close();
    }

    public boolean isConnected() {
        return SerialPortMicHelper.getInstance().isOpen();
    }


    @Override
    public void onWakeup(int angle) {
        if (context != null) {
            Intent intent = new Intent();
            intent.setAction(AMYROBOT_WAKEUP_ACTION);
            intent.putExtra("type", "mic");
            intent.putExtra("angle", angle);
            context.sendOrderedBroadcast(intent, null);
        } else {
            LogUtils.e(TAG, "onWakeup: context is null");
        }
    }

    @Override
    public void onVerRequest(String appver, String libver) {
        LogUtils.i(TAG, "onVerRequest: ");
        appVer = appver;
        libVer = libver;
    }

    public boolean beam(int num) {
        num = num & 0b101;
        if (!isConnected())
            connect(context);
        sendText("BEAM" + num + "\n");
        return true;
    }

    public boolean reset() {//重置
        if (!isConnected())
            connect(context);
        sendText("RESET\n");
        return true;
    }

    public String getVer() {//得到版本信息
        if (!isConnected())
            connect(context);
        // TODO: 2018/5/9 处理方法不好
        for (int i = 0; i < 10; i++) {
            if (TextUtils.isEmpty(appVer)) {
                sendText("VER\n");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return context.getString(R.string.request_version_error);
                }
            } else {
                return appVer;
            }
        }
        return context.getString(R.string.request_version_error);
    }


    private void sendText(String string) {
        SerialPortMicHelper.getInstance().send(string.getBytes());
    }
}
