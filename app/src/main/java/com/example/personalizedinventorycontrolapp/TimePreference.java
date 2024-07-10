package com.example.personalizedinventorycontrolapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;

public class TimePreference extends DialogPreference {
    private int mTime;
    private int mDialogLayoutResId = R.layout.pref_dialog_time_picker;


    public TimePreference(Context context) {
        this(context, null);
    }
    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public TimePreference(Context context, AttributeSet attrs,
                          int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }
    public TimePreference(Context context, AttributeSet attrs,
                          int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public int getTime() {
        return mTime;
    }
    public void setTime(int time) {
        mTime = time;
        // Save to Shared Preferences
        persistInt(time);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        // Default value from attribute. Fallback value is set to 0.
        return a.getInt(index, 0);
    }
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,
                                     Object defaultValue) {
        // Read the value. Use the default value if it is not possible.
        setTime(restorePersistedValue ?
                getPersistedInt(mTime) : (int) defaultValue);
    }

    @Override
    public int getDialogLayoutResource() {
        return mDialogLayoutResId;
    }

    @Nullable
    @Override
    public CharSequence getSummary() {
        return super.getSummary();
    }

    @Override
    public void setSummary(@Nullable CharSequence summary) {
        super.setSummary(summary);
    }
}
