package com.example.huanjiayang.Week09ServiceSimpleDemo.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.huanjiayang.Week09ServiceSimpleDemo.data.WeatherDataModel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyWeatherService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.huanjiayang.Week09ServiceSimpleDemo.service.action.FOO";
    private static final String ACTION_BAZ = "com.example.huanjiayang.Week09ServiceSimpleDemo.service.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.huanjiayang.Week09ServiceSimpleDemo.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.huanjiayang.Week09ServiceSimpleDemo.service.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyWeatherService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyWeatherService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public MyWeatherService() {
        super("MyWeatherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // The declaration of result string needs to be
        // outside of the if branching to make sure
        // we always have a result string to return
        String result = "";

        String city_name = intent.getStringExtra("city_name");

        // check the network status
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data


            Log.i("week06inClass", "preparing to get network data");
            result = GET("http://api.openweathermap.org/data/2.5/forecast/daily?q="+city_name+"&mode=json&units=metric&cnt=7");
            Log.i("week06inClass",result);
        } else {
            // We cannot make a Toast here as this is the background thread,
            // We have to publish the no network information back to
            // Main thread using publishProgress and make Toast in the onProgressUpdate
            // function below
            //publishProgress("No Network Connection!");
        }
        // We remove the following return statement as we
        // will now convert it into ArrayList before returning
        //return result;


        // We use the helper functions we add at the end of the Task
        // to construct an ArrayList to pass to the onPostExecute()
        // NOTE that now we are returning a String[] instead of String
        // in this function, the corresponding class declaration for
        // this function and AsyncTask class extension needs to change
        // to match the type.
        String[] weatherresult = getWeatherDataFromJson(result,7);
        Log.i("week06inClass",weatherresult[0]);

        getApplicationContext().getContentResolver().delete(WeatherDataModel.weatherEntry.CONTENT_URI,null,null);

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        for(int i=0;i<weatherresult.length;i++) {
            values.put("DAY_WEATHER", weatherresult[i]);
            getApplicationContext().getContentResolver().insert(WeatherDataModel.weatherEntry.CONTENT_URI, values);
        }

        //myArrayAdapter.swapCursor();
        //displayListView();
        return;
    }


    // The same helper function that make the HTTP call
    public String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            //Log.i("week06inClass", e.toString());
            result = e.toString();
        }

        return result;
    }

    // The same helper function that convert the response buffer into string
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    // We add a few helper function to help with the format
    // and retracting information in JSON and put them
    // into an ArrayList to be used with our ArrayAdapter:

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need, and construct an ArrayList with them
     */
    public String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
    {
        String[] resultStrs = new String[numDays];

        try {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DATETIME = "dt";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);


            for (int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long. We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime = dayForecast.getLong(OWM_DATETIME);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp". Try not to name variables
                // "temp" when working with temperature. It confuses people.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }
        }
        catch(JSONException e){
            return null;
        }
        return resultStrs;
    }

    private String getReadableDateString(long time){
        // Because the open weather map API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, we only display integer degrees.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
