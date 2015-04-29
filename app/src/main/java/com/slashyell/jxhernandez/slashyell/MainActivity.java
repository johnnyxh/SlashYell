package com.slashyell.jxhernandez.slashyell;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.MapFragment;
import com.androidmapsextensions.Marker;
import com.example.johnny.myapplication.backend.yellMessageApi.model.GeoPt;
import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity implements MessageReceiver{

    private static final float ZOOM_LEVEL_NORMAL = 16f;
    private static final float ZOOM_LEVEL_MESSAGE = 18f;

    private MenuItem refreshButton;
    private View refreshView;
    YellMessageWindowAdapter markerAdapter;

    private Map<Long, Marker> idMarkerMap;

    private ImageButton newYellButton;
    private GoogleMap map;
    private LocationManager gps;
    private ListView yellList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idMarkerMap = new HashMap<Long, Marker>();

        // Setting custom action bars
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.main_actionbar_top); //load your layout
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM); //show it

        newYellButton = (ImageButton) findViewById(R.id.new_yell);
        newYellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewYell();
            }
        });

        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapview)).getExtendedMap();

        markerAdapter = new YellMessageWindowAdapter(this.getLayoutInflater());
        map.setInfoWindowAdapter(markerAdapter);

        gps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        yellList = (ListView) findViewById(R.id.yellList);

        View emptyView = getLayoutInflater().inflate(R.layout.empty_yell_list,null);
        ((ViewGroup)yellList.getParent()).addView(emptyView);
        yellList.setEmptyView(emptyView);
        yellList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        yellList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YellMessage message = (YellMessage) yellList.getItemAtPosition(position);
                Marker messageMarker = idMarkerMap.get(message.getId());
                messageMarker.showInfoWindow();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(messageMarker.getPosition(), ZOOM_LEVEL_MESSAGE));
            }
        });

        GeoPt myLocation = MapUtils.getLocation(gps);

        // Initial animation/zoom into users position
        if (myLocation != null)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLastKnownLocation(gps.getBestProvider(new Criteria(), true)).getLatitude(), gps.getLastKnownLocation(gps.getBestProvider(new Criteria(), true)).getLongitude()), ZOOM_LEVEL_NORMAL));

        //new EndPointAsyncTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        refreshButton = menu.findItem(R.id.action_refresh);
        refreshView = refreshButton.getActionView();

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
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
            return true;
        }
        if (id == R.id.action_location_found) {
            // Center the map on the users position
            GeoPt current_location = MapUtils.getLocation(gps);
            if (current_location != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(current_location.getLatitude(), current_location.getLongitude()), ZOOM_LEVEL_NORMAL));
            } else {
                Toast.makeText(this, getResources().getString(R.string.location_unavailable), Toast.LENGTH_LONG);
            }
            return true;
        }
        if (id == R.id.action_refresh) {
            refreshButton.setActionView(R.layout.actionbar_indeterminate_progress);
            refreshButton.setEnabled(false);
            new GetYells(this).execute(MapUtils.getLocation(gps));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void createNewYell() {
        Intent newYellIntent = new Intent(this, NewYellActivity.class);
        startActivity(newYellIntent);
    }

    @Override
    public void onMessagesReceived(List<YellMessage> messages) {
        idMarkerMap.clear();
        if (messages != null) {
            ListAdapter newMessageAdapter = new YellMessageListAdapter(this, R.layout.yell_item, messages);
            yellList.setAdapter(newMessageAdapter);
            for (YellMessage message : messages) {
                idMarkerMap.put(message.getId(), MapUtils.addMessageToMap(map, message, markerAdapter));
            }
        } else {
            messages = new ArrayList<YellMessage>();
            ListAdapter newMessageAdapter = new YellMessageListAdapter(this, R.layout.yell_item, messages);
            yellList.setAdapter(newMessageAdapter);
        }

        refreshButton.setActionView(refreshView);
        refreshButton.setEnabled(true);
    }
}
