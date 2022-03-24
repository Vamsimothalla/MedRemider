package com.perfex.medicineremainder;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;

import dagger.hilt.android.HiltAndroidApp;

public class App extends Application {
    FirebaseApp firebaseApp;
    @Override
    public void onCreate() {
        super.onCreate();
        firebaseApp=FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
    }
}
