package com.example.huanjiayang.Week09ServiceSimpleDemo;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.example.huanjiayang.Week09ServiceSimpleDemo.data.WeatherDBHelper;
import com.example.huanjiayang.Week09ServiceSimpleDemo.data.WeatherDataModel;

import java.util.ArrayList;


public class WeatherProviderTest extends ApplicationTestCase<Application> {
    public WeatherProviderTest() {
        super(Application.class);
    }

    public void testCreateDB() throws Throwable {

        mContext.deleteDatabase(WeatherDBHelper.DB_NAME);

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
            values.put(WeatherDataModel.weatherEntry.COLUMN_DAY_WEATHER, mArrayList.get(i).toString());
            long locationRowId;
            Uri retUri;

            //locationRowId = db.insert(WeatherDataModel.weatherEntry.TABLE_NAME, null, values);
            retUri = mContext.getContentResolver().insert(WeatherDataModel.weatherEntry.CONTENT_URI,values);

            assertTrue(retUri != null);
        }

        //dbHelper.close();
// Data's inserted. IN THEORY.
// Now pull some out to stare at it and verify it made
// the round trip.
// Specify which columns you want.
        String[] columns = {
                WeatherDataModel.weatherEntry.COLUMN_DAY_WEATHER
        };
// A cursor is your primary interface to the query results.
  /*     Cursor cursor = db.query(
                "WEATHER_TABLE", // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

*/
        Cursor cursor = mContext.getContentResolver().query(
                WeatherDataModel.weatherEntry.CONTENT_URI, // Table to Query
                columns,
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

// If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
// Get the value in each column by finding the appropriate column index.
            int i = 0;
            do {
                int dayweatherIndex = cursor.getColumnIndex(WeatherDataModel.weatherEntry.COLUMN_DAY_WEATHER);
                String dayweather = cursor.getString(dayweatherIndex);
// data was returned! Assert that it's the right data
                Log.i("Week09WeatherProvider", "column index" + dayweatherIndex);
                Log.i("Week09WeatherProvider", dayweather);
                assertEquals(dayweather, mArrayList.get(i).toString());
                i++;
            }while(cursor.moveToNext());
        } else {
// If no result returned in the query, we indicate the test
// fails by calling JUnit fail()...
            fail("No values returned :(");
        }


    }

    public void testProviderCreateType() throws Throwable{

        String type = mContext.getContentResolver().getType(WeatherDataModel.weatherEntry.CONTENT_URI);

        assertEquals(WeatherDataModel.weatherEntry.CONTENT_TYPE_DIR, type);


        int test_day = 1;
// content://com.example.android.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                WeatherDataModel.weatherEntry.buildWeatherUriWithId(test_day));
// vnd.android.cursor.item/com.example.android.sunshine.app/weather
        assertEquals(WeatherDataModel.weatherEntry.CONTENT_TYPE_ITEM, type);
    }

}