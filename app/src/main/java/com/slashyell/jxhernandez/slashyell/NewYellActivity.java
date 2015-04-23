package com.slashyell.jxhernandez.slashyell;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
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

        yellText = (EditText) findViewById(R.id.yelltext);

        gps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
        yellMessage.setLocation();
        yellMessage.setMessage(message);
        yellMessage.setDate(new DateTime(new Date()));

        SendYell sender = new SendYell();
        sender.doInBackground(yellMessage);
    }
}
