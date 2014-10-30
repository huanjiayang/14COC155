package com.example.huanjiayang.week05extras;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Week04Activity extends ActionBarActivity {


    //EXTRA MILE ONLY: declare the ArrayAdapter to
    // be used with the ListView
    public ArrayAdapter<String> myArrayAdapter;

    // the onCreate() callBack before an Activity is created
    // See the android Activity lifecycle we showed in class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // auto generated code: set the layout for activity
        setContentView(R.layout.activity_week04);
    }

    // The function triggered by the new button added in week 05
    // starts an implicit Intent to view a geo location
    public void seeOnMap(View view){
        // construct the Uri that contains the geo scheme location information
        // the longitude and latitude are given as 0 ,0 to indication no coordinate info
        // then put the address in the query part of the Uri. See Google developer
        // Website for information.
        String uriString = getString(R.string.geo_uri_without_coord) + ((EditText)findViewById(R.id.edit_message)).getText().toString();
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
    public void refreshWeather(View view){
        // Initialize the three arguments to be used for Toast
        Context context = getApplicationContext();
        CharSequence text = "Refreshing weather!";
        int duration = Toast.LENGTH_SHORT;

        // Instantiate a Toast object with the parameters
        Toast toast = Toast.makeText(context, text, duration);
        // use .show() of a Toast object to show it
        toast.show();
        //Call the helper function that constructs the ListView
        displayListView();

    }

    // EXTRA MILE ONLY: The function that fills the ListView
    public void displayListView(){

        // put raw data in an ArrayList
        ArrayList myArrayList = new ArrayList();
        myArrayList.add("Today - Storm 8 / 12");
        myArrayList.add("Tomorrow - Foggy 9 / 13");
        myArrayList.add("Thurs - Rainy 8 / 13");
        myArrayList.add("Fri - foggy 8 / 12");
        myArrayList.add("Sat - Sunny 9 / 14");
        myArrayList.add("Sun - Sunny 10 / 15");
        myArrayList.add("Mon - Sunny 11 / 15");

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
                //Create an Explicit Intent with the target Activity class named
                Intent myIntent = new Intent(getApplicationContext(),Week05WeatherDetailActivity.class);
                //Put some extra data in the form of a key-value pair with the Intent
                myIntent.putExtra("weatherdetail",myString);
                //start Activity with the Intent
                startActivity(myIntent);

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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
