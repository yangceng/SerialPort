package com.amyrobotics.serialport;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/9.
 */

public class SerialPortDataSetObservable extends SerialPortObservable<SerialPortDataSetObserver> {
    public void notifyChanged(String comBean) {
        synchronized(mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).notifyChange(comBean);
            }
        }
    }

    public void onDataReceived(ArrayList<String> hexList) {
        synchronized(mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onDataReceived(hexList);
            }
        }
    }

}
