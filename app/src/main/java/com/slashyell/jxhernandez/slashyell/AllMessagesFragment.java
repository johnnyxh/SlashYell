package com.slashyell.jxhernandez.slashyell;

import android.app.Activity;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.MapFragment;
import com.androidmapsextensions.Marker;
import com.example.johnny.myapplication.backend.yellMessageApi.model.GeoPt;
import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.slashyell.jxhernandez.slashyell.AllMessagesFragment.OnMessagesInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllMessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllMessagesFragment extends MapFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_INIT_LOCATION_LONG = "arg_init_location_long";
    private static final String ARG_INIT_LOCATION_LAT = "arg_init_location_lat";

    private static final float ZOOM_LEVEL_NORMAL = 16f;
    private static final float ZOOM_LEVEL_MESSAGE = 18f;

    YellMessageWindowAdapter markerAdapter;

    private Map<Long, Marker> idMarkerMap;

    private GoogleMap map;

    private Bitmap drawableMap;

    private OnMessagesInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param longitude The inital zoom in longitude
     * @param latitude The intial zoom in latitude
     * @return A new instance of fragment AllMessagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllMessagesFragment newInstance(float latitude, float longitude) {
        AllMessagesFragment fragment = new AllMessagesFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_INIT_LOCATION_LAT, latitude);
        args.putFloat(ARG_INIT_LOCATION_LONG, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    public AllMessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        idMarkerMap = new HashMap<Long, Marker>();

        map = this.getExtendedMap();

        markerAdapter = new YellMessageWindowAdapter(inflater);
        map.setInfoWindowAdapter(markerAdapter);

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                mListener.onMapFragmentInfoInteraction((YellMessage) marker.getData());
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mListener.onMapFragmentMarkerInteraction((YellMessage) marker.getData());
                return false;
            }
        });

        mListener.onMapInit();

        // Initial animation/zoom into users position
        if (getArguments() != null)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getArguments().getFloat(ARG_INIT_LOCATION_LAT), getArguments().getFloat(ARG_INIT_LOCATION_LONG)), ZOOM_LEVEL_NORMAL));

        return rootView;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMessagesInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMessagesInteractionListener {
        // TODO: Update argument type and name
        public void onMapFragmentInfoInteraction(YellMessage message);
        public void onMapFragmentMarkerInteraction(YellMessage message);
        public void onMapInit();
    }

    public void locationFound(GeoPt center) {
        if (center != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(center.getLatitude(), center.getLongitude()), ZOOM_LEVEL_NORMAL));
        } else {
            Toast.makeText(this.getActivity(), getResources().getString(R.string.location_unavailable), Toast.LENGTH_LONG);
        }
    }

    public void refreshMessages(List<YellMessage> messages) {
        idMarkerMap.clear();
        if (messages != null) {
            for (YellMessage message : messages) {
                idMarkerMap.put(message.getId(), MapUtils.addMessageToMap(map, message, markerAdapter));
            }
        } else {
            Toast.makeText(this.getActivity(), R.string.post_failure, Toast.LENGTH_LONG).show();
        }
    }

}
