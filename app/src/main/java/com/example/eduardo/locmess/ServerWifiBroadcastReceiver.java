package com.example.eduardo.locmess;

/**
 * Created by Eduardo on 11/05/2017.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.widget.Toast;

public class ServerWifiBroadcastReceiver extends BroadcastReceiver{

    private WifiP2pManager manager;
    private Channel channel;
    private MainActivity activity;

    public ServerWifiBroadcastReceiver(WifiP2pManager manager, Channel channel,MainActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;

        Toast.makeText(activity, "Server Broadcast Receiver created", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(activity, "Wifi Direct is enabled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, "Wifi Direct is disabled", Toast.LENGTH_LONG).show();
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkState = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkState.isConnected())
            {
                Toast.makeText(activity, "Connection Status: Connected", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(activity, "Connection Status: Disconnected", Toast.LENGTH_LONG).show();
                manager.cancelConnect(channel, null);

            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}