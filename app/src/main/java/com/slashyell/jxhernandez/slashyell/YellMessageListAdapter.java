package com.slashyell.jxhernandez.slashyell;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by johnny on 4/21/15.
 */
public class YellMessageListAdapter extends ArrayAdapter<YellMessage> {

    public YellMessageListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public YellMessageListAdapter(Context context, int resource, List<YellMessage> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.yell_item, null);

        }

        YellMessage p = getItem(position);

        if (p != null) {

            TextView nameField = (TextView) v.findViewById(R.id.yell_item_name);
            TextView timeField = (TextView) v.findViewById(R.id.yell_item_time);
            TextView locationField = (TextView) v.findViewById(R.id.yell_item_location);
            TextView commentField = (TextView) v.findViewById(R.id.yell_item_comment);

            if (nameField != null) {
                nameField.setText(p.getUserId());
            }
            if (timeField != null) {
                timeField.setText(DateUtils.getRelativeDateTimeString(this.getContext(), p.getDate().getValue(), DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0));
            }
            if (locationField != null) {

                locationField.setText(p.getTextLocation());
            }
            if (commentField != null) {
                commentField.setText(p.getMessage());
            }

        }

        return v;

    }
}
