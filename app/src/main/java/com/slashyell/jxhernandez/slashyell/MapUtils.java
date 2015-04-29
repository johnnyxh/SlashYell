package com.slashyell.jxhernandez.slashyell;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.androidmapsextensions.CircleOptions;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.example.johnny.myapplication.backend.yellMessageApi.model.GeoPt;
import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by johnny on 4/23/15.
 */
public final class MapUtils {

    private MapUtils() {}

    public static Marker addMessageToMap(GoogleMap map, YellMessage message, YellMessageWindowAdapter markerAdapter) {
        MarkerOptions pinOptions = new MarkerOptions();
        pinOptions.position(new LatLng(message.getLocation().getLatitude(), message.getLocation().getLongitude()));
        pinOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        Marker marker = map.addMarker(pinOptions);
        marker.setData(message);

        return marker;
    }

    public static Marker addPreviewToMap(GoogleMap map, GeoPt location) {
        CircleOptions dragRadius = new CircleOptions();
        dragRadius.center(new LatLng(location.getLatitude(), location.getLongitude()));
        //TODO: Remove hardcoded numbers. Radius is in meters keep that in mind.
        dragRadius.radius(50);
        dragRadius.fillColor(Color.argb(130, 133, 151, 255));
        dragRadius.strokeColor(Color.argb(255, 0, 38, 255));
        dragRadius.strokeWidth(2f);

        map.addCircle(dragRadius);

        MarkerOptions pinOptions = new MarkerOptions();
        pinOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
        pinOptions.draggable(true);
        pinOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        return map.addMarker(pinOptions);
    }

    public static LatLng reduceDistanceBetween(Location center, Location current, float maxDistance) {
        float currentDistance = center.distanceTo(current);
        //TODO: Hardcoded...
        float percentageChange = (maxDistance-1)/currentDistance;
        double newLat = ((current.getLatitude() - center.getLatitude())*percentageChange) + center.getLatitude();
        double newLon = ((current.getLongitude() - center.getLongitude())*percentageChange) + center.getLongitude();
        LatLng newLoc = new LatLng(newLat, newLon);
        return newLoc;
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
