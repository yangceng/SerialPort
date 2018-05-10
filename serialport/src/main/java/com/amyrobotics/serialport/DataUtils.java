package com.amyrobotics.serialport;

import com.amyrobotics.serialport.head.ControlHeadChannel;
import com.amyrobotics.serialport.head.SerialPortHeadHelper;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/23 0023.
 */

public class DataUtils implements Constant{
    private static final String TAG = "DataUtils";
    /***
     *
     * @param dataLength  发送数据命令cmd的长度
     * @param style        发送的类型
     * @param cmd          发送的数据命令的cmd
     * @param datas        数据命令
     * @param
     * @return
     */
    public static byte[] sendCmd(byte dataLength, byte style, byte cmd, byte[] datas) {
        byte[] cmdData = null;
        byte xor = dataLength;
        if (datas == null) {
            cmdData = new byte[9];
        } else {
            cmdData = new byte[datas.length + 9];
        }
        //把数组head拷贝到cmdData这个数组中
        System.arraycopy(HEAD, 0, cmdData, 0, HEAD.length);
        for (int i = 0; i < cmdData.length; i++) {
            if (i == 4) {
                cmdData[i] = dataLength;
            } else if (i == 5) {
                cmdData[i] = style;
            } else if (i == 6) {
                cmdData[i] = cmd;
            }
        }
        if (datas != null) {
            System.arraycopy(datas, 0, cmdData, 7, datas.length);
        }
        for (int i = 5; i < cmdData.length - 2; i++) {
            //进行
            if (i >= 5) {
                xor ^= cmdData[i];
            }
        }
        cmdData[cmdData.length - 2] = xor;
        cmdData[cmdData.length - 1] = (byte) 0xDD;

        return cmdData;
    }

    /***
     * 灯环的发送指令的方法
     * @param datas   数据命令
     * @param
     * @return
     */
    public static void sendLampAllCmd(byte[] datas) throws InterruptedException {
        byte[] cmdData = null;
        cmdData = new byte[datas.length + 9];
        //把数组head拷贝到cmdData这个数组中
        System.arraycopy(HEAD, 0, cmdData, 0, HEAD.length);
        cmdData[4] = (byte) 0x24;
        cmdData[5] = (byte) 0x06;
        cmdData[6] = (byte) 0x01;
        if (datas != null) {
            System.arraycopy(datas, 0, cmdData, 7, datas.length);
        }
        byte xor = (byte) 0x24;
        for (int i = 5; i < cmdData.length - 2; i++) {
            //进行
            if (i >= 5) {
                xor ^= cmdData[i];
            }
        }
        cmdData[cmdData.length - 2] = xor;
        cmdData[cmdData.length - 1] = (byte) 0xDD;
        LogUtils.i(TAG, "sendLampCmd: "+byte2hex(cmdData));
        SerialPortHeadHelper.getInstance().send(cmdData);
        Thread.sleep(20);

        cmdData[6] = (byte) 0x02;
        xor = (byte) 0x24;
        for (int i = 5; i < cmdData.length - 2; i++) {
            //进行
            if (i >= 5) {
                xor ^= cmdData[i];
            }
        }
        cmdData[cmdData.length - 2] = xor;
        cmdData[cmdData.length - 1] = (byte) 0xDD;
        LogUtils.i(TAG, "sendLampCmd: "+byte2hex(cmdData));
        SerialPortHeadHelper.getInstance().send(cmdData);
        Thread.sleep(20);

        cmdData[6] = (byte) 0x03;
        xor = (byte) 0x24;
        for (int i = 5; i < cmdData.length - 2; i++) {
            //进行
            if (i >= 5) {
                xor ^= cmdData[i];
            }
        }
        cmdData[cmdData.length - 2] = xor;
        cmdData[cmdData.length - 1] = (byte) 0xDD;
        LogUtils.i(TAG, "sendLampCmd: "+byte2hex(cmdData));
        SerialPortHeadHelper.getInstance().send(cmdData);
        Thread.sleep(20);


        cmdData[6] = (byte) 0x05;
        xor = (byte) 0x24;
        for (int i = 5; i < cmdData.length - 2; i++) {
            //进行
            if (i >= 5) {
                xor ^= cmdData[i];
            }
        }
        cmdData[cmdData.length - 2] = xor;
        cmdData[cmdData.length - 1] = (byte) 0xDD;
        LogUtils.i(TAG, "sendLampCmd: "+byte2hex(cmdData));
        SerialPortHeadHelper.getInstance().send(cmdData);
    }


    /**
     * 解析串口发送过来的命令的类型
     *
     * @param
     * @return
     */

