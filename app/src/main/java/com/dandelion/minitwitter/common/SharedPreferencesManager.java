package com.dandelion.minitwitter.common;
// [Shared Preferences Manager]: access to shared preferences to manage data storage

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Shared Preferences Manager > application data management
 * - Access to shared preferences to manage application data storage
 * - Because is used in different applications points in a common class (in order to keep resources)
 * - Will manage session and application data storage
 */
public class SharedPreferencesManager {
    // [Vars]
    private static final String APP_SETTINGS_FILE = "APP_SETTINGS";

    // (Constructor)
    public SharedPreferencesManager() {

    }
    // [Methods]: data manager methods

    // Gets Shared Preferences resource
    private static SharedPreferences getSharedPreferences() {
        return GlobalApp.getContext()
                .getSharedPreferences(APP_SETTINGS_FILE, Context.MODE_PRIVATE);
    }

    // Saves a String data in preferences file
    public static void saveStringValue(String paramDataLabel, String paramDataValue) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(paramDataLabel,paramDataValue);
        editor.commit();
    }

    // Saves a Boolean data in preferences file
    public static void saveBoolValue(String paramDataLabel, Boolean paramDataValue) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(paramDataLabel,paramDataValue);
        editor.commit();
    }

    // Gets a String data stored preferences file
    public static String getStringValue(String paramDataLabel) {
        return getSharedPreferences().getString(paramDataLabel,null);
    }

    // Gets a Boolean data stored preferences file
    public static Boolean getBoolValue(String paramDataLabel) {
        return getSharedPreferences().getBoolean(paramDataLabel,false);
    }
}
