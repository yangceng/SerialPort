package com.amyrobotics.serialport.head;

import android.util.Log;

import com.amyrobotics.serialport.Constant;
import com.amyrobotics.serialport.DataUtils;
import com.amyrobotics.serialport.LogUtils;
import com.amyrobotics.serialport.SerialPortDataSetObservable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class HeadReadThread extends Thread implements Constant {
    private static final String TAG = "HeadReadThread";
    private InputStream mInputStream;
    private DataInputStream dataInputStream;
    private OnDataReceivedListener onDataReceivedListener;
    private SerialPortDataSetObservable serialPortDataSetObservable;

    public HeadReadThread(InputStream inputStream, SerialPortDataSetObservable serialPortDataSetObservable) {
        this.mInputStream = inputStream;
        dataInputStream = new DataInputStream(mInputStream);
        this.serialPortDataSetObservable = serialPortDataSetObservable;
    }

    public void setOnDataReceivedListener(OnDataReceivedListener onDataReceivedListener) {
        this.onDataReceivedListener = onDataReceivedListener;
    }

    public HeadReadThread(InputStream inputStream) {

    }

    @Override
    public void run() {
        if (mInputStream == null) {
            return;
        }
        int size = -1;
        int length = 0;
        ArrayList<String> hexList = new ArrayList<>();
        LogUtils.e("开始读线程");

        while (!isInterrupted()) {
            try {
                size = mInputStream.read();
                String hex = String.format("%02x", size).toUpperCase();
                hexList.add(hex);
                if (LAST.equals(hex)) {
                    if (hexList.size() >= 9) {
                        String head1 = hexList.get(0);
                        String head2 = hexList.get(1);
                        String head3 = hexList.get(2);
                        String head4 = hexList.get(3);
                        String lengthHex = hexList.get(4);
                        length = DataUtils.getNumber(lengthHex);
                        if (HEAD_1.equals(head1) && HEAD_2.equals(head2) && HEAD_3.equals(head3) &&
                                HEAD_4.equals(head4) && hexList.size() == length + 9) {
                            Log.i(TAG, "run: " + hexList.toString());
                            if (serialPortDataSetObservable != null) {
                                serialPortDataSetObservable.onDataReceived(hexList);
                            }
                        }
                    }
                    hexList.clear();
                    length = 0;
                    size = -1;
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
