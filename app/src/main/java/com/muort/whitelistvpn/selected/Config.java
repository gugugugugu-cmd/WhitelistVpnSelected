package com.muort.whitelistvpn.selected;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class Config {

    public static final String TAG = "WhitelistVpnSelected";
    public static final String PREF_NAME = "config";
    public static final String KEY_SELECTED_PACKAGES = "selected_packages";

    public static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static Set<String> getSelectedPackages(Context context) {
        Set<String> result = new HashSet<>(
                prefs(context).getStringSet(KEY_SELECTED_PACKAGES, new HashSet<>())
        );
        Log.i(TAG, "getSelectedPackages() count=" + result.size());
        return result;
    }

    public static boolean setSelectedPackages(Context context, Set<String> packages) {
        try {
            prefs(context).edit()
                    .putStringSet(KEY_SELECTED_PACKAGES, new HashSet<>(packages))
                    .apply();

            Log.i(TAG, "setSelectedPackages() success, count=" + packages.size());
            return true;
        } catch (Throwable t) {
            Log.e(TAG, "setSelectedPackages() failed: " + t, t);
            return false;
        }
    }

    public static boolean clearSelectedPackages(Context context) {
        try {
            prefs(context).edit().remove(KEY_SELECTED_PACKAGES).apply();
            Log.i(TAG, "clearSelectedPackages() success");
            return true;
        } catch (Throwable t) {
            Log.e(TAG, "clearSelectedPackages() failed: " + t, t);
            return false;
        }
    }
}