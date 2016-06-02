package com.example.arono.minesweeper.Table;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

import com.example.arono.minesweeper.R;

public class TableActivity extends AppCompatActivity {


    TabHost tabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec specs = tabHost.newTabSpec("tag1");
        specs.setContent(R.id.tabList);
        specs.setIndicator("List");
        tabHost.addTab(specs);

        specs = tabHost.newTabSpec("tag2");
        specs.setContent(R.id.tabMap);
        specs.setIndicator("Map");
        tabHost.addTab(specs);
        tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.DKGRAY);
        tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.DKGRAY);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
