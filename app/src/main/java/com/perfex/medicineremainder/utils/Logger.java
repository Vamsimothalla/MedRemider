package com.perfex.medicineremainder.utils;

import android.util.Log;

import androidx.annotation.NonNull;

public class Logger {
    final static Logger INSTANCE = new Logger();
    public static Logger getInstance(){
        return INSTANCE;
    }
    public void debugLog(@NonNull Object o, String message){
        Log.d(o.getClass().getSimpleName(), "debug: "+message);
    }

    public void errorLog(Object o,Exception e,String message){
        Log.e(o.getClass().getSimpleName(), "errorLog: "+message ,e);
    }
}