    public static void getDataStyle(ArrayList<String> hexList) {
        String Class = hexList.get(5);
        String cmd = hexList.get(6);
        if ("03".equals(Class)) {
            switch (cmd) {
                // 01 04 分别是下1 下2触摸板
                case "01":
                    ControlHeadChannel.getInstance().setStyle(100);
                    ControlHeadChannel.getInstance().setTurnStyle("07");
                    break;
                case "04":
                    ControlHeadChannel.getInstance().setStyle(100);
                    ControlHeadChannel.getInstance().setTurnStyle("07");
                    break;
                //02是右触摸板
                case "02":
                    ControlHeadChannel.getInstance().setStyle(-150);
                    ControlHeadChannel.getInstance().setTurnStyle("06");
                    break;
                //08左触摸板
                case "08":
                    ControlHeadChannel.getInstance().setStyle(150);
                    ControlHeadChannel.getInstance().setTurnStyle("06");
                    break;
                //03上触摸板
                case "03":
                    ControlHeadChannel.getInstance().setStyle(-100);
                    ControlHeadChannel.getInstance().setTurnStyle("07");
                    break;

                //05是下1和下2触摸板
                case "05":
                    ControlHeadChannel.getInstance().setStyle(100);
                    ControlHeadChannel.getInstance().setTurnStyle("07");
                    break;
                //左右一起
                case "0A":
                    return;
                //后
                case "0C":
                    ControlHeadChannel.getInstance().setTurnStyle("0C");
                    return;
                //上下一起
                case "0F":
                    return;
            }
        }
        SerialPortHeadHelper.getInstance().send(REPORT_MASHINE_LOC);
    }

    /**
     * 字节数组转换为十六进制字符串
     *
     * @param b byte[] 需要转换的字节数组
     * @return String 十六进制字符串
     */
    public static final String byte2hex(byte b[]) {
        if (b == null) {
            throw new IllegalArgumentException("Argument b ( byte array ) is null! ");
        }
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp + " ";
            } else {
                hs = hs + stmp + " ";
            }
        }
        return hs.toUpperCase();
    }

    /***
     * 把十六进制转为十进制数
     * @param hex 如:"FFFFF380"
     * @return
     */
    public static int getNumber(String hex) {
        if (hex == null) {
            return 0;
        }
        BigInteger bi = new BigInteger(hex, 16);
        int num = bi.intValue();
        return num;
    }

    /***
     * 把收到的数据中是为数据部分的解析
     * @return
     */
    public static String[] getHexData(ArrayList<String> hexList) {
        if (hexList == null) {
            throw new IllegalArgumentException("Argument hexData ( byte array ) is null! ");
        }
        String temp = "";
        if (hexList.size() > 9) {
            for (int i = 7; i < hexList.size() - 2; i++) {
                temp += hexList.get(i);
            }
        }
        int length = (hexList.size() - 9) / 4;
        String[] result = new String[length];
        if (temp.length() == 8) {
            result[0] = DataUtils.getNormalDataHex(temp);
        } else if (temp.length() > 8 && temp.length() <= 16) {
            result[0] = DataUtils.getNormalDataHex(temp.substring(0, 8));
            result[1] = DataUtils.getNormalDataHex(temp.substring(8, temp.length()));
        }
        return result;
    }

    /***
     * 把收到的数据转为正常的排序的十六进制数
     * @param hex
     * @return
     */
    public static String getNormalDataHex(String hex) {
        String stmp = "";
        String result = "";
        int length = hex.length() / 2;
        char[] hexChars = hex.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        byte[] data = new byte[d.length];
        //转为要发送的数据顺序
        data[3] = d[0];
        data[2] = d[1];
        data[1] = d[2];
        data[0] = d[3];
        for (int n = 0; n < data.length; n++) {
            stmp = Integer.toHexString(data[n] & 0xff);
            if (stmp.length() == 1) {
                result = result + "0" + stmp;
            } else {
                result = result + stmp;
            }
        }
        return result.toUpperCase();
    }

    /**
     * 把十进制转为十六进制且转为协议的发送数据
     *
     * @param
     * @return
     */
    public static byte[] toHex(int num) {
        //把十进制数转为十六进制字符串
        String temp = String.format("%08x", num).toUpperCase();
        int length = temp.length() / 2;
        char[] hexChars = temp.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        byte[] data = new byte[d.length];
        //转为要发送的数据顺序
        data[0] = d[3];
        data[1] = d[2];
        data[2] = d[1];
        data[3] = d[0];
        return data;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /***
     * 获取手触摸头部的位置
     * @param cmd
     * @return
     */
    public static String getHandTouchDirection(String cmd) {
        String result = "";
        if (cmd == null) {
            return null;
        }
        String[] cmds = cmd.split(" ");
        result = cmds[6];
        return result;
    }


}
