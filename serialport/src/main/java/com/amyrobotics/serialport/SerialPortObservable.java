package com.amyrobotics.serialport;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/9.
 */

public abstract class SerialPortObservable<T> {

    protected final ArrayList<T> mObservers = new ArrayList<T>();


    public void registerObserver(T observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The observer is null.");
        }
        synchronized(mObservers) {
            if (mObservers.contains(observer)) {
                throw new IllegalStateException("Observer " + observer + " is already registered.");
            }
            mObservers.add(observer);
        }
    }


    public void unregisterObserver(T observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The observer is null.");
        }
        synchronized(mObservers) {
            int index = mObservers.indexOf(observer);
            if (index == -1) {
                throw new IllegalStateException("Observer " + observer + " was not registered.");
            }
            mObservers.remove(index);
        }
    }

    public boolean observerIsExist(T observer){
        boolean result = false;
        int index = mObservers.indexOf(observer);
        if (index != -1){
            result = true;
        }else {
            result = false;
        }
        return result;
    }

    public void unregisterAll() {
        synchronized(mObservers) {
            mObservers.clear();
        }
    }
}
