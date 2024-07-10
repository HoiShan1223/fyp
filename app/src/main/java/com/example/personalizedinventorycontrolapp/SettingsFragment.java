package com.example.personalizedinventorycontrolapp;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{
    ListPreference NotificationDay;
    TimePreference timePreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);
        NotificationDay = findPreference("NotificationDay");
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getPreferenceScreen().getSharedPreferences()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getPreferenceScreen().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ListPreference listPreference = findPreference("NotificationDay");
        assert listPreference != null;
        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            listPreference.setSummaryProvider((Preference.SummaryProvider<ListPreference>) preference1 -> {
                String text = (String) preference1.getEntry();
                if (TextUtils.isEmpty(text)){
                    return "Not set";
                }
                return text;
            });
            return true;
        });
        timePreference = findPreference("NotificationTime");
        assert timePreference != null;
        int time = timePreference.getTime();
        int hours = time / 60;
        int minutes = time % 60;
        String timeString;
        if(hours< 10){
            String hourString = "0" + hours;
            String mintuesString;
            if(minutes == 0){
                mintuesString = "0" + minutes;
                timeString = hourString + ":" + mintuesString;
            }else{
                timeString = hourString + ":" + minutes;
            }
        }else{
            String mintuesString;
            if(minutes == 0){
                mintuesString = "0" + minutes;
                timeString = hours + ":" + mintuesString;
            }else{
                timeString = hours + ":" + minutes;
            }
        }
        timePreference.setSummary(timeString);
        timePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            timePreference.setSummaryProvider((Preference.SummaryProvider<TimePreference>) preference12 -> {
                int time1 = preference12.getTime();
                int hours1 = time1 / 60;
                int minutes1 = time1 % 60;
                String timeString1;
                if(hours1< 10){
                    String hourString = "0" + hours1;
                    timeString1 = hourString + ":" + minutes1;
                }else{
                    timeString1 = hours1 + ":" + minutes1;
                }
                if (TextUtils.isEmpty(timeString1)){
                    return "Not set";
                }
                return timeString1;
            });
            return true;
        });
        return  super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        // Try if the preference is one of our custom Preferences
        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference) {
            // Create a new instance of TimePreferenceDialogFragment with the key of the related
            // Preference
            dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey());
        }

        // If it was one of our custom Preferences, show its dialog
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(),
                    "android.support.v7.preference" +
                            ".PreferenceFragment.DIALOG");
        }
        // Could not be handled here. Try with the super method.
        else {
            super.onDisplayPreferenceDialog(preference);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "notificationSwitch": {
                boolean newValue = sharedPreferences.getBoolean(key, false);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(key, newValue);
                editor.apply();
                break;
            }
            case "NotificationDay": {
                String newValue = sharedPreferences.getString(key, "");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(key, newValue);
                editor.apply();
                break;
            }
            case "NotificationTime": {
                int newValue = sharedPreferences.getInt(key, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(key, newValue);
                editor.apply();
                break;
            }
        }
    }
}