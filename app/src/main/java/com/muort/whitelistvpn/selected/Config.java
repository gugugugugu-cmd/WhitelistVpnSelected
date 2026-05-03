package com.muort.whitelistvpn.selected;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

public class Config {

    public static final String TAG = "WhitelistVpnSelected";
    public static final String MODULE_PACKAGE = "com.muort.whitelistvpn.selected";
    public static final String DIR_NAME = MODULE_PACKAGE;
    public static final String FILE_NAME = "selected_apps.txt";

    public static File getBaseDir() {
        return new File(Environment.getExternalStorageDirectory(),
             "WhitelistVpnSelected");
    }

    public static File getConfigFile() {
        return new File(getBaseDir(), FILE_NAME);
    }

    public static Set<String> getSelectedPackages() {
        Set<String> result = new LinkedHashSet<>();
        File file = getConfigFile();

        Log.i(TAG, "getSelectedPackages() path=" + file.getAbsolutePath());
        Log.i(TAG, "getSelectedPackages() exists=" + file.exists()
                + ", isFile=" + file.isFile()
                + ", length=" + (file.exists() ? file.length() : -1));

        if (!file.exists() || !file.isFile()) {
            Log.w(TAG, "getSelectedPackages() config file not found");
            return result;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String raw = line;
                line = line.trim();
                if (!line.isEmpty()) {
                    result.add(line);
                    Log.i(TAG, "getSelectedPackages() read line: " + raw);
                }
            }
        } catch (Throwable t) {
            Log.e(TAG, "getSelectedPackages() failed: " + t, t);
        }

        Log.i(TAG, "getSelectedPackages() count=" + result.size());
        return result;
    }

    public static void setSelectedPackages(Set<String> packages) {
        File dir = getBaseDir();
        Log.i(TAG, "setSelectedPackages() dir=" + dir.getAbsolutePath());

        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            Log.i(TAG, "setSelectedPackages() mkdirs=" + mkdirs);
        } else {
            Log.i(TAG, "setSelectedPackages() dir already exists");
        }

        File file = getConfigFile();
        Log.i(TAG, "setSelectedPackages() file=" + file.getAbsolutePath());

        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String pkg : packages) {
            if (pkg == null) continue;
            pkg = pkg.trim();
            if (pkg.isEmpty()) continue;
            sb.append(pkg).append('\n');
            count++;
            Log.i(TAG, "setSelectedPackages() write pkg=" + pkg);
        }

        try (FileOutputStream fos = new FileOutputStream(file, false)) {
            fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            fos.flush();
            Log.i(TAG, "setSelectedPackages() success, count=" + count
                    + ", exists=" + file.exists()
                    + ", length=" + file.length());
        } catch (Throwable t) {
            Log.e(TAG, "setSelectedPackages() failed: " + t, t);
        }
    }

    public static void clearSelectedPackages() {
        File file = getConfigFile();
        Log.i(TAG, "clearSelectedPackages() file=" + file.getAbsolutePath()
                + ", exists=" + file.exists());

        if (file.exists()) {
            boolean deleted = file.delete();
            Log.i(TAG, "clearSelectedPackages() deleted=" + deleted);
        }
    }
}