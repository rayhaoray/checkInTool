package com.zlei.checkInTool;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionM.ActivityType;
import com.sessionm.api.ext.SessionM;
import com.sessionm.api.ext.SessionMFragment;
import com.sessionm.core.Config;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.zlei.ble.DeviceScanActivity;

import br.com.condesales.EasyFoursquareAsync;
import br.com.condesales.listeners.AccessTokenRequestListener;
import br.com.condesales.listeners.ImageRequestListener;
import br.com.condesales.listeners.UserInfoRequestListener;
import br.com.condesales.models.User;
import br.com.condesales.tasks.users.UserImageRequest;

public class MainActivity extends Activity implements
        AccessTokenRequestListener, ImageRequestListener {
    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private SlidingUpPanelLayout mLayout;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mListTitles;
    private boolean startFromNotification;

    public static FragmentManager fragmentManager;

    private static EasyFoursquareAsync async;

    private static com.sessionm.api.ext.SessionM sessionM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getFragmentManager();

        sessionM = com.sessionm.api.ext.SessionM.getInstance();

        //Config config = sessionM.getConfig();
        //Config.ServerType type = Config.ServerType.aC;
        //config.setServerType(type);

        mTitle = mDrawerTitle = getTitle();
        mListTitles = getResources().getStringArray(R.array.lists_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new MyArrayAdapter(this,
                R.layout.drawer_list_item, mListTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            startFromNotification = bundle.getBoolean("startFromNotification");
        }
        if (savedInstanceState == null) {
            selectItem(0);
        }

        // ask for access
        async = new EasyFoursquareAsync(this);
        //async.requestAccess(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionM.getInstance().onActivityStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SessionM.getInstance().onActivityResume(this);
        if (startFromNotification) {
            SessionM.getInstance().presentActivity(ActivityType.PORTAL);
            startFromNotification = false;
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SessionM.getInstance().onActivityPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SessionM.getInstance().onActivityStop(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ListFragment.ARG_NUMBER, position);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        switch (position) {
            case 0:
                fragment = new SessionMFragment();
                break;
            case 1:
                Intent i1 = new Intent(this, VenuesActivity.class);
                startActivity(i1);
                break;
            case 2:
                Intent i2 = new Intent(this, MVenuesActivity.class);
                startActivity(i2);
                break;
            case 3:
                sessionM.presentActivity(ActivityType.PORTAL);
                break;
            case 4:
                Intent i3 = new Intent(this, AccountActivity.class);
                startActivity(i3);
                break;
            case 5:
                fragment = new MapCheckinFragment();
                break;
            case 6:
                Intent i5 = new Intent(this, DeviceScanActivity.class);
                startActivity(i5);
                break;
            default:
                break;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction().replace(R.id.main_content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setId(position);
        setTitle(mListTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onBackPressed() {
        int position = mDrawerList.getId();
        if (allowBackPressed(position)) { // and then you define a method allowBackPressed with the logic to allow back pressed or not
            super.onBackPressed();
        }
        else
            this.finish();
    }

    private boolean allowBackPressed(int position){
        return position == 5;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class ListFragment extends Fragment {
        public static final String ARG_NUMBER = "arg_number";

        public ListFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int i = getArguments().getInt(ARG_NUMBER);
            View rootView = inflater.inflate(R.layout.activity_main,
                    container, false);

            return rootView;
        }
    }

    public class MyArrayAdapter extends ArrayAdapter<String> {
        private final int resource;
        private final Context context;
        private final String[] objects;

        public MyArrayAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            this.resource = resource;
            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(resource, parent,
                    false);
            TextView textView = (TextView) rowView.findViewById(R.id.list_text);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.list_icon);
            textView.setText(objects[position]);
            String s = objects[position];

            imageView.setImageResource(R.drawable.ic_launcher);
            return rowView;
        }
    }

    @Override
    public void onError(String errorMsg) {
    }

    @Override
    public void onImageFetched(Bitmap bmp) {
    }

    @Override
    public void onAccessGrant(String accessToken) {
        // with the access token you can perform any request to foursquare.
        async.getUserInfo(new UserInfoRequestListener() {

            @Override
            public void onError(String errorMsg) {
                // Some error getting user info
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onUserInfoFetched(User user) {
                // OWww. did i already got user!?
                if (user.getBitmapPhoto() == null) {
                    UserImageRequest request = new UserImageRequest(
                            MainActivity.this, MainActivity.this);
                    request.execute(user.getPhoto());
                } else {
                }
            }
        });
    }

    public static EasyFoursquareAsync getAsync() {
        return async;
    }

}
