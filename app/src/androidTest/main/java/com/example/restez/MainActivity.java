package com.example.restez;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.restez.Adapter.AppModelAdapter;
import com.example.restez.Model.AppModel;
import com.example.restez.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;



public class MainActivity extends BlunoLibrary {
    public static final int OPEN_NEW_ACTIVITY = 123456;
    private RecyclerView appsRecyclerView;
    private AppModelAdapter adapter;

    private List<AppModel> appList;
    private DatabaseHandler db;
    private BuzzForNotif receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        FloatingActionButton addNewApp = findViewById(R.id.add_new_app);

        FloatingActionButton refresh = findViewById(R.id.refresh_list);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshList();
            }
        });

        appsRecyclerView = findViewById(R.id.appsRecyclerView);
        appsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = new DatabaseHandler(getApplicationContext());
        db.openDatabase();


        adapter = new AppModelAdapter(db, this);
        appsRecyclerView.setAdapter(adapter);

        appList = db.getAllApps();
        Collections.reverse(appList);
        adapter.setTasks(appList);

        addNewApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SelectNewApp.class);
                startActivityForResult(intent, OPEN_NEW_ACTIVITY);
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        receiver = new BuzzForNotif();
        intentFilter.addAction("com.github.chagall.notificationslistener");
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_NEW_ACTIVITY) {
            refreshList();
        }
    }

    protected void refreshList() {
        appList = db.getAllApps();
        Collections.reverse(appList);
        adapter.setTasks(appList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onConectionStateChange(connectionStateEnum theconnectionStateEnum) {

    }

    @Override
    public void onSerialReceived(String theString) {

    }


    public class BuzzForNotif extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int receivedNotificationCode = intent.getIntExtra("Notification Code",-1);
        }
    }



}