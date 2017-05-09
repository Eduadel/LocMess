package com.example.eduardo.locmess;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.eduardo.locmess.helper.SQLiteHandler;
import com.example.eduardo.locmess.helper.SessionManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActionBar actionBar;
    private SQLiteHandler db;
    private SessionManager session;
    private static final String TAG = "MainActivity";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_location:
                    actionBar.setTitle("Location");
                    selectedFragment = LocationFragment.newInstance();
                    break;
                case R.id.navigation_messages:
                    actionBar.setTitle("Messages");
                    selectedFragment = MessagesFragment.newInstance();
                    break;
                case R.id.navigation_profile:
                    actionBar.setTitle("Profile");
                    selectedFragment = ProfileFragment.newInstance();
                    break;
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_layout, selectedFragment);
            ft.commit();
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            //logoutUser();
        }

        /*
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationBar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, LocationFragment.newInstance());
        transaction.commit();
        statusCheck();
        
        if (!checkIfArraysCreated()) {createSRArray();}

        // Logout button click event
        /*btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });*/

    }
    //Location Check
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
/*    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }*/
    
     private boolean checkIfArraysCreated() {
        String[] test_file_key = {"sent_server", "sent_local", "received"};
        int n=0;

        for (String s: test_file_key) {
            String path = getFilesDir().getAbsolutePath() + "/" + s;
            File file = new File(path);
            if (file.exists()) {n++;}
        }

        if(n==3){return true;}
        else {return false;}

    }

    public void createSRArray () {
        List<PinMessage> sent_server = new ArrayList<PinMessage>();
        List<PinMessage> sent_local = new ArrayList<PinMessage>();
        List<PinMessage> received = new ArrayList<PinMessage>();
        try {
            // Save the list of entries to internal storage
            InternalStorage.writeObject(this, "sent_server", sent_server);
            InternalStorage.writeObject(this, "sent_local", sent_local);
            InternalStorage.writeObject(this, "received", received);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }
}
