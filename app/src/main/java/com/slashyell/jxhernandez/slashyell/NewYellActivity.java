package com.slashyell.jxhernandez.slashyell;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.johnny.myapplication.backend.yellMessageApi.model.GeoPt;
import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.api.client.util.DateTime;

import java.util.Date;


public class NewYellActivity extends Activity {

    private EditText yellText;
    private GoogleMap map;
    private LocationManager gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_yell);

        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapview)).getMap();

        // Disallow scrolling/zooming/etc
        map.getUiSettings().setAllGesturesEnabled(false);

        yellText = (EditText) findViewById(R.id.yelltext);

        gps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final GeoPt myLocation = MapUtils.getLocation(gps);

        if (myLocation != null) {
            //TODO: Might want the ZOOM_LEVEL constant accessible somewhere else. Hardcoded right now
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 16f));
            MapUtils.addPreviewToMap(map, myLocation);

            // Disallow dragging marker outside of correctional radius
            //TODO: Clean this up
            final Location center = new Location("Center");
            center.setLatitude(myLocation.getLatitude());
            center.setLongitude((myLocation.getLongitude()));
            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                @Override
                public void onMarkerDragStart(Marker marker) {}

                @Override
                public void onMarkerDrag(Marker marker) {}

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    LatLng currentPos = marker.getPosition();
                    Location currentLoc = new Location("Current Location");
                    currentLoc.setLatitude(currentPos.latitude);
                    currentLoc.setLongitude(currentPos.longitude);
                    //TODO: Remove the hardcoded stuff
                    if (center.distanceTo(currentLoc) > 50) {
                        marker.setPosition(MapUtils.reduceDistanceBetween(center, currentLoc, 50));
                    }
                }
            });


        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_yell, menu);
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
        } else if (id == R.id.action_send) {
            sendYell();
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendYell() {

        String message = yellText.getText().toString();
        if (message.isEmpty()) {
            return;
        }

        YellMessage yellMessage = new YellMessage();
        yellMessage.setLocation(MapUtils.getLocation(gps));
        yellMessage.setMessage(message);
        yellMessage.setDate(new DateTime(new Date()));

        new SendYell(yellMessage).execute();

        this.finish();
    }
}
