package com.example.eduardo.locmess;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public final int fileRequestID = 55;
    public final int port = 7950;
    ActionBar actionBar;
    BottomNavigationView navigation;
    FragmentTransaction transaction;
    DBHandler db = new DBHandler(this);
    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel wifichannel;
    private BroadcastReceiver wifiServerReceiver;
    private IntentFilter wifiServerReceiverIntentFilter;
    private String path;
    private File downloadTarget;
    private Intent serverServiceIntent;
    private boolean serverThreadActive;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        navigation = (BottomNavigationView) findViewById(R.id.navigationBar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, LocationFragment.newInstance());
        transaction.commit();

        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wifichannel = wifiManager.initialize(this, getMainLooper(), null);
        //wifiServerReceiver = new ServerWifiBroadcastReceiver(wifiManager, wifichannel, this);

        wifiServerReceiverIntentFilter = new IntentFilter();;
        wifiServerReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiServerReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiServerReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiServerReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        path = "/sdcard/";
        downloadTarget = new File(path);

        serverServiceIntent = null;
        serverThreadActive = false;

        Toast.makeText(getBaseContext(), "No File being transfered", Toast.LENGTH_LONG).show();

        registerReceiver(wifiServerReceiver, wifiServerReceiverIntentFilter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_wifi:
                Intent intent = new Intent(this, ClientActivity.class);
                this.startActivity(intent);
                break;
            case R.id.menu_logout:
                logoutUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wifi_direct, menu);
        return true;
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        //db.deleteuser(int);

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

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

    //WifiDirect
    public void startFileBrowseActivity(View view) {

        Intent clientStartIntent = new Intent(this, FileBrowser.class);
        startActivityForResult(clientStartIntent, fileRequestID);
        //Path returned to onActivityResult

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == fileRequestID) {
            //Fetch result
            File targetDir = (File) data.getExtras().get("file");

            if(targetDir.isDirectory())
            {
                if(targetDir.canWrite())
                {
                    downloadTarget = targetDir;
                    Toast.makeText(getBaseContext(), "Download directory set to " + targetDir.getName(), Toast.LENGTH_LONG).show();

                }
                else
                {
                    Toast.makeText(getBaseContext(), "You do not have permission to write to " + targetDir.getName(), Toast.LENGTH_LONG).show();
                }

            }
            else
            {
                Toast.makeText(getBaseContext(), "The selected file is not a directory. Please select a valid download directory", Toast.LENGTH_LONG).show();
            }

        }
    }
    public void startServer(View view) {

        //If server is already listening on port or transfering data, do not attempt to start server service
        if(!serverThreadActive)
        {
            //Create new thread, open socket, wait for connection, and transfer file

            serverServiceIntent = new Intent(this, ServerService.class);
            serverServiceIntent.putExtra("saveLocation", downloadTarget);
            serverServiceIntent.putExtra("port", new Integer(port));
            serverServiceIntent.putExtra("serverResult", new ResultReceiver(null) {
                @Override
                protected void onReceiveResult(int resultCode, final Bundle resultData) {

                    if(resultCode == port )
                    {
                        if (resultData == null) {
                            //Server service has shut down. Download may or may not have completed properly.
                            serverThreadActive = false;
                        }
                        }

                }
            });

            serverThreadActive = true;
            startService(serverServiceIntent);
        }
        else
        {
            //Set status to already running
            Toast.makeText(getBaseContext(), "The server is already running", Toast.LENGTH_LONG).show();
        }
    }
    public void stopServer(View view) {
        //stop download thread
        if(serverServiceIntent != null)
        {
            stopService(serverServiceIntent);

        }
    }
    public void startClientActivity(View view) {
        stopServer(null);
        Intent clientStartIntent = new Intent(this, ClientActivity.class);
        startActivity(clientStartIntent);
    }
    //END WifiDirect

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
