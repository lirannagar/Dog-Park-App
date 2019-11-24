package com.app.dogsapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dogsapp.R;
import com.app.dogsapp.fragments.InfoFragment;
import com.app.dogsapp.fragments.MainFragment;
import com.app.dogsapp.fragments.ParkInfoFragment;
import com.app.dogsapp.fragments.ParksFragment;
import com.app.dogsapp.fragments.RelationsFragment;
import com.app.dogsapp.fragments.SettingsFragment;
import com.app.dogsapp.interfaces.ProgressListener;
import com.app.dogsapp.objects.Friend;
import com.app.dogsapp.objects.NearbyUser;
import com.app.dogsapp.objects.Park;
import com.app.dogsapp.services.LocationTrack;
import com.app.dogsapp.tools.Database;
import com.app.dogsapp.tools.Networking;
import com.app.dogsapp.tools.Storage;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements ProgressListener, ParkInfoFragment.ParkInfoFragmentListener, MainFragment.MainFragmentListener, SettingsFragment.SettingsFragmentListener, RelationsFragment.RelationsFragmentListener, ParksFragment.ParksFragmentListener {

    Toolbar toolbar;

    private Database db;
    private String userID = "";

    LocationTrack locationTrack;

    final int LOCATION_PERMISSION = 22;

    private FrameLayout progressView;
    private TextView progressMessage;
    private ProgressBar progressBar;
    private Storage storage;
    private ImageButton refreshBtn;
    private Snackbar snackbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        db = new Database();
        storage = new Storage();

        progressView = findViewById(R.id.progress_view);
        progressMessage = findViewById(R.id.progress_message);
        progressBar = findViewById(R.id.progressBar);

        refreshBtn = findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshFragment();
            }
        });


        //Init firebase authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //if there is a user logged in
        if (currentUser != null) {
            //get the id
            userID = currentUser.getUid();

            //Check for Fine Location permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION);

            } else {

                //If the app has the permission the get the location
                getLocation();
            }

        }
        //If there is no user logged in then start the log in activity
        else {
            startActivity(new Intent(this, LogInActivity.class));
            finish();
        }


        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });


        if (savedInstanceState == null) {
            MainFragment fragment = new MainFragment();
            fragment.setOnUserSelectedListener(this);
            fragment.setProgressListener(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                showSettings(false);
                return true;
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LogInActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.refresh_menu_btn:
                refreshFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Refresh the current fragment
    public void refreshFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        getSupportFragmentManager().beginTransaction()
                .detach(fragment)
                .attach(fragment)
                .commit();
    }


    //Starts the location service for getting the users current location
    public void getLocation() {
        locationTrack = new LocationTrack(MainActivity.this, db, userID);

        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();

        } else {
            locationTrack.showSettingsAlert();
        }
    }


    //Handles the result after the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {

                }
                return;
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Stop the location track service when the app is closed
        if (locationTrack != null)
            locationTrack.stopListener();
    }

    public Database getDb() {
        return db;
    }

    public Storage getStorage() {
        return storage;
    }

    public String getUserID() {
        return userID;
    }


    public void onBack() {
        //If there is an fragment in the list,then show the previous one otherwise close the app
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            getSupportFragmentManager().popBackStackImmediate();
            refreshFragment();
        } else {

                super.onBackPressed();

        }
    }


    @Override
    public void onBackPressed() {
        onBack();

    }

    @Override
    public void onUserSelected(NearbyUser user) {
        InfoFragment fragment = new InfoFragment();
        fragment.setProgressListener(this);
        Bundle args = new Bundle();
        args.putString("userID", user.getId());
        args.putInt("friendship", user.getFriend());
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, fragment)
                .addToBackStack("info")
                .commit();
    }

    @Override
    public void onUserDoesntExist() {
        showSettings(true);
    }

    @Override
    public void onShowFriends() {
        RelationsFragment fragment = new RelationsFragment();
        fragment.setRelationsFragmentListener(this);
        fragment.setProgressListener(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, fragment)
                .addToBackStack("relations")
                .commit();
    }

    @Override
    public void onShowParks() {
        ParksFragment fragment = new ParksFragment();
        fragment.setParksFragmentListener(this);
        fragment.setProgressListener(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, fragment)
                .addToBackStack("parks")
                .commit();
    }


    public void showSettings(boolean newUser) {
        SettingsFragment fragment = new SettingsFragment();
        fragment.setProgressListener(this);
        fragment.setSettingsFragmentListener(this);
        if (newUser) {
            Bundle args = new Bundle();
            args.putBoolean("newUser", true);
            fragment.setArguments(args);
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, fragment)
                .addToBackStack("settings")
                .commit();
    }

    @Override
    public void onSaved() {
        //Clear the backstack
        for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
            getSupportFragmentManager().popBackStack();
        }
        MainFragment fragment = new MainFragment();
        fragment.setProgressListener(this);
        fragment.setOnUserSelectedListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, fragment)
                .commit();
    }

    @Override
    public void onFriendSelected(Friend friend) {
        InfoFragment fragment = new InfoFragment();
        fragment.setProgressListener(this);
        Bundle args = new Bundle();
        args.putString("userID", friend.getId());

        if (friend.isFriend())
            args.putInt("friendship", 1);
        else
            args.putInt("friendship", 0);

        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, fragment)
                .addToBackStack("info")
                .commit();
    }

    @Override
    public void onParkSelected(Park park) {
        ParkInfoFragment fragment = new ParkInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("park", park);
        fragment.setProgressListener(this);
        fragment.setOnUserInParkSelectedListener(this);


        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, fragment)
                .addToBackStack("parkInfo")
                .commit();
    }

    @Override
    public void setOnUserInParkSelected(NearbyUser user) {
        onUserSelected(user);
    }


    //Progress listener functions
    @Override
    public void loadingFinished() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                refreshBtn.setVisibility(View.GONE);
                progressView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void loadingStarted() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (!Networking.isNetworkConnected(getApplicationContext())) {
                    showNoInternetConenction();
                } else {
                    refreshBtn.setVisibility(View.GONE);
                    if (snackbar != null)
                        snackbar.dismiss();
                    progressView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressMessage.setText(getApplication().getString(R.string.loading_msg));
                }
            }
        });
    }

    @Override
    public void showNoInternetConenction() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                showError("No internet connection,please try again.");

                refreshBtn.setVisibility(View.VISIBLE);
                snackbar = Snackbar.make(progressView, "Please check your internet connection.", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Go to settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                snackbar.show();

            }
        });
    }

    @Override
    public void showError(final String error) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressView.setVisibility(View.VISIBLE);
                refreshBtn.setVisibility(View.VISIBLE);
                progressMessage.setText(error);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void showToastMessage(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                refreshBtn.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void showGeneralError() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressView.setVisibility(View.VISIBLE);
                refreshBtn.setVisibility(View.VISIBLE);
                progressMessage.setText(getApplication().getString(R.string.error_message));
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


    }

}
