package com.example.huanjiayang.Week08SQLiteSimpleDemo;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.example.huanjiayang.Week08SQLiteSimpleDemo.data.WeatherDBHelper;

import java.util.ArrayList;


public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testCreateDB() throws Throwable {

        mContext.deleteDatabase(WeatherDBHelper.DB_NAME);
        WeatherDBHelper myDBHelper = new WeatherDBHelper(this.mContext,WeatherDBHelper.DB_NAME,null,WeatherDBHelper.DB_VERSION);
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }


    public void testDatabaseData() throws Throwable {
        // Just some dummy data to insert...
        ArrayList mArrayList = new ArrayList();
        mArrayList.add("Tomorrow - Foggy 9 / 13");
        mArrayList.add("Thurs - Rainy 8 / 13");
        mArrayList.add("Fri - foggy 8 / 12");
        mArrayList.add("Sat - Sunny 9 / 14");
        mArrayList.add("Sun - Sunny 10 / 15");
        mArrayList.add("Mon - Sunny 11 / 15");

        WeatherDBHelper dbHelper = new WeatherDBHelper(this.mContext,WeatherDBHelper.DB_NAME,null,WeatherDBHelper.DB_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        for(int i=0;i<mArrayList.size();i++) {
            values.put("DAY_WEATHER", mArrayList.get(i).toString());
            long locationRowId;
            locationRowId = db.insert("WEATHER_TABLE", null, values);
            assertTrue(locationRowId != -1);
        }

// Data's inserted. IN THEORY.
// Now pull some out to stare at it and verify it made
// the round trip.
// Specify which columns you want.
        String[] columns = {
                "DAY_WEATHER"
        };
// A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                "WEATHER_TABLE", // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );
// If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
// Get the value in each column by finding the appropriate column index.
            int i = 0;
            do {
                int dayweatherIndex = cursor.getColumnIndex("DAY_WEATHER");
                String dayweather = cursor.getString(dayweatherIndex);
// data was returned! Assert that it's the right data
                Log.i("Week08SQLiteSimpleDemoTEST column index", "" + dayweatherIndex);
                Log.i("Week08SQLiteSimpleDemoTEST", dayweather);
                assertEquals(dayweather, mArrayList.get(i).toString());
                i++;
            }while(cursor.moveToNext());
        } else {
// If no result returned in the query, we indicate the test
// fails by calling JUnit fail()...
            fail("No values returned :(");
        }


    }

}