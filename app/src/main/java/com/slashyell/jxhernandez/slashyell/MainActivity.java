package com.slashyell.jxhernandez.slashyell;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.johnny.myapplication.backend.yellMessageApi.model.GeoPt;
import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;

import java.util.List;


public class MainActivity extends Activity implements MessageReceiver, AllMessagesFragment.OnMessagesInteractionListener, AllRepliesFragment.OnRepliesInteractionListener {

    private static final int NUM_PAGES = 2;

    private MenuItem refreshButton;
    private MenuItem locationButton;
    private View refreshView;
    private ImageButton newYellButton;

    private ViewPager pager;
    private ScreenSlidePagerAdapter pagerAdapter;

    private LocationManager gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Setting custom action bars
        final ActionBar actionBar = getActionBar();

        pager = (ViewPager) findViewById(R.id.main_activity_pager);
        pager.setPageTransformer(false, new DepthPageTransformer());
        pager.setPageMargin(-50);
        pager.setHorizontalFadingEdgeEnabled(true);
        pager.setFadingEdgeLength(30);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                actionBar.setSelectedNavigationItem(i);
                if (i == 0) {
                    newYellButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            createNewYell();
                        }
                    });
                    newYellButton.setImageResource(R.drawable.new_yell);
                    newYellButton.invalidate();
                    locationButton.setVisible(true);
                    locationButton.setEnabled(true);
                }
                else if (i == 1) {
                    newYellButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            createNewReply();
                        }
                    });
                    newYellButton.setImageResource(R.drawable.reply);
                    newYellButton.invalidate();
                    locationButton.setVisible(false);
                    locationButton.setEnabled(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        pagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        pager.setAdapter(pagerAdapter);


        actionBar.setCustomView(R.layout.main_actionbar_top); //load your layout
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM); //show it
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); // Set navigation tabs depecrated in API 21+

        newYellButton = (ImageButton) findViewById(R.id.new_yell);
        newYellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewYell();
            }
        });

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                pager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}
        };


        // Setting the tabs just to see how they look
        actionBar.addTab(actionBar.newTab().setText("AROUND YOU")
                .setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("REPLIES")
                .setTabListener(tabListener));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        refreshButton = menu.findItem(R.id.action_refresh);
        refreshView = refreshButton.getActionView();

        locationButton = menu.findItem(R.id.action_location_found);

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
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
            return true;
        }
        if (id == R.id.action_location_found) {
            GeoPt center = MapUtils.getCurrentLocation(gps);
            if (center != null) {
                pagerAdapter.getAllMessagesFragment().locationFound(center);
            } else {
                Toast.makeText(this, getResources().getString(R.string.location_unavailable), Toast.LENGTH_LONG);
            }
            return true;
        }
        if (id == R.id.action_refresh) {
            refreshButton.setActionView(R.layout.actionbar_indeterminate_progress);
            refreshButton.setEnabled(false);
            switch(pager.getCurrentItem()) {
                case 0:
                    refreshMessages();
                    break;
                case 1:
                    refreshReplies();
                    break;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMessagesReceived(List<YellMessage> messages) {
        pagerAdapter.getAllMessagesFragment().refreshMessages(messages);
        refreshButton.setActionView(refreshView);
        refreshButton.setEnabled(true);
    }

    @Override
    public void onRepliesReceived(List<YellMessage> replies) {
        pagerAdapter.getAllRepliesFragment().updateReplies(replies);
        refreshButton.setActionView(refreshView);
        refreshButton.setEnabled(true);
    }

    @Override
    public void onMapFragmentInfoInteraction(YellMessage message) {
        // Actually get replies and display
        // pagerAdapter.getAllRepliesFragment().updateOriginalPost(message);
        pager.setCurrentItem(1, true);
    }

    @Override
    public void onMapFragmentMarkerInteraction(YellMessage message) {
        pagerAdapter.getAllRepliesFragment().updateOriginalPost(message);
    }

    @Override
    public void onMapInit() {
        refreshMessages();
    }

    @Override
    public void onReplyFragmentInteraction(Long id) {
        new GetReplies(this).execute(id);
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() != 0) {
            pager.setCurrentItem(0, true);
        } else {
            super.onBackPressed();
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        Fragment[] fragments;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new Fragment[2];
            GeoPt center = MapUtils.getCurrentLocation(gps);
            fragments[0] = AllMessagesFragment.newInstance(center.getLatitude(), center.getLongitude());
            fragments[1] = new AllRepliesFragment();


        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        public AllMessagesFragment getAllMessagesFragment() {
            return (AllMessagesFragment) fragments[0];
        }

        public AllRepliesFragment getAllRepliesFragment() {
            return (AllRepliesFragment) fragments[1];
        }
    }

    private void createNewYell() {
        Intent newYellIntent = new Intent(this, NewYellActivity.class);
        startActivity(newYellIntent);
    }

    private void createNewReply() {
        ((AllRepliesFragment) pagerAdapter.getItem(1)).createReply();
    }

    private Drawable getDrawable(String d) {
        int imageResource = getResources().getIdentifier(d, null, getPackageName());
        return getResources().getDrawable(imageResource);
    }

    private void refreshMessages() {
        GeoPt center = MapUtils.getMapLocation(pagerAdapter.getAllMessagesFragment().getExtendedMap());
        if (center != null) {
            new GetYells(this).execute(center);
        } else {
            Toast.makeText(this, getResources().getString(R.string.location_unavailable), Toast.LENGTH_LONG);
        }
    }

    private void refreshReplies() {
        new GetReplies(this).execute(pagerAdapter.getAllRepliesFragment().getOriginalPost());
    }
}
