package com.amyrobotics.serialport.head;


import com.amyrobotics.serialport.Constant;
import com.amyrobotics.serialport.DataUtils;

import java.io.IOException;

public class ControlHeadChannel implements Constant {
    private static final String TAG = "ControlHeadChannel";

    private static ControlHeadChannel instance = null;
    private byte[] sendedcmd = null;
    private int style = 0;
    private String turnStyle = null;

    public String getTurnStyle() {
        return turnStyle;
    }

    public void setTurnStyle(String turnStyle) {
        this.turnStyle = turnStyle;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }


    public static synchronized ControlHeadChannel getInstance() {
        if (instance == null) {
            synchronized (ControlHeadChannel.class) {
                instance = new ControlHeadChannel();
            }
        }
        return instance;
    }

    public boolean connect() {
        if (SerialPortHeadHelper.getInstance().isOpen())
            return true;
        try {
            SerialPortHeadHelper.getInstance().open();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void disconnect() {
        SerialPortHeadHelper.getInstance().close();
    }

    public boolean isConnected() {
        return SerialPortHeadHelper.getInstance().isOpen();
    }

    public boolean turnHeadRight() {
        return turnHead((byte) 0x06, 1000);
    }

    public boolean turnHeadLeft() {
        return turnHead((byte) 0x06, -1000);
    }

    public boolean turnHeadUp() {
        return turnHead((byte) 0x07, 1000);
    }

    public boolean turnHeadDown() {
        return turnHead((byte) 0x07, -1000);
    }

    /***
     * 上下转动cmdStyle为0x07 左右转动为0x06
     * @param cmdStyle
     * @param angle
     * @return
     */
    public boolean turnHead(byte cmdStyle, int angle) { //上下方向，正数向上转动，负数向下转动，零位在最上位置	有效位置0 至 -3400
        if (!isConnected())
            connect();
        sendedcmd = DataUtils.sendCmd((byte) 0x04, (byte) 0x02, cmdStyle, DataUtils.toHex(angle));
        SerialPortHeadHelper.getInstance().send(sendedcmd);
        return true;
    }

    public boolean warning() {
        if (!isConnected())
            connect();
        try {
            DataUtils.sendLampAllCmd(RED);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean normal() {
        if (!isConnected())
            connect();
        try {
            DataUtils.sendLampAllCmd(BLUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean talking() {
        if (!isConnected())
            connect();
        try {
            DataUtils.sendLampAllCmd(GREEN);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public boolean thinking() {
        if (!isConnected())
            connect();
        sendedcmd = new byte[]{(byte) 0x26, (byte) 't', (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1C};
        SerialPortHeadHelper.getInstance().send(sendedcmd);
        return true;
    }

    public boolean listening() {
        if (!isConnected())
            connect();
        try {
            DataUtils.sendLampAllCmd(GREEN);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean lowBattery() {
        if (!isConnected())
            connect();
        sendedcmd = new byte[]{(byte) 0x26, (byte) 'B', (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1C};
        SerialPortHeadHelper.getInstance().send(sendedcmd);
        return true;
    }

    public boolean singing() {
        if (!isConnected())
            connect();
        sendedcmd = new byte[]{(byte) 0x26, (byte) 'R', (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1C};
        SerialPortHeadHelper.getInstance().send(sendedcmd);
        return true;
    }

    public boolean turnHeadReset() {
        if (!isConnected())
            connect();
        SerialPortHeadHelper.getInstance().send(LR_RESET);
        SerialPortHeadHelper.getInstance().send(UD_RESET);
        return true;
    }

    public boolean openMachine() {
        if (!isConnected())
            connect();
        SerialPortHeadHelper.getInstance().send(OPEN_MASHINE);
        return true;
    }


    public void getHeadVer() {
        if (!isConnected())
            connect();
        sendedcmd = DataUtils.sendCmd((byte) 0x00, (byte) 0x01, (byte) 0xAA, null);
        SerialPortHeadHelper.getInstance().send(sendedcmd);
    }
}
