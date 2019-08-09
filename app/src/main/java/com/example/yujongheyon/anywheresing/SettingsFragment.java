package com.example.yujongheyon.anywheresing;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by yujongheyon on 2018-07-21.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
