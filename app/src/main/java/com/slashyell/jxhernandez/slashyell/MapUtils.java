package com.slashyell.jxhernandez.slashyell;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.example.johnny.myapplication.backend.yellMessageApi.model.GeoPt;
import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by johnny on 4/23/15.
 */
public final class MapUtils {

    private MapUtils() {}

    public static void addMessageToMap(GoogleMap map, YellMessage message, YellMessageWindowAdapter markerAdapter) {
        MarkerOptions pinOptions = new MarkerOptions();
        pinOptions.position(new LatLng(message.getLocation().getLatitude(), message.getLocation().getLongitude()));
        markerAdapter.setYellMessage(message);
        pinOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        map.addMarker(pinOptions);
    }

    public static GeoPt getLocation(LocationManager gps) {
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
}
