package com.amyrobotics.serialport.head;

import android.os.Handler;
import android.os.HandlerThread;
import android.serialport.SerialPort;
import android.util.Log;

import com.amyrobotics.serialport.Constant;
import com.amyrobotics.serialport.DataUtils;
import com.amyrobotics.serialport.LogUtils;
import com.amyrobotics.serialport.SerialPortDataSetObservable;
import com.amyrobotics.serialport.SerialPortDataSetObserver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


public class SerialPortHeadHelper implements Constant, SerialPortDataSetObserver {
    private static final String TAG = "SerialPortHeadHelper";
    private static volatile SerialPortHeadHelper single;
    private SerialPort serialPort;
    private OutputStream outputStream;

    private HeadReadThread mReadThread;
    private Handler writeHandler;
    private HandlerThread mWriteThread;
    private SerialPortDataSetObservable serialPortDataSetObservable;

    public SerialPortDataSetObservable getSerialPortDataSetObservable() {
        return serialPortDataSetObservable;
    }

    public static SerialPortHeadHelper getInstance() {
        if (null == single) {
            synchronized (SerialPortHeadHelper.class) {
                if (null == single) {
                    single = new SerialPortHeadHelper();
                }
            }
        }
        return single;
    }

    private SerialPortHeadHelper() {

    }


    /**
     * 打开串口
     */
    public void open() throws IOException {
        if (serialPort == null) {
            serialPort = new SerialPort(HEAD_DEVICE_PATH, HEAD_DEVICE_BAUDRATE);
            serialPortDataSetObservable = new SerialPortDataSetObservable();
            mReadThread = new HeadReadThread(serialPort.getInputStream(), serialPortDataSetObservable);
            mReadThread.start();
            if (!serialPortDataSetObservable.observerIsExist(this)) {
                serialPortDataSetObservable.registerObserver(this);
            }
            outputStream = serialPort.getOutputStream();
            mWriteThread = new HandlerThread("write-head-thread");
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
    public void notifyChange(String comBean) {

    }

    @Override
    public void onDataReceived(ArrayList<String> hexList) {
        if (hexList == null || hexList.size() == 0) return;
        LogUtils.i(TAG, "onDataReceived: " + hexList.toString());
        if (hexList.size() <= 9) {
            DataUtils.getDataStyle(hexList);
        } else {
            String[] hexData = DataUtils.getHexData(hexList);
            int leftAndRight = DataUtils.getNumber(hexData[1]);
            int upAndDown = DataUtils.getNumber(hexData[0]);
            int turnLRAngle = leftAndRight + ControlHeadChannel.getInstance().getStyle();
            int turnUDAngle = upAndDown + ControlHeadChannel.getInstance().getStyle();
            //限位
//            if (turnLRAngle < -1100 /*|| turnUDAngle < -1100 || turnUDAngle > 2000 || turnLRAngle > 1100*/) {
//                turnLRAngle = -1100;
////                return;
//            }
//            if (turnUDAngle < -1100) {
//                turnUDAngle = -1100;
//            }
//            if (turnUDAngle > 2000) {
//                turnUDAngle = 2000;
//            }
//            if (turnLRAngle > 1100) {
//                turnLRAngle = 1100;
//            }

            Log.d("ggggggggleftAndRight", "" + leftAndRight + " ------" + "" + turnLRAngle);
            Log.d("ggggggggupAndDown", "" + upAndDown + " ------" + "" + turnUDAngle);
            if ("06".equals(ControlHeadChannel.getInstance().getTurnStyle())) {
                if (turnLRAngle < -1100 || turnLRAngle > 1100) return;
                ControlHeadChannel.getInstance().turnHead((byte) 0x06, turnLRAngle);
            } else if ("07".equals(ControlHeadChannel.getInstance().getTurnStyle())) {
                if (turnUDAngle < -1100 || turnUDAngle > 2000) return;
                ControlHeadChannel.getInstance().turnHead((byte) 0x07, turnUDAngle);
            } else if ("0C".equals(ControlHeadChannel.getInstance().getTurnStyle())) {
                ControlHeadChannel.getInstance().turnHeadReset();
            }
        }

    }
}
