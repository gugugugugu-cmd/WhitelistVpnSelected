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
        XposedBridge.log("WhitelistVpnSelected loaded: " + lpparam.packageName);

        try {
            XposedHelpers.findAndHookMethod(
                    "android.net.VpnService$Builder",
                    lpparam.classLoader,
                    "establish",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("WhitelistVpnSelected establish hooked in: " + lpparam.packageName);

                            try {
                                Application app = AndroidAppHelper.currentApplication();
                                if (app == null) {
                                    XposedBridge.log("WhitelistVpnSelected currentApplication is null");
                                    return;
                                }

                                Object builder = param.thisObject;

                                XSharedPreferences prefs = new XSharedPreferences(MODULE_PACKAGE, PREF_NAME);
                                prefs.reload();

                                Set<String> selected =
                                        prefs.getStringSet(Config.KEY_SELECTED_PACKAGES, null);

                                XposedBridge.log("WhitelistVpnSelected selected count = "
                                        + (selected == null ? -1 : selected.size()));

                                if (selected == null || selected.isEmpty()) {
                                    return;
                                }

                                for (String pkg : selected) {
                                    if (pkg == null || pkg.trim().isEmpty()) {
                                        continue;
                                    }

                                    try {
                                        XposedHelpers.callMethod(builder, "addAllowedApplication", pkg);
                                        XposedBridge.log("WhitelistVpnSelected added: " + pkg);
                                    } catch (Throwable t) {
                                        XposedBridge.log("WhitelistVpnSelected add failed: " + pkg + " -> " + t);
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