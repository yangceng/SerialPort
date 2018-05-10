package com.amyrobotics.serialport.mic;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.serialport.SerialPort;
import android.util.Log;

import com.amyrobotics.serialport.Constant;
import com.amyrobotics.serialport.DataUtils;
import com.amyrobotics.serialport.LogUtils;
import com.amyrobotics.serialport.SerialPortDataSetObservable;
import com.amyrobotics.serialport.SerialPortDataSetObserver;
import com.amyrobotics.serialport.head.ControlHeadChannel;
import com.amyrobotics.serialport.head.HeadReadThread;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


public class SerialPortMicHelper implements Constant, SerialPortDataSetObserver {
    private static final String TAG = "SerialPortHeadHelper";
    private static volatile SerialPortMicHelper single;
    private SerialPort serialPort;
    private OutputStream outputStream;

    private MicReadThread mReadThread;

    public void setListener(OnMicComDataChangeListener listener) {
        this.listener = listener;
    }

    private OnMicComDataChangeListener listener;
    private Handler writeHandler;
    private HandlerThread mWriteThread;
    private SerialPortDataSetObservable serialPortDataSetObservable;

    public SerialPortDataSetObservable getSerialPortDataSetObservable() {
        return serialPortDataSetObservable;
    }

    public static SerialPortMicHelper getInstance() {
        if (null == single) {
            synchronized (SerialPortMicHelper.class) {
                if (null == single) {
                    single = new SerialPortMicHelper();
                }
            }
        }
        return single;
    }

    private SerialPortMicHelper() {

    }


    /**
     * 打开串口
     */
    public void open() throws IOException {
        if (serialPort == null) {
            serialPort = new SerialPort(MIC_DEVICE_PATH, MIC_DEVICE_BAUDRATE);
            serialPortDataSetObservable = new SerialPortDataSetObservable();
            mReadThread = new MicReadThread(serialPort.getInputStream(), serialPortDataSetObservable);
            mReadThread.start();
            if (!serialPortDataSetObservable.observerIsExist(this)) {
                serialPortDataSetObservable.registerObserver(this);
            }
            outputStream = serialPort.getOutputStream();
            mWriteThread = new HandlerThread("write-mic-thread");
            mWriteThread.start();
            writeHandler = new Handler(mWriteThread.getLooper());
        }
    }

    public boolean isOpen() {
        return serialPort != null;
    }

    /**
     * 关闭串口
     */
    public void close() {
        if (mReadThread != null) {
            mReadThread.close();
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mWriteThread != null) {
            mWriteThread.quit();
        }

        if (serialPort != null) {
            if (!serialPortDataSetObservable.observerIsExist(this)) {
                serialPortDataSetObservable.unregisterObserver(this);
            }
            serialPort.close();
            serialPort = null;
        }
    }

    /**
     * 发送数据
     *
     * @param datas
     * @return
     */
    public void send(final byte[] datas) {
        if (serialPort != null)
            writeHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream.write(datas);
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    @Override
    public void notifyChange(String msg) {
        if (msg.contains("WAKE UP!angle:")) {
            int angle = 0;
            try {
                int index_start = msg.indexOf(MIC_WAKEUP_ANGLE);
                int index_end = msg.indexOf(MIC_WAKEUP_ANGLE_END);
                if (index_end == -1) {
                    index_end = msg.indexOf("#");
                }
                angle = Integer.parseInt(msg.substring(index_start + MIC_WAKEUP_ANGLE.length(), index_end).trim());
                if (listener != null)
                    listener.onWakeup(angle);
                LogUtils.i(TAG, "notifyChange: " + angle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            int appver_start = msg.indexOf(MIC_APPVER_START);
            int libver_start = msg.indexOf(MIC_LIBVER_START);
            int ver_end = msg.indexOf(MIC_VER_END);
            String appver = msg.substring(appver_start + MIC_APPVER_START.length(), libver_start).trim().replace("#", "");
            String libver = msg.substring(libver_start + MIC_LIBVER_START.length(), ver_end).trim().replace("#", "");
            if (listener != null)
                listener.onVerRequest(appver, libver);
            LogUtils.i(TAG, "appver: " + appver + "   libver:" + libver);
        }
    }

    @Override
    public void onDataReceived(ArrayList<String> hexList) {

    }

    interface OnMicComDataChangeListener {
        void onWakeup(int angle);

        void onVerRequest(String appver, String libver);
    }
}
