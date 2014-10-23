package com.example.huanjiayang.week04;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class Week04Activity extends ActionBarActivity {


    // the onCreate() callBack before an Activity is created
    // See the android Activity lifecycle we showed in class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // auto generated code: set the layout for activity
        setContentView(R.layout.activity_week04);
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
