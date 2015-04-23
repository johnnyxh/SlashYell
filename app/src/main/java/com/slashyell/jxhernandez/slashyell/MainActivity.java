package com.slashyell.jxhernandez.slashyell;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.johnny.myapplication.backend.yellMessageApi.YellMessageApi;
import com.example.johnny.myapplication.backend.yellMessageApi.model.GeoPt;
import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends Activity {

    private static final float ZOOM_LEVEL = 16f;

    private ImageButton newYellButton;
    private GoogleMap map;
    private LocationManager gps;
    private ListView yellList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting custom action bars
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.main_actionbar_top); //load your layout
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_SHOW_CUSTOM); //show it

        newYellButton = (ImageButton) findViewById(R.id.new_yell);
        newYellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewYell();
            }
        });

        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapview)).getMap();

        gps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        yellList = (ListView) findViewById(R.id.yellList);
        yellList.setEmptyView(findViewById(R.id.emptyElement));
        ArrayList<YellMessage> testItems = new ArrayList<YellMessage>();

        YellMessage test1 = new YellMessage();
        test1.setUserId("Johnny Hernandez");
        test1.setDate(new DateTime(new Date(), TimeZone.getTimeZone("EST")));

        GeoPt myLocation = getLocation();
        if (myLocation!=null)
            test1.setLocation(getLocation());

        test1.setMessage("Look at this amazing list its cool right?");
        testItems.add(test1);

        YellMessage test2 = new YellMessage();
        test2.setUserId("Johnny Hernandez");
        test2.setDate(new DateTime(new Date(), TimeZone.getTimeZone("EST")));
        if (myLocation!=null)
            test2.setLocation(getLocation());

        test2.setMessage("It looks like absolute trash");
        testItems.add(test2);

        ListAdapter testAdapter = new YellMessageListAdapter(this, R.layout.yell_item, testItems);
        yellList.setAdapter(testAdapter);

        // Initial animation/zoom into users position
        if (myLocation!=null)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLastKnownLocation(gps.getBestProvider(new Criteria(), true)).getLatitude(), gps.getLastKnownLocation(gps.getBestProvider(new Criteria(), true)).getLongitude()), ZOOM_LEVEL));
    }

    public GeoPt getLocation() {
        GeoPt location = new GeoPt();
        Location pos = gps.getLastKnownLocation(gps.getBestProvider(new Criteria(), true));
        if (pos == null) {
            return null;
        }
        else {
            location.setLatitude((float) pos.getLatitude());
            location.setLongitude((float) pos.getLongitude());
            return location;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void createNewYell() {
        Intent newYellIntent = new Intent(this, NewYellActivity.class);
        startActivity(newYellIntent);
    }
}
