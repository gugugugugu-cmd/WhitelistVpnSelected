package com.muort.whitelistvpn.selected;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

public class Config {

    public static final String MODULE_PACKAGE = "com.muort.whitelistvpn.selected";
    public static final String DIR_NAME = MODULE_PACKAGE;
    public static final String FILE_NAME = "selected_apps.txt";

    public static File getBaseDir() {
        return new File(Environment.getExternalStorageDirectory(),
                "Android/media/" + DIR_NAME);
    }

    public static File getConfigFile() {
        return new File(getBaseDir(), FILE_NAME);
    }

    public static Set<String> getSelectedPackages() {
        Set<String> result = new LinkedHashSet<>();
        File file = getConfigFile();

        if (!file.exists() || !file.isFile()) {
            return result;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    result.add(line);
                }
            }
        } catch (Throwable ignored) {
        }

        return result;
    }

    public static void setSelectedPackages(Set<String> packages) {
        File dir = getBaseDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = getConfigFile();

        StringBuilder sb = new StringBuilder();
        for (String pkg : packages) {
            if (pkg == null) continue;
            pkg = pkg.trim();
            if (pkg.isEmpty()) continue;
            sb.append(pkg).append('\n');
        }

        try (FileOutputStream fos = new FileOutputStream(file, false)) {
            fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            fos.flush();
        } catch (Throwable ignored) {
        }
    }

    public static void clearSelectedPackages() {
        File file = getConfigFile();
        if (file.exists()) {
            file.delete();
        }
    }
}