package com.example.huanjiayang.Week08SQLiteSimpleDemo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class Week05WeatherDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w05_detail_weather);

        // We always add the fragment in this activity
        getSupportFragmentManager().beginTransaction()
                .add(R.id.weather_detail_container, new DetailFragment())
                .commit();
/*
        Intent theIntent = getIntent();
        String theExtra = "";

        if(theIntent.hasExtra("weatherdetail")){
            theExtra = theIntent.getStringExtra("weatherdetail");
        }else{
            theExtra = "no data found in extra";
        }

        ((TextView)findViewById(R.id.weather_detail_text)).setText(theExtra);
        */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.w05_detail_weather, menu);
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
