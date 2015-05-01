package com.slashyell.jxhernandez.slashyell;

import android.location.Address;
import android.location.Geocoder;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by johnny on 4/23/15.
 */
public class YellMessageWindowAdapter implements com.androidmapsextensions.GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater;

    public YellMessageWindowAdapter(LayoutInflater inflater) {

        this.inflater = inflater;
    }

    @Override
    public View getInfoContents(com.androidmapsextensions.Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(com.androidmapsextensions.Marker marker) {
        View v = inflater.inflate(R.layout.yell_infowindow, null);

        YellMessage message = marker.getData();

        TextView nameField = (TextView) v.findViewById(R.id.yell_window_name);
        TextView timeField = (TextView) v.findViewById(R.id.yell_window_time);
        TextView locationField = (TextView) v.findViewById(R.id.yell_window_location);
        TextView commentField = (TextView) v.findViewById(R.id.yell_window_comment);

        nameField.setText(message.getUserId());
        timeField.setText(DateUtils.getRelativeDateTimeString(v.getContext(), message.getDate().getValue(), DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0));
        locationField.setText(message.getTextLocation());
        commentField.setText(message.getMessage());

        return v;
    }
}
