package com.muort.whitelistvpn.selected;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AppSelectActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnSave;
    private EditText editSearch;
    private AppListAdapter adapter;
    private final List<AppInfo> appList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Config.TAG, "AppSelectActivity onCreate()");
        setContentView(R.layout.activity_app_select);

        listView = findViewById(R.id.listViewApps);
        btnSave = findViewById(R.id.btnSaveApps);
        editSearch = findViewById(R.id.editSearch);

        loadApps();

        adapter = new AppListAdapter(this, appList);
        listView.setAdapter(adapter);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s == null ? "" : s.toString();
                Log.i(Config.TAG, "AppSelectActivity filter keyword=" + keyword);
                adapter.filter(keyword);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnSave.setOnClickListener(v -> saveSelection());
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();
        Set<String> selected = Config.getSelectedPackages();
        Log.i(Config.TAG, "AppSelectActivity loadApps() selected count=" + selected.size());

        List<ApplicationInfo> installed = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Log.i(Config.TAG, "AppSelectActivity installed app count=" + installed.size());

        for (ApplicationInfo ai : installed) {
            String label = String.valueOf(pm.getApplicationLabel(ai));
            String pkg = ai.packageName;

            appList.add(new AppInfo(
                    label,
                    pkg,
                    pm.getApplicationIcon(ai),
                    selected.contains(pkg)
            ));
        }

        appList.sort((a, b) -> a.appName.compareToIgnoreCase(b.appName));
        Log.i(Config.TAG, "AppSelectActivity loadApps() final list count=" + appList.size());
    }

    private void saveSelection() {
        Set<String> selected = new LinkedHashSet<>();
        for (AppInfo app : adapter.getOriginalApps()) {
            if (app.checked) {
                selected.add(app.packageName);
                Log.i(Config.TAG, "AppSelectActivity save selected pkg=" + app.packageName);
            }
        }

        Log.i(Config.TAG, "AppSelectActivity saveSelection() count=" + selected.size());
        Config.setSelectedPackages(selected);

        Set<String> verify = Config.getSelectedPackages();
        Log.i(Config.TAG, "AppSelectActivity verify saved count=" + verify.size());

        finish();
    }
}