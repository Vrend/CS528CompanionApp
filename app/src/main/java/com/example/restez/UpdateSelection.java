package com.example.restez;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.restez.Adapter.AppModelAdapter;
import com.example.restez.Utils.DatabaseHandler;

public class UpdateSelection extends AppCompatActivity {

    private RadioGroup monitorType;
    private RadioButton selectedButton;
    private EditText query;
    private Button finish;

    private AppModelAdapter adapter;
    private int type;
    private String queryString;

    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_selection);


        monitorType = findViewById(R.id.monitor_type);
        query = findViewById(R.id.query);
        finish = findViewById(R.id.finished);


        monitorType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                selectedButton = radioGroup.findViewById(id);
                switch(id) {
                    case R.id.anything:
                        type = 0;
                        query.setEnabled(false);
                        break;
                    case R.id.isExactly:
                        type = 1;
                        query.setEnabled(true);
                        break;
                    case R.id.contains:
                        type = 2;
                        query.setEnabled(true);
                        break;
                }
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = getIntent().getExtras();
                db = new DatabaseHandler(getApplicationContext());
                db.openDatabase();

                db.updateMonitorType(Integer.parseInt(extras.get("ID").toString()),
                        query.getText().toString(), type);

                db.close();
                finishAndRemoveTask();
            }
        });
    }
}