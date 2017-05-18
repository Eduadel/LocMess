package com.example.eduardo.locmess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.GroupInfoListener;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

public class MainActivity extends AppCompatActivity implements
        PeerListListener, GroupInfoListener {

    private static final String TAG = "MainActivity";
    public final int fileRequestID = 55;
    public final int port = 7950;
    ActionBar actionBar;
    BottomNavigationView navigation;
    FragmentTransaction transaction;
    DBHandler db = new DBHandler(this);
    static List<String> peersStr = new ArrayList<String>();
    
    //Termite and Wifi-Direct
    private static SimWifiP2pBroadcastReceiver mReceiver;
    private static SimWifiP2pManager mManager = null;
    private static SimWifiP2pManager.Channel mChannel = null;
    private static Messenger mService = null;
    private boolean mBound = false;
    private static SimWifiP2pSocketServer mSrvSocket = null;
    private static SimWifiP2pSocket mCliSocket = null;
    
    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel wifichannel;
    private BroadcastReceiver wifiServerReceiver;
    private IntentFilter wifiServerReceiverIntentFilter;
    private String path;
    private File downloadTarget;
    private Intent serverServiceIntent;
    private boolean serverThreadActive;
    SessionManager session;

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

        session = new SessionManager(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        navigation = (BottomNavigationView) findViewById(R.id.navigationBar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, LocationFragment.newInstance());
        transaction.commit();
        statusCheck();

       if(!checkIfArraysCreated()){createSRArray();}

        //Termite related ...............................................................
        // initialize the WDSim API
        SimWifiP2pSocketManager.Init(this.getApplicationContext());

        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);

        setWifiOn();
        
       /* wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
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

        registerReceiver(wifiServerReceiver, wifiServerReceiverIntentFilter);*/

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
    private void logoutUser() {
        //db.deleteuser(int);

        // logout user
        session.logoutUser();

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
    
    public void termiteSender(PinMessage p) {
        mManager.requestGroupInfo(mChannel, MainActivity.this);
        for(String st : peersStr){
            Log.i(TAG, st);
            new OutgoingCommTask().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        st);
            new SendCommTask().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        p);
            Log.i(TAG, "finished for loop");
        }
    }

    public class IncommingCommTask extends AsyncTask<Void, PinMessage, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "IncommingCommTask started (" + this.hashCode() + ").");

            try {
                mSrvSocket = new SimWifiP2pSocketServer(
                        Integer.parseInt(getString(R.string.port)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    try {
                        ObjectInput in = new ObjectInputStream(sock.getInputStream());
                        PinMessage p = (PinMessage) in.readObject();
                        publishProgress(p);
                        sock.getOutputStream().write(("\n").getBytes());
                    } catch (IOException e) {
                        Log.i(TAG, "Error reading socket:");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        sock.close();
                    }
                } catch (IOException e) {
                    Log.d("Error socket:", e.getMessage());
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(PinMessage... values) {
            Log.i(TAG, "Message Received");
            PinMessage received_message = values[0];

            //Show Alert to user
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom);
            builder.setTitle(getResources().getString(R.string.title));
            builder.setMessage(values[0].getContent());
            builder.show();
            //Add Message to cache
            addMessage(values[0]);

        }
    }

    private void addMessage(PinMessage value){
        String key = "received";
        try {
            // Retrieve the list from internal storage
            List<PinMessage> entries = (List<PinMessage>) InternalStorage.readObject(this, key);

            //Add message to array
            entries.add(value);

            //Delete previous copy
            String path = getFilesDir().getAbsolutePath() + "/" + key;
            File file = new File(path);
            file.delete();

            //Save updated version to cache
            InternalStorage.writeObject(this, key, entries);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public class OutgoingCommTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                mCliSocket = new SimWifiP2pSocket(params[0],
                        Integer.parseInt("10001"));
            } catch (UnknownHostException e) {
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                return "IO error:" + e.getMessage();
            }
            return null;
        }
    }

    public class SendCommTask extends AsyncTask<PinMessage, String, Void> {

        @Override
        protected Void doInBackground(PinMessage... msg) {
            try {
                if (mCliSocket!=null) {
                    ObjectOutput out = new ObjectOutputStream(mCliSocket.getOutputStream()); //write(SerializationUtils.serialize(msg[0]));
                    out.writeObject(msg[0]);
                    BufferedReader sockIn = new BufferedReader(
                            new InputStreamReader(mCliSocket.getInputStream()));
                    sockIn.readLine();
                    out.flush();
                    mCliSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCliSocket = null;
            return null;
        }
    }

    public void setWifiOn(){
        Intent intent = new Intent(MainActivity.this, SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;

        // spawn the chat server background task
        new IncommingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
        }
    };

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        StringBuilder peersStr = new StringBuilder();
        Log.i(TAG, "onPeersAvailable");
        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            peersStr.append(devstr);
        }
    }
    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices,
                                     SimWifiP2pInfo groupInfo) {
        Log.i(TAG, "onGroupInfoAvailable");
        // compile list of network members
        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            String devstr = "" + device.getVirtIp();
            Log.i(TAG, devstr);
            peersStr.add(devstr);
        }
    }


    /*
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
    }*/
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
