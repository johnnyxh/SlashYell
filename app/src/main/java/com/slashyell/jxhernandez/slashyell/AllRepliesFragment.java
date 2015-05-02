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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.johnny.myapplication.backend.yellMessageApi.model.GeoPt;
import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;

import java.io.IOException;
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

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onReplyFragmentInteraction(uri);
        }
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
        public void onReplyFragmentInteraction(Uri uri);
    }

    public void updateOriginalPost(YellMessage message) {
        this.originalPost = message;
        Log.d("OP Post", message.getMessage());
    }

    public void updateReplies() {
        if (originalPost != null) {
            // Im gonna write the update code in a second
        }
    }
    public void createReply() {
        LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.fragment_all_replies , null);
        final EditText editText = (EditText)view.findViewById(R.id.editText1);
        new AlertDialog.Builder(getActivity().getApplicationContext())
                .setTitle("Input a message")
                .setView(view)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                .show();
    }

}
