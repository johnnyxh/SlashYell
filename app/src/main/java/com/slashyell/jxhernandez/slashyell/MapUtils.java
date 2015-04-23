package com.slashyell.jxhernandez.slashyell;

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

    public static void addMessageToMap(GoogleMap map, YellMessage message) {
        MarkerOptions pinOptions = new MarkerOptions();
        pinOptions.position(new LatLng(message.getLocation().getLatitude(), message.getLocation().getLongitude()));
        pinOptions.title(message.getUserId());
        pinOptions.snippet(message.getMessage());
        pinOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        map.addMarker(pinOptions);
    }
}
