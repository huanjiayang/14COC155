package com.example.huanjiayang.Week08SQLiteSimpleDemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huanjiayang.Week08SQLiteSimpleDemo.data.WeatherDBHelper;

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
import java.util.ArrayList;
import java.util.Date;


public class Week04Activity extends ActionBarActivity {


    //EXTRA MILE ONLY: declare the ArrayAdapter to
    // be used with the ListView
    public ArrayAdapter<String> myArrayAdapter;
    private boolean isTwoPane;
    private DetailFragment myDetailFragment;

    public ArrayList myArrayList = new ArrayList();

    private WeatherDBHelper DBHelper;
    private SQLiteDatabase db;
    // the onCreate() callBack before an Activity is created
    // See the android Activity lifecycle we showed in class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // auto generated code: set the layout for activity
        setContentView(R.layout.activity_week04);

        // We look at whether the placeholder fragment container exists.
        // if it exists then we know we inflated the tablet UI layout,
        // then we want to add the fragment in the placeholder container
        if (findViewById(R.id.weather_detail_container) != null){
            myDetailFragment = new DetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, myDetailFragment)
                    .commit();
            isTwoPane = true;
        }
        else{
            // Otherwise we inflated phone UI layout, we
            // don't do anything, just keep the result in
            // a variable to be used later on
            isTwoPane = false;
        }


        // We call displayListView() function here to populate
        // The ListView first so that the AsyncTask and
        // Manipulate it later on on refreshing
        //displayListView();
        //getApplicationContext().deleteDatabase(WeatherDBHelper.DB_NAME);

        // We instantiate the WeatherDBHelper here and make both the helper and the
        // db a member variable so that we can access them later on
        DBHelper = new WeatherDBHelper(getApplicationContext(),WeatherDBHelper.DB_NAME,null,WeatherDBHelper.DB_VERSION);
        db = DBHelper.getWritableDatabase();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //refreshWeather();
    }

    @Override
    protected void onResume() {
        super.onResume();

        myArrayList.clear();
/*
        WeatherDBHelper dbHelper = new WeatherDBHelper(getApplicationContext(),WeatherDBHelper.DB_NAME,null,WeatherDBHelper.DB_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
*/
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
            do {
                int dayweatherIndex = cursor.getColumnIndex("DAY_WEATHER");
                String dayweather = cursor.getString(dayweatherIndex);
                myArrayList.add(dayweather);
            }while(cursor.moveToNext());
        } else {
// That's weird, it works on MY machine...
            myArrayList.add("No values returned :(");
        }


        //db.close();

        displayListView();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //WeatherDBHelper dbHelper = new WeatherDBHelper(getApplicationContext(),WeatherDBHelper.DB_NAME,null,WeatherDBHelper.DB_VERSION);
        //SQLiteDatabase db = DBHelper.getWritableDatabase();

        DBHelper.clearTable("WEATHER_TABLE");

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        for(int i=0;i<myArrayList.size();i++) {
            values.put("DAY_WEATHER", myArrayList.get(i).toString());
            db.insert("WEATHER_TABLE", null, values);
        }

        //db.close();
    }

    // The function triggered by the new button added in week 05
    // starts an implicit Intent to view a geo location
    //public void seeOnMap(View view){
    public void seeOnMap(){
        // construct the Uri that contains the geo scheme location information
        // the longitude and latitude are given as 0 ,0 to indication no coordinate info
        // then put the address in the query part of the Uri. See Google developer
        // Website for information.
        //String uriString = getString(R.string.geo_uri_without_coord) + ((EditText)findViewById(R.id.edit_message)).getText().toString();
        SharedPreferences city_name_pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String city_name = city_name_pref.getString(getString(R.string.city_name),getString(R.string.city_name_default));
        String uriString = getString(R.string.geo_uri_without_coord) + city_name;

        Uri myUri = Uri.parse(uriString);

        // Create the implicit Intent, no Activity to name,
        // Just telling Android that you intention is to "view"
        // something by giving Intent.ACTION_VIEW as the parameter.
        // See google developer website for all the standard
        // intention values you can use.
        Intent locIntent = new Intent(Intent.ACTION_VIEW);
        // Attach the geo scheme data with the Intent
        locIntent.setData(myUri);

        // Request to start an Activity using the intent
        startActivity(locIntent);

    }

    // The function we associated with Button onClick
    // in the XML layout file. Note that it has to be
    // public void, and must take the Button as argument
    // by taking a View type argument
    public void refreshWeather(){

        // Get the city name entered by the user
        //String city_name = ((EditText)findViewById(R.id.edit_message)).getText().toString();

        SharedPreferences city_name_pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String city_name = city_name_pref.getString(getString(R.string.city_name),getString(R.string.city_name_default));

        ((TextView)findViewById(R.id.location_text)).setText("Weather for " + city_name + ":");
        // Instead of making the network calls,
        // we just start the getWeatherTask here
        // Note that we pass the city name to the getWeatherTask
        // when we start it
        getWeatherTask myGWT = new getWeatherTask();
        myGWT.execute(city_name);

    }


    // EXTRA MILE ONLY: The function that fills the ListView
    public void displayListView(){

        // put raw data in an ArrayList

        //myArrayList.add("no data");
        //myArrayList.add("Tomorrow - Foggy 9 / 13");
        //myArrayList.add("Thurs - Rainy 8 / 13");
        //myArrayList.add("Fri - foggy 8 / 12");
        //myArrayList.add("Sat - Sunny 9 / 14");
        //myArrayList.add("Sun - Sunny 10 / 15");
        //myArrayList.add("Mon - Sunny 11 / 15");

        // Initialize the ArrayAdapter to be used
        myArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.list_item,R.id.list_item_textview,myArrayList);

        //Get hold of the ListView (added in the activity layout!)
        ListView myLV = (ListView)this.findViewById(R.id.myListView);
        // Set the Adpater for the ListView
        myLV.setAdapter(myArrayAdapter);

        //set item click listener for the ListView
        myLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // handle the click

                // Log some information to see item click is handled
                Log.i("week05practical","an item in listview was clicked.");

                // Get the string contained in the Textview clicked
                String myString = ((TextView)view).getText().toString();

                // We found out whether we are on tablet layout in the onCreat(),
                // so if we are on tablet, we update the TextView in the fragment
                if(isTwoPane){
                    ((TextView)myDetailFragment.getView().getRootView().findViewById(R.id.weather_detail_text)).setText(myString);
                }
                else {

                    // Otherwise, if we are on phone layout, we use Intent to move to the
                    // detail activity like before

                    //Create an Explicit Intent with the target Activity class named
                    Intent myIntent = new Intent(getApplicationContext(), Week05WeatherDetailActivity.class);
                    //Put some extra data in the form of a key-value pair with the Intent
                    myIntent.putExtra("weatherdetail", myString);
                    //start Activity with the Intent
                    startActivity(myIntent);
                }

            }
        });


    }

    // The following two functions are for the settings menu
    // that comes by default on the top right corner
    // of your activity. We are not dealing with them this week

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.week04, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(getApplicationContext(),Week07SettingsActivity.class);
            startActivity(settingIntent);
            return true;
        }
        else if (id == R.id.refresh_button){
            refreshWeather();
        }
        else if (id == R.id.view_on_map){
            seeOnMap();
        }
        return super.onOptionsItemSelected(item);
    }

    public class getWeatherTask extends AsyncTask<String,String,String[]>{

        // You must override the this function to define what
        // the background task should do
        @Override
        protected String[] doInBackground(String... Params) {

            // The declaration of result string needs to be
            // outside of the if branching to make sure
            // we always have a result string to return
            String result = "";

            // check the network status
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // fetch data


                Log.i("week06inClass","preparing to get network data");
                result = GET("http://api.openweathermap.org/data/2.5/forecast/daily?q="+Params[0]+"&mode=json&units=metric&cnt=7");
                Log.i("week06inClass",result);
            } else {
                // We cannot make a Toast here as this is the background thread,
                // We have to publish the no network information back to
                // Main thread using publishProgress and make Toast in the onProgressUpdate
                // function below
                publishProgress("No Network Connection!");
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
            return weatherresult;
        }


        // Instead of
        protected void onPostExecute(String[] weatherresult){

            // We use the ArrayList we constructed from JSON
            // to set up the ListView.
            // NOTE!: This time We actually called the displayListView()
            // function in the onCreate() of the Activity (see it at the
            // beginning of this Activity), because we need to create the
            // ArrayAdapter before we can manipulate it here. It will still display
            // the dummy data first but let's ignore that for now, we'll get
            // rid of that later on

            myArrayAdapter.clear();
            //myArrayAdapter.addAll(weatherresult);
            for(int i=0; i<weatherresult.length;i++)
                myArrayAdapter.add(weatherresult[i]);

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

/*      NO LONGER USED AFTER ADDING JSON PARSING

        // We override this function, which is a callback on
        // the main thread to update UI when background task finishes
        // Note that the input of this function is the output of the
        // doInBackground function
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("week06inClass",s);
            ((TextView)findViewById(R.id.json_test)).setText(s);
        }
*/

        // This is the callback triggered when you call the publishProgress function
        // Note that the input of this function is the parameter you give to
        // publishProgress function when you call it
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast myToast = Toast.makeText(getApplicationContext(),values[0],Toast.LENGTH_SHORT);
            myToast.show();
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



    }

}
