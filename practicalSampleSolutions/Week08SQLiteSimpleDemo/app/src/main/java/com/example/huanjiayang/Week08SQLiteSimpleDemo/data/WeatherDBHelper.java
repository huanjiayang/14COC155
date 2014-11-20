package com.example.huanjiayang.Week08SQLiteSimpleDemo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by huanjiayang on 16/11/14.
 */
public class WeatherDBHelper extends SQLiteOpenHelper{

    public static final int DB_VERSION = 1;
    // db name as public as we use it in test later
    public static final String DB_NAME = "weather.db";

    public SQLiteDatabase myDB;

    public WeatherDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
        myDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        myDB = db;

        String query = "CREATE TABLE IF NOT EXISTS WEATHER_TABLE ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, DAY_WEATHER TEXT NOT NULL );";

        db.execSQL(query);
        Log.i("Week08SQLiteSimpleDemoTEST","WeatherDBHelper onCreate()");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS WEATHER_TABLE");

        onCreate(db);
    }


    public void clearTable(String table_name){
        myDB.execSQL("DELETE FROM "+ table_name);
    }
}
