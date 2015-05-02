package com.slashyell.jxhernandez.slashyell;

import android.app.Activity;

import android.app.Fragment;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.slashyell.jxhernandez.slashyell.AllMessagesFragment.OnMessagesInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllMessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllMessagesFragment extends MapFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final float ZOOM_LEVEL_NORMAL = 16f;
    private static final float ZOOM_LEVEL_MESSAGE = 18f;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    YellMessageWindowAdapter markerAdapter;

    private Map<Long, Marker> idMarkerMap;

    private GoogleMap map;

    private OnMessagesInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllMessagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllMessagesFragment newInstance(String param1, String param2) {
        AllMessagesFragment fragment = new AllMessagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AllMessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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
                mListener.onMapFragmentInteraction((YellMessage) marker.getData());
            }
        });

        /*
        // Initial animation/zoom into users position
        if (myLocation != null)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLastKnownLocation(gps.getBestProvider(new Criteria(), true)).getLatitude(), gps.getLastKnownLocation(gps.getBestProvider(new Criteria(), true)).getLongitude()), ZOOM_LEVEL_NORMAL));

        //new EndPointAsyncTask().execute();
        */

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
        public void onMapFragmentInteraction(YellMessage message);
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
