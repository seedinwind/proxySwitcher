package com.depart.proxy.proxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Yuan Jiwei on 17/1/9.
 */
public class SharePreferencesUtil {
    public static final String FIRST_ID_SCAN = "first_id_scan";
    private static SharePreferencesUtil sInstance;
    private static SharedPreferences sSp;

    private SharePreferencesUtil(Context context) {
        sSp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void init(Context context) {
        if (sInstance == null) {
            sInstance = new SharePreferencesUtil(context.getApplicationContext());
        }
    }

    public static void put(String key, String value) {
        SharedPreferences.Editor editor = sSp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void put(String key, boolean value) {
        SharedPreferences.Editor editor = sSp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void put(String key, int value) {
        SharedPreferences.Editor editor = sSp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static String get(String key) {
        return sSp.getString(key, "");
    }

    public static boolean getBoolean(String key) {
        return sSp.getBoolean(key, false);
    }

    public static int getInt(String key) {
        return sSp.getInt(key, 0);
    }

    public static void remove(String[] keys) {
        SharedPreferences.Editor editor = sSp.edit();
        for (String key : keys) {
            editor.remove(key);
        }
        editor.commit();
    }
}
