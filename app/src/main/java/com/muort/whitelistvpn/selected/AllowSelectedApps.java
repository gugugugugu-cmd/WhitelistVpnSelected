package com.muort.whitelistvpn.selected;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;

import java.util.LinkedHashSet;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AllowSelectedApps implements IXposedHookLoadPackage {

    private static final Uri SELECTED_APPS_URI =
            Uri.parse("content://com.muort.whitelistvpn.selected.provider/selected_apps");

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("WhitelistVpnSelected loaded: " + lpparam.packageName
                + " process=" + lpparam.processName);

        try {
            XposedHelpers.findAndHookMethod(
                    "android.net.VpnService$Builder",
                    lpparam.classLoader,
                    "establish",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("WhitelistVpnSelected establish hooked in: "
                                    + lpparam.packageName + " process=" + lpparam.processName);

                            try {
                                Application app = AndroidAppHelper.currentApplication();
                                if (app == null) {
                                    XposedBridge.log("WhitelistVpnSelected currentApplication is null");
                                    return;
                                }

                                Object builder = param.thisObject;
                                Set<String> selected = querySelectedPackages(app);

                                XposedBridge.log("WhitelistVpnSelected provider selected count=" + selected.size());

                                if (selected.isEmpty()) {
                                    XposedBridge.log("WhitelistVpnSelected no selected packages, skip");
                                    return;
                                }

                                for (String pkg : selected) {
                                    XposedBridge.log("WhitelistVpnSelected try add: " + pkg);

                                    try {
                                        XposedHelpers.callMethod(builder, "addAllowedApplication", pkg);
                                        XposedBridge.log("WhitelistVpnSelected add success: " + pkg);
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
            XposedBridge.log("WhitelistVpnSelected hook failed in "
                    + lpparam.packageName + ": " + t);
        }
    }

    private Set<String> querySelectedPackages(Application app) {
        Set<String> result = new LinkedHashSet<>();
        Cursor cursor = null;

        try {
            cursor = app.getContentResolver().query(
                    SELECTED_APPS_URI,
                    new String[]{"package_name"},
                    null,
                    null,
                    null
            );

            if (cursor == null) {
                XposedBridge.log("WhitelistVpnSelected provider query returned null cursor");
                return result;
            }

            int index = cursor.getColumnIndex("package_name");
            if (index < 0) {
                XposedBridge.log("WhitelistVpnSelected provider cursor missing column package_name");
                return result;
            }

            while (cursor.moveToNext()) {
                String pkg = cursor.getString(index);
                if (pkg != null && !pkg.trim().isEmpty()) {
                    result.add(pkg);
                    XposedBridge.log("WhitelistVpnSelected provider row: " + pkg);
                }
            }
        } catch (Throwable t) {
            XposedBridge.log("WhitelistVpnSelected provider query failed: " + t);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable ignored) {
                }
            }
        }

        return result;
    }
}