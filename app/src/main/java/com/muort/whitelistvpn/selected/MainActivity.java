package com.muort.whitelistvpn.selected;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TextView tvSelectedCount;
    private TextView tvPath;
    private Button btnSelectApps;
    private Button btnClearSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Config.TAG, "MainActivity onCreate()");
        setContentView(R.layout.activity_main);

        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        tvPath = findViewById(R.id.tvPath);
        btnSelectApps = findViewById(R.id.btnSelectApps);
        btnClearSelected = findViewById(R.id.btnClearSelected);

        String path = Config.getConfigFile().getAbsolutePath();
        tvPath.setText("配置文件：\n" + path);
        Log.i(Config.TAG, "MainActivity config path=" + path);

        btnSelectApps.setOnClickListener(v -> {
            Log.i(Config.TAG, "MainActivity open AppSelectActivity");
            startActivity(new Intent(this, AppSelectActivity.class));
        });

        btnClearSelected.setOnClickListener(v -> {
            Log.i(Config.TAG, "MainActivity clear selected clicked");
            Config.clearSelectedPackages();
            updateSelectedCount();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(Config.TAG, "MainActivity onResume()");
        updateSelectedCount();
    }

    private void updateSelectedCount() {
        Set<String> selected = Config.getSelectedPackages();
        Log.i(Config.TAG, "MainActivity updateSelectedCount() count=" + selected.size());
        tvSelectedCount.setText("已选择：" + selected.size());
    }
}