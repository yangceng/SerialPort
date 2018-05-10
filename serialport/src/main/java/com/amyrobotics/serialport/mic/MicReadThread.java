package com.amyrobotics.serialport.mic;

import android.text.TextUtils;
import android.util.Log;

import com.amyrobotics.serialport.Constant;
import com.amyrobotics.serialport.DataUtils;
import com.amyrobotics.serialport.LogUtils;
import com.amyrobotics.serialport.SerialPortDataSetObservable;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MicReadThread extends Thread implements Constant {
    private static final String TAG = "HeadReadThread";
    private InputStream mInputStream;
    private BufferedReader bufferedReader;
    private OnDataReceivedListener onDataReceivedListener;
    private SerialPortDataSetObservable serialPortDataSetObservable;

    public MicReadThread(InputStream inputStream, SerialPortDataSetObservable serialPortDataSetObservable) {
        this.mInputStream = inputStream;
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.serialPortDataSetObservable = serialPortDataSetObservable;
    }

    public void setOnDataReceivedListener(OnDataReceivedListener onDataReceivedListener) {
        this.onDataReceivedListener = onDataReceivedListener;
    }

    public MicReadThread(InputStream inputStream) {

    }

    @Override
    public void run() {
        if (mInputStream == null) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        LogUtils.e("开始读线程");

        while (!isInterrupted()) {
            try {
                String s = bufferedReader.readLine().trim();
                if (!TextUtils.isEmpty(s)) {
                    stringBuffer.append(s).append("\n");
                    if (stringBuffer.toString().trim().endsWith(MIC_COM_DATA_END)) {
                        LogUtils.i(TAG, "run: "+stringBuffer.toString().trim());
                        if (serialPortDataSetObservable != null) {
                            serialPortDataSetObservable.notifyChanged(stringBuffer.toString());
                        }
                        stringBuffer.delete(0, stringBuffer.length());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e("结束读进程");
    }

    /**
     * 停止读线程
     */
    public void close() {
        try {
            mInputStream.close();
        } catch (IOException e) {
            LogUtils.e("异常", e);
        } finally {
            interrupt();
        }
    }

    interface OnDataReceivedListener {
        void onDataReceived(ArrayList<String> list, int length);
    }

}
