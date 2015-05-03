package com.slashyell.jxhernandez.slashyell;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.MapFragment;
import com.androidmapsextensions.Marker;
import com.example.johnny.myapplication.backend.yellMessageApi.model.GeoPt;
import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NewYellActivity extends Activity {

    private Marker messageMarker;

    private MenuItem sendButton;

    private Geocoder geocoder;

    private EditText yellText;
    private GoogleMap map;
    private LocationManager gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_yell);

        geocoder = new Geocoder(this, Locale.getDefault());

        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.newyell_mapview)).getExtendedMap();

        // Disallow scrolling/etc
        map.getUiSettings().setScrollGesturesEnabled(false);


        gps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final GeoPt myLocation = MapUtils.getLocation(gps);


        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            LatLng lastPos;

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (!cameraPosition.target.equals(lastPos)) {
                    map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
                    lastPos = cameraPosition.target;
                }
            }
        });

        // Setting up the character counter
        // TODO: Consider making this a custom view so I can use it elsewhere
        yellText = (EditText) findViewById(R.id.yelltext);
        final TextView charCounter = (TextView) findViewById(R.id.yell_message_counter);

        final TextWatcher mTextEditorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                charCounter.setText(String.valueOf(200 - s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        yellText.addTextChangedListener(mTextEditorWatcher);



        if (myLocation != null) {
            //TODO: Might want the ZOOM_LEVEL constant accessible somewhere else. Hardcoded right now
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 16f));
            messageMarker = MapUtils.addPreviewToMap(map, myLocation);

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


        } else {
            Toast.makeText(this, getResources().getString(R.string.location_unavailable), Toast.LENGTH_LONG);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_yell, menu);

        sendButton = menu.findItem(R.id.action_send);

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
            sendButton.setActionView(R.layout.actionbar_indeterminate_progress);
            sendButton.setEnabled(false);
            sendYell();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendYell() {

        String message = yellText.getText().toString();
        if (message.isEmpty()) {
            return;
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String displayName = sharedPref.getString(SettingsActivity.DISPLAY_NAME, "Anon");

        YellMessage yellMessage = new YellMessage();
        yellMessage.setUserId(displayName);
        GeoPt userLocation = new GeoPt();
        userLocation.setLatitude((float)messageMarker.getPosition().latitude);
        userLocation.setLongitude((float)messageMarker.getPosition().longitude);
        yellMessage.setLocation(userLocation);
        yellMessage.setMessage(message);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(messageMarker.getPosition().latitude, messageMarker.getPosition().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String location = addresses.get(0).getAddressLine(0);
            yellMessage.setTextLocation(location);
        } catch (IOException e) {
            yellMessage.setTextLocation(getResources().getString(R.string.unknown_location));
        }

        // AsyncTask will terminate this activity when finished
        new SendYell(this, yellMessage).execute();
    }
}
