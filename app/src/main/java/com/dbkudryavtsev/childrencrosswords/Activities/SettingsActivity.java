package com.dbkudryavtsev.childrencrosswords.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dbkudryavtsev.childrencrosswords.R;

public class SettingsActivity extends AppCompatActivity {

    private static final String[] menuStrings = {"Обновить кроссворды", "Сбросить прогресс", "Фон кроссвордов"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ListView settingsList = (ListView) findViewById(R.id.settings_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                menuStrings);
        settingsList.setAdapter(adapter);
    }
}
