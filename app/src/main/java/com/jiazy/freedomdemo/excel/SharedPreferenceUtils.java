package com.jiazy.freedomdemo.excel;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtils {
    private static final String PREFERENCES_FILE_NAME = "share_preference";
    private static final String KEY = "currentIndex";

    public static int getCurrentIndex(Context context) {
        SharedPreferences sp =getPref(context);
        return sp.getInt(KEY, 1);
    }

    public static SharedPreferences getPref(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static void changeCurrentIndex(Context context) {
        int currentIndex = getCurrentIndex(context);
        SharedPreferences sp =getPref(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY, currentIndex + 1);
        editor.apply();
    }
}
