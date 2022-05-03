package com.example.restez;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import com.example.restez.Model.AppModel;
import com.example.restez.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectNewApp extends AppCompatActivity {
    private Button selectApp, addApp;

    private RadioGroup monitorType;
    private RadioButton selectedButton;
    private ArrayList<PInfo> apps;

    private TextView appSelection;
    private EditText query;

    private DatabaseHandler db;
    private int selectedMonitorType;

    private String selectedPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_new_app);

        apps = getInstalledApps(false);

        selectApp = findViewById(R.id.select_app);
        appSelection = findViewById(R.id.selection);
        monitorType = findViewById(R.id.monitor_type);
        query = findViewById(R.id.query);

        addApp = findViewById(R.id.add_app);

        db = new DatabaseHandler(getApplicationContext());
        db.openDatabase();



        selectApp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });

        monitorType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                selectedButton = radioGroup.findViewById(id);

                switch(id) {
                    case R.id.anything:
                        query.setEnabled(false);
                        selectedMonitorType = 0;
                        break;
                    case R.id.isExactly:
                        query.setEnabled(true);
                        selectedMonitorType = 1;
                        break;
                    case R.id.contains:
                        query.setEnabled(true);
                        selectedMonitorType = 2;
                        break;
                }
            }
        });

        addApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppModel app = new AppModel();
                app.setName(appSelection.getText().toString());
                app.setType(selectedMonitorType);
                app.setStatus(0);
                app.setSearch(query.getText().toString());
                app.setPackageName(selectedPackage);
                db.insertApp(app);
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();

        for (PInfo app : apps) {
            MenuItem item = popup.getMenu()
                    .add(R.id.group, Menu.NONE, Menu.NONE, menuIconWithText(app.icon, app.appname));
            item.setTooltipText(app.packageName);
        }

        inflater.inflate(R.menu.app_list, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                appSelection.setText(menuItem.getTitle());
                selectedPackage = menuItem.getTooltipText().toString();
                return true;
            }
        });

        popup.show();
    }


    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, 75, 75);
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    class PInfo {
        private String appname = "";
        private Drawable icon;
        private String packageName;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
        ArrayList<PInfo> res = new ArrayList<PInfo>();
        List<PackageInfo> packs = getPackageManager()
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);

        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);

            String name = p.applicationInfo.loadLabel(getPackageManager()).toString();
            if ((isSystemPackage(p)) && (p.versionName == null) && (!name.contains("com."))) {
                continue ;
            }
            PInfo newInfo = new PInfo();
            newInfo.appname = name;
            newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
            newInfo.packageName = p.packageName;
            res.add(newInfo);
        }
        return res;
    }
}