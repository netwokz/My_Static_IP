package com.netwokz.mystaticip;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_prefrences, rootKey);

//        ListPreference mThemeList = findPreference("theme");
//        assert mThemeList != null;
//        int nightModeFlags = AppCompatDelegate.getDefaultNightMode();
//        switch (nightModeFlags) {
//            case AppCompatDelegate.MODE_NIGHT_YES:
//                mThemeList.setValue("MODE_NIGHT_YES");
//                break;
//            case AppCompatDelegate.MODE_NIGHT_NO:
//                mThemeList.setValue("MODE_NIGHT_NO");
//                break;
//            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
//                mThemeList.setValue("MODE_NIGHT_FOLLOW_SYSTEM");
//                break;
//        }
    }
}