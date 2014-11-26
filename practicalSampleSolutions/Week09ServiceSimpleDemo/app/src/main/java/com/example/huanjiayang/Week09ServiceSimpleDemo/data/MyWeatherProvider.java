package com.example.huanjiayang.Week09ServiceSimpleDemo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MyWeatherProvider extends ContentProvider {

    public static final int WEATHER = 100;
    public static final int WEATHER_WITH_ID = 101;
    private static final UriMatcher myUriMatcher = buildUriMatcher();
    public static WeatherDBHelper myDBHelper;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(WeatherDataModel.CONTENT_AUTHORITY,WeatherDataModel.PATH_WEATHER,WEATHER);

        matcher.addURI(WeatherDataModel.CONTENT_AUTHORITY,WeatherDataModel.PATH_WEATHER+"/#",WEATHER_WITH_ID);

        return matcher;
    }


    public MyWeatherProvider() {
    }


    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        int match_code = myUriMatcher.match(uri);

        switch(match_code){
            case WEATHER:
                return WeatherDataModel.weatherEntry.CONTENT_TYPE_DIR;
            case WEATHER_WITH_ID:
                return WeatherDataModel.weatherEntry.CONTENT_TYPE_ITEM;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        int match_code = myUriMatcher.match(uri);
        Uri retUri = null;

        switch(match_code){
            case WEATHER:{
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                long _id = db.insert(WeatherDataModel.weatherEntry.TABLE_NAME,null,values);
                if (_id > 0)
                    retUri = WeatherDataModel.weatherEntry.buildWeatherUriWithId(_id);
                else
                    throw new SQLException("failed to insert");
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        return retUri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        myDBHelper = new WeatherDBHelper(getContext(),WeatherDBHelper.DB_NAME,null,WeatherDBHelper.DB_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        int match_code = myUriMatcher.match(uri);
        Cursor myCursor;

        switch(match_code){
            case WEATHER:{
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                myCursor = db.query(
                        "WEATHER_TABLE", // Table to Query
                        projection,
                        null, // Columns for the "where" clause
                        null, // Values for the "where" clause
                        null, // columns to group by
                        null, // columns to filter by row groups
                        null // sort order
                );
                Log.i("Week09WeatherProvider", "querying for WEATHER");
                Log.i("Week09WeatherProvider", myCursor.getCount()+"");
                break;
            }
            case WEATHER_WITH_ID:{
                myCursor = myDBHelper.getReadableDatabase().query(
                        WeatherDataModel.weatherEntry.TABLE_NAME,
                        projection,
                        WeatherDataModel.weatherEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        myCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return myCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int match_code = myUriMatcher.match(uri);

        switch(match_code){
            case WEATHER:{
                myDBHelper.clearTable(WeatherDataModel.weatherEntry.TABLE_NAME);
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int match_code = myUriMatcher.match(uri);

        switch(match_code){
            case WEATHER:{
                myDBHelper.clearTable(WeatherDataModel.weatherEntry.TABLE_NAME);
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return 0;
    }

}
