package com.example.huanjiayang.Week09ContentProviderSimpleDemo.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by huanjiayang on 16/11/14.
 */

public class WeatherDataModel {

    public static final String CONTENT_AUTHORITY = "com.example.huanjiayang.Week09ContentProviderSimpleDemo";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = "weather";

    public static final class weatherEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/"+CONTENT_AUTHORITY+"/"+PATH_WEATHER;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/"+CONTENT_AUTHORITY+"/"+PATH_WEATHER;

        public static final String TABLE_NAME = "WEATHER_TABLE";

        public static final String COLUMN_DAY_NO = "DAY_NO";

        public static final String COLUMN_DAY_WEATHER = "DAY_WEATHER";

        public static Uri buildWeatherUriWithDayNo(long day_no){
            return ContentUris.withAppendedId(CONTENT_URI,day_no);
        }

    }


}

