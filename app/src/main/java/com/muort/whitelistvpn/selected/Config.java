package com.muort.whitelistvpn.selected;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class Config {
    public static final String PREF_NAME = "config";
    public static final String KEY_SELECTED_PACKAGES = "selected_packages";

    public static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static Set<String> getSelectedPackages(Context context) {
        return new HashSet<>(
                prefs(context).getStringSet(KEY_SELECTED_PACKAGES, new HashSet<>())
        );
    }

    public static void setSelectedPackages(Context context, Set<String> packages) {
        prefs(context).edit().putStringSet(KEY_SELECTED_PACKAGES, new HashSet<>(packages)).apply();
    }

    public static void clearSelectedPackages(Context context) {
        prefs(context).edit().remove(KEY_SELECTED_PACKAGES).apply();
    }
}