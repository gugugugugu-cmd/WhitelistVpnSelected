package com.muort.whitelistvpn.selected;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.util.Set;

public class SelectedAppsProvider extends ContentProvider {

    public static final String AUTHORITY = "com.muort.whitelistvpn.selected.provider";
    public static final String PATH_SELECTED_APPS = "selected_apps";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + PATH_SELECTED_APPS);

    private static final int MATCH_SELECTED_APPS = 1;

    private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public SelectedAppsProvider() {
        uriMatcher.addURI(AUTHORITY, PATH_SELECTED_APPS, MATCH_SELECTED_APPS);
    }

    @Override
    public boolean onCreate() {
        Log.i(Config.TAG, "SelectedAppsProvider onCreate()");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Log.i(Config.TAG, "SelectedAppsProvider query() uri=" + uri);

        if (uriMatcher.match(uri) != MATCH_SELECTED_APPS) {
            Log.w(Config.TAG, "SelectedAppsProvider query() unsupported uri=" + uri);
            return null;
        }

        MatrixCursor cursor = new MatrixCursor(new String[]{"_id", "package_name"});

        if (getContext() == null) {
            Log.w(Config.TAG, "SelectedAppsProvider query() context is null");
            return cursor;
        }

        Set<String> selected = Config.getSelectedPackages(getContext());
        Log.i(Config.TAG, "SelectedAppsProvider query() selected count=" + selected.size());

        int id = 0;
        for (String pkg : selected) {
            cursor.addRow(new Object[]{id++, pkg});
            Log.i(Config.TAG, "SelectedAppsProvider query() row=" + pkg);
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        if (uriMatcher.match(uri) == MATCH_SELECTED_APPS) {
            return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH_SELECTED_APPS;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}