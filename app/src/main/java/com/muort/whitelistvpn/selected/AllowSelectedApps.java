package com.muort.whitelistvpn.selected;

import android.app.AndroidAppHelper;
import android.app.Application;

import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AllowSelectedApps implements IXposedHookLoadPackage {

    private static final String MODULE_PACKAGE = "com.muort.whitelistvpn.selected";
    private static final String PREF_NAME = "config";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        try {
            XposedHelpers.findAndHookMethod(
                    "android.net.VpnService$Builder",
                    lpparam.classLoader,
                    "establish",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                Application app = AndroidAppHelper.currentApplication();
                                if (app == null) return;

                                Object builder = param.thisObject;

                                XSharedPreferences prefs = new XSharedPreferences(MODULE_PACKAGE, PREF_NAME);
                                prefs.reload();

                                Set<String> selected =
                                        prefs.getStringSet(Config.KEY_SELECTED_PACKAGES, null);

                                if (selected == null || selected.isEmpty()) {
                                    return;
                                }

                                for (String pkg : selected) {
                                    if (pkg == null || pkg.trim().isEmpty()) {
                                        continue;
                                    }

                                    try {
                                        XposedHelpers.callMethod(
                                                builder,
                                                "addAllowedApplication",
                                                pkg
                                        );
                                    } catch (Throwable ignored) {
                                    }
                                }
                            } catch (Throwable t) {
                                XposedBridge.log("WhitelistVpnSelected beforeHookedMethod error in "
                                        + lpparam.packageName + ": " + t);
                            }
                        }
                    }
            );
        } catch (Throwable t) {
            XposedBridge.log("WhitelistVpnSelected hook failed in " + lpparam.packageName + ": " + t);
        }
    }
}