package com.amyrobotics.serialport;

import java.util.ArrayList; /**
 * Created by Administrator on 2017/10/9.
 */

public interface SerialPortDataSetObserver {
     void notifyChange(String comBean);

     void onDataReceived(ArrayList<String> hexList);
}
