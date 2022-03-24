package com.perfex.medicineremainder.ui.settings;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class Preferences {

    private static final String NOTIFICATION = "notification";
    private static final String PRIORITY_NOTIFICATION = "priority_notification";
    SharedPreferences sharedPref ;

    private static Preferences INSTANCE;



    private Preferences(Context context){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public static Preferences getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new Preferences(context);
        }
        return INSTANCE;
    }
    public boolean isNotificationEnabled(){
        return sharedPref.getBoolean(NOTIFICATION,true);
    }
    public boolean isPriorityNotificationEnabled(){
        return sharedPref.getBoolean(PRIORITY_NOTIFICATION,true);
    }

    public boolean isEnabledAlarm(){
        return sharedPref.getBoolean("alarm",true);
    }
}
