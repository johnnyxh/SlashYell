package com.slashyell.jxhernandez.slashyell;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.johnny.myapplication.backend.yellMessageApi.model.GeoPt;
import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.slashyell.jxhernandez.slashyell.AllRepliesFragment.OnRepliesInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllRepliesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllRepliesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private YellMessage originalPost;

    private TextView originalPostName;
    private TextView originalPostTime;
    private TextView originalPostMessage;
    private TextView originalPostLocation;

    private ListView yellRepliesList;


    private OnRepliesInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllRepliesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllRepliesFragment newInstance(String param1, String param2) {
        AllRepliesFragment fragment = new AllRepliesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AllRepliesFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_all_replies, container, false);

        yellRepliesList = (ListView) rootView.findViewById(R.id.yellList);

        View emptyView = inflater.inflate(R.layout.empty_yell_list, null);
        ((ViewGroup)yellRepliesList.getParent()).addView(emptyView);
        yellRepliesList.setEmptyView(emptyView);
        yellRepliesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        yellRepliesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Do something on click
                // Show location of reply?
            }
        });

        originalPostName = (TextView) rootView.findViewById(R.id.reply_fragment_main_post_name);
        originalPostTime = (TextView) rootView.findViewById(R.id.reply_fragment_main_post_time);
        originalPostMessage = (TextView) rootView.findViewById(R.id.reply_fragment_main_post_message);
        originalPostLocation = (TextView) rootView.findViewById(R.id.reply_fragment_main_post_location);


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnRepliesInteractionListener) activity;
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
    public interface OnRepliesInteractionListener {
        // TODO: Update argument type and name
        public void onReplyFragmentInteraction(Long id);
    }

    public void updateOriginalPost(YellMessage message) {
        this.originalPost = message;
        originalPostName.setText(message.getUserId());
        originalPostTime.setText(DateUtils.getRelativeDateTimeString(getActivity(), message.getDate().getValue(), DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0));
        originalPostMessage.setText(message.getMessage());
        originalPostLocation.setText(message.getTextLocation());
        mListener.onReplyFragmentInteraction(message.getId());
    }

    public Long getOriginalPost() {
        if (originalPost != null) {
            return this.originalPost.getId();
        }
        return new Long(-1);
    }

    public void updateReplies(List<YellMessage> replies) {
        if (replies != null) {
            YellMessageListAdapter listAdapter = new YellMessageListAdapter(getActivity(), R.layout.yell_item, replies);
            yellRepliesList.setAdapter(listAdapter);
        } else {
            YellMessageListAdapter listAdapter = new YellMessageListAdapter(getActivity(), R.layout.yell_item, new ArrayList<YellMessage>());
            yellRepliesList.setAdapter(listAdapter);
        }
    }
    public void createReply() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        //final EditText editText = (EditText)view.findViewById(R.id.editText1);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Input a message");
        final View v = inflater.inflate(R.layout.new_reply_dialog, null);
        builder.setView(v);
        builder.setPositiveButton("POST",
                new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(originalPost == null)return;
                            EditText messageEditText = (EditText) v.findViewById(R.id.reply_dialog_message);
                            String message = messageEditText.getText().toString();
                            if (message.isEmpty()) {
                                return;
                            }

                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            String displayName = sharedPref.getString(SettingsActivity.DISPLAY_NAME, "Anon");

                            YellMessage yellMessage = new YellMessage();
                            yellMessage.setUserId(displayName);
                            /*
                            GeoPt userLocation = new GeoPt();
                            userLocation.setLatitude((float)messageMarker.getPosition().latitude);
                            userLocation.setLongitude((float)messageMarker.getPosition().longitude);
                            yellMessage.setLocation(userLocation);
                            */
                            yellMessage.setMessage(message);
                            /*
                            List<Address> addresses;
                            try {
                                addresses = geocoder.getFromLocation(messageMarker.getPosition().latitude, messageMarker.getPosition().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                String location = addresses.get(0).getAddressLine(0);
                                yellMessage.setTextLocation(location);
                            } catch (IOException e) {
                                yellMessage.setTextLocation(getResources().getString(R.string.unknown_location));
                            }
                            */
                            // AsyncTask will terminate this activity when finished
                            yellMessage.setLocation(new GeoPt().setLatitude(0f).setLongitude(0f));
                            yellMessage.setOpId(originalPost.getId());
                            new SendYell(getActivity(), yellMessage, false).execute();

                        }

                });

        builder.show();
    }

}
