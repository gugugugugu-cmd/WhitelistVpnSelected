package com.muort.whitelistvpn.selected;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TextView tvSelectedCount;
    private Button btnSelectApps;
    private Button btnClearSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        btnSelectApps = findViewById(R.id.btnSelectApps);
        btnClearSelected = findViewById(R.id.btnClearSelected);

        btnSelectApps.setOnClickListener(v ->
                startActivity(new Intent(this, AppSelectActivity.class)));

        btnClearSelected.setOnClickListener(v -> {
            Config.clearSelectedPackages(this);
            updateSelectedCount();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSelectedCount();
    }

    private void updateSelectedCount() {
        Set<String> selected = Config.getSelectedPackages(this);
        tvSelectedCount.setText("已选择：" + selected.size());
    }
}