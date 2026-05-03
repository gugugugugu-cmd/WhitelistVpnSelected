package com.muort.whitelistvpn.selected;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TextView tvSelectedCount;
    private Button btnSelectApps;
    private Button btnClearSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Config.TAG, "MainActivity onCreate()");
        setContentView(R.layout.activity_main);

        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        btnSelectApps = findViewById(R.id.btnSelectApps);
        btnClearSelected = findViewById(R.id.btnClearSelected);

        btnSelectApps.setOnClickListener(v -> {
            Log.i(Config.TAG, "MainActivity open AppSelectActivity");
            startActivity(new Intent(this, AppSelectActivity.class));
        });

        btnClearSelected.setOnClickListener(v -> {
            boolean ok = Config.clearSelectedPackages(this);
            if (ok) {
                Toast.makeText(this, "已清空", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "清空失败", Toast.LENGTH_SHORT).show();
            }
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
        Set<String> selected = Config.getSelectedPackages(this);
        Log.i(Config.TAG, "MainActivity updateSelectedCount() count=" + selected.size());
        tvSelectedCount.setText("已选择：" + selected.size());
    }
}