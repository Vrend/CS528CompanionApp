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
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    private Button buttonScan;

    private TextView textAccelTag;

    private boolean state = true;

    private StringBuilder data_buffer = new StringBuilder();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();	//onDestroy Process by BlunoLibrary
        unregisterReceiver(receiver);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        request(1000, new OnPermissionsResult() {
            @Override
            public void OnSuccess() {
                Toast.makeText(MainActivity.this,"Permissions Granted",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFail(List<String> noPermissions) {
                Toast.makeText(MainActivity.this,"Permissions Failed",Toast.LENGTH_SHORT).show();
            }
        });

        onCreateProcess();

        serialBegin(115200);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        buttonScan = (Button) findViewById(R.id.scan);					//initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
            }
        });

        textAccelTag = (TextView) findViewById(R.id.accelTag);

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
        intentFilter.addAction("com.example.restez.NotificationsListener");
        registerReceiver(receiver, intentFilter);


        //startService(new Intent(this, NotificationsListener.class));
    }

    protected void onResume(){
        super.onResume();
        System.out.println("CS528 DataCollect onResume");
        onResumeProcess();	//onResume Process by BlunoLibrary
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);	//onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_NEW_ACTIVITY) {
            refreshList();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        onPauseProcess();		//onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
//		onStopProcess();  //onStop Process by BlunoLibrary
    }


    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        switch (theConnectionState) {											//Four connection state
            case isConnected:
                buttonScan.setText("Connected");
                serialSend("1");
                break;
            case isConnecting:
                buttonScan.setText("Connecting");
                break;
            case isToScan:
                buttonScan.setText("Scan");
                break;
            case isScanning:
                buttonScan.setText("Scanning");
                break;
            case isDisconnecting:
                buttonScan.setText("isDisconnecting");
                serialSend("0");
                break;
            default:
                break;
        }
    }


    protected void refreshList() {
        appList = db.getAllApps();
        Collections.reverse(appList);
        adapter.setTasks(appList);
        adapter.notifyDataSetChanged();
    }




    private String[] verify_data(String input) {
            String tuple = input.trim();
            if(tuple.startsWith("[") && tuple.endsWith("]")) { // needs to be enclosed
//				System.out.println("First check");
                String tuple_tags_removed = tuple.substring(1, tuple.length()-1);
                if(!tuple_tags_removed.contains("[") && !tuple_tags_removed.contains("]")) { // no nested tags
//					System.out.println("Second check");
                    String[] elements = tuple_tags_removed.split(",");
                    if(elements.length == 4) {
                        boolean fourth_check = true;
//						System.out.println("Third check");
                        for(String element : elements) {
                            try {
                                Integer.parseInt(element);
                            }
                            catch(NumberFormatException nfe) {
                                fourth_check = false;
                                break;
                            }
                        }
                        if(fourth_check) {
                            return elements;
                        }
                    }
                }
            }
        return null;
    }


    @Override
    public void onSerialReceived(String theString) {
        data_buffer.append(theString);
        if(theString.contains("]")) {
            String[] sensor_vals = verify_data(data_buffer.toString());
            if(sensor_vals != null) {
                int magx = Integer.parseInt(sensor_vals[0]);
                int magy = Integer.parseInt(sensor_vals[1]);
                int magz = Integer.parseInt(sensor_vals[2]);
                int magnitude = (int) Math.sqrt(Math.pow(magx, 2) + Math.pow(magy, 2) + Math.pow(magz, 2));
                System.out.println("Magnitude is: "+magnitude);
                if(magnitude > 1000) { // somewhat arbitrary threshold
                    textAccelTag.setText("In Motion");
                    state = false;
                }
                else {
                    textAccelTag.setText("At Rest");
                    state = true;
                }
            }
            data_buffer = new StringBuilder();
        }
    }


    public class BuzzForNotif extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Alert received!");
            if(state) {
                serialSend("3");
            }
        }
    }



}