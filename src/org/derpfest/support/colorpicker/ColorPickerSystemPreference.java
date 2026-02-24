/*
 * Copyright (C) 2011 Sergey Margaritov
 * Copyright (C) 2013 Slimroms
 * Copyright (C) 2015 The TeamEos Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.derpfest.support.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import org.derpfest.support.R;
import org.derpfest.support.preferences.SystemSettingsStore;

/**
 * ColorPickerPreference that persists the selected color to {@link android.provider.Settings.System}.
 * For custom gradient colors when gradient is enabled (tiles, brightness slider, volume slider): use
 * {@code android:key="gradient_start_color"} and/or {@code android:key="gradient_end_color"}.
 * Value 0 means use the default theme gradient color for that side.
 * Use {@code app:defaultDisplayColorRes} to show the real theme default in the picker when no color
 * is chosen yet (instead of black).
 */
public class ColorPickerSystemPreference extends ColorPickerPreference {

    private int mDefaultDisplayColorResId;

    public ColorPickerSystemPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPreferenceDataStore(new SystemSettingsStore(context.getContentResolver()));
        initDefaultDisplayColorRes(context, attrs, defStyle, 0);
    }

    public ColorPickerSystemPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPreferenceDataStore(new SystemSettingsStore(context.getContentResolver()));
        initDefaultDisplayColorRes(context, attrs, 0, 0);
    }

    public ColorPickerSystemPreference(Context context) {
        super(context, null);
        setPreferenceDataStore(new SystemSettingsStore(context.getContentResolver()));
        mDefaultDisplayColorResId = 0;
    }

    private void initDefaultDisplayColorRes(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerSystemPreference,
                defStyleAttr, defStyleRes);
        mDefaultDisplayColorResId = a.getResourceId(R.styleable.ColorPickerSystemPreference_defaultDisplayColorRes, 0);
        a.recycle();
    }

    @Override
    protected int getDisplayColor() {
        int persisted = getPersistedInt(0);
        if (persisted != 0) {
            return persisted;
        }
        if (mDefaultDisplayColorResId != 0) {
            try {
                return getContext().getColor(mDefaultDisplayColorResId);
            } catch (Exception ignored) {
                // Resource not found or not a color
            }
        }
        return 0;
    }
}
