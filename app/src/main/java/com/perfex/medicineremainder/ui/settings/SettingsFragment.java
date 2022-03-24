package com.perfex.medicineremainder.ui.settings;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.perfex.medicineremainder.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final int REQUEST_CODE_ALERT_RINGTONE = 222;
    private static final String KEY_RINGTONE_PREFERENCE = "ringtone" ;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }


}