package com.example.restez.Utils;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.restez.Model.AppModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "appListDatabase";
    public static final String APP_TABLE = "app";

    // entries in the table
    private static final String ID = "id";
    private static final String APP_NAME = "name";
    private static final String STATUS = "status";
    private static final String TYPE = "type";
    private static final String QUERY_NAME = "queryy";
    private static final String PACKAGE_NAME = "packageName";

//    private static final String CREATE_APP_TABLE = "CREATE TABLE app(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
//            "APP_NAME TEXT, STATUS INTEGER, TYPE TEXT, QUERY_NAME TEXT, PACKAGE_NAME TEXT)";

    private static final String CREATE_APP_TABLE = String.format("CREATE TABLE app(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT)", ID, APP_NAME, STATUS, TYPE, QUERY_NAME, PACKAGE_NAME);


    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("THIS SHOULD BE CALLED", "THIS SHOULD BE CALLED");
        System.out.println(CREATE_APP_TABLE);
        db.execSQL(CREATE_APP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + APP_TABLE);
        onCreate(db);
    }
    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertApp(AppModel app) {
        ContentValues cv = new ContentValues();
        cv.put(APP_NAME, app.getName());
        cv.put(TYPE, app.getType());
        cv.put(QUERY_NAME, app.getSearch());
        cv.put(STATUS, 0);
        cv.put(PACKAGE_NAME, app.getPackageName());
        db.insert(APP_TABLE, null, cv);
    }

    @SuppressLint("Range")
    public List<AppModel> getAllApps() {
        List<AppModel> appList = new ArrayList<>();
        Cursor cur = null;

        db.beginTransaction();

        try {
            cur = db.query(APP_TABLE, null, null, null, null, null, null, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        AppModel app = new AppModel();
                        app.setId(cur.getInt(cur.getColumnIndex(ID)));
                        app.setName(cur.getString(cur.getColumnIndex(APP_NAME)));
                        app.setType(cur.getInt(cur.getColumnIndex(TYPE)));
                        app.setSearch(cur.getString(cur.getColumnIndex(QUERY_NAME)));
                        app.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        app.setPackageName(cur.getString(cur.getColumnIndex(PACKAGE_NAME)));
                        appList.add(app);
                    } while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            cur.close();
        }
        return appList;
    }

    public void updateStatus(int id, int status) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(APP_TABLE, cv, ID + "=?",
            new String[] {String.valueOf(id)});

    }

    public void updateApp(int id, String app) {
        ContentValues cv = new ContentValues();
        cv.put(APP_NAME, app);
        db.update(APP_TABLE, cv, ID + "=?", new String[] {String.valueOf(id)});
    }

    public void updateMonitorType(int id, String query, int type) {
        ContentValues cv = new ContentValues();
        cv.put(QUERY_NAME, query);
        cv.put(TYPE, type);
        db.update(APP_TABLE, cv, ID + "=?", new String[] {String.valueOf(id)});
    }

    public void deleteApp(int id) {
        db.delete(APP_TABLE, ID + "=?", new String[]{String.valueOf(id)});
    }











}
