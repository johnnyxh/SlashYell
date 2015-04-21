package com.slashyell.jxhernandez.slashyell;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.johnny.myapplication.backend.messaging.Messaging;
import com.example.johnny.myapplication.backend.registration.Registration;
import com.example.johnny.myapplication.backend.yellMessageApi.YellMessageApi;
import com.example.johnny.myapplication.backend.yellMessageApi.YellMessageApiRequest;
import com.example.johnny.myapplication.backend.yellMessageApi.YellMessageApiRequestInitializer;
import com.example.johnny.myapplication.backend.yellMessageApi.YellMessageApiScopes;
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
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private static final float ZOOM_LEVEL = 16f;

    private GoogleMap map;
    private LocationManager gps;
    private ListView yellList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapview)).getMap();

        yellList = (ListView) findViewById(R.id.yellList);
        yellList.setEmptyView(findViewById(R.id.emptyElement));
        ArrayList<String> testItems = new ArrayList<String>();
        testItems.add("Test Item #1");
        testItems.add("Test Item #2");
        ListAdapter testAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testItems);
        yellList.setAdapter(testAdapter);

        gps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Initial animation/zoom into users position
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLastKnownLocation(gps.getBestProvider(new Criteria(), true)).getLatitude(), gps.getLastKnownLocation(gps.getBestProvider(new Criteria(), true)).getLongitude()), ZOOM_LEVEL));

        //new EndPointAsyncTask().execute();
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

    private class EndPointAsyncTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            YellMessageApi.Builder builder = new YellMessageApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });

            YellMessageApi myApi = builder.build();

            try {
                YellMessage message = new YellMessage();
                GeoPt location = new GeoPt();
                message.setId(new Long(25));
                location.setLongitude(14.34782f);
                location.setLatitude(10.34782f);
                message.setLocation(location);
                message.setMessage("DOES IT WORK?");
                myApi.insert(message).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
