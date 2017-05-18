package com.example.eduardo.locmess;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.eduardo.locmess.DBHandler.KEY_GPS;
import static com.example.eduardo.locmess.DBHandler.KEY_LOCAL;

public class MessagesFragment extends Fragment {
    private static final String TAG = "MessagesFragment";
    List<Map<String, Map<String, Integer>>> messages = new ArrayList<Map<String, Map<String, Integer>>>();
    ListView lv;
    ArrayAdapter<String> adapter;

    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_messages, container, false);

        lv = (ListView) rootView.findViewById(R.id.messagesListView);
        adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, messages);
        lv.setAdapter(adapter);

        atualizeList();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = messages.get(position).toString();
                Toast.makeText(getActivity(), title, Toast.LENGTH_SHORT).show();

                //Call ShowMessage Activity
                Intent i = new Intent(view.getContext(), ShowMessage.class);
                Intent i_plus = addExtraToIntent(position, i);
                startActivity(i_plus);
            }
        });

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    deleteMessage(position);
                                    messages.remove(position);
                                }


                            }
                        });
        lv.setOnTouchListener(touchListener);

        FloatingActionButton new_message_view = (FloatingActionButton) rootView.findViewById(R.id.new_message);
        new_message_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Call CreateMessage Activity
                Intent i = new Intent(view.getContext(), CreateMessage.class);
                startActivity(i);
            }
        });

        return rootView;
    }

    //Adds the messages lists name (Sent Local, Sent Server or Received) and message ID to the intent that will
    //be sent to the ShowMessage activity
    public Intent addExtraToIntent(int position, Intent i){
        String[] list = {"sent_local", "sent_server", "received"};
        String list_name="";
        int the_one = 3, comp=0;

        Map<String, Map<String,Integer>> out_map = messages.get(position);
        Map<String,Integer> in_map = null;

        for (String key: out_map.keySet()){
            in_map = out_map.get(key);
        }

        for (int o=0; o<3; o++){
            String ref = list[o];
            if(in_map.containsKey(ref)){
                comp = in_map.get(ref);
                the_one = comp;
                list_name=ref;
            }
        }

        i.putExtra("list", list_name);
        i.putExtra("id", the_one);

        return i;
    }

    //Deletes the selected message from cache
    public void deleteMessage(int position) {
        String[] list = {"sent_local", "sent_server", "received"};
        int the_one = 3, comp=0;

        Map<String, Map<String,Integer>> out_map = messages.get(position);
        Map<String,Integer> in_map = null;

        for (String key: out_map.keySet()){
            in_map = out_map.get(key);
        }

        for (int o=0; o<3; o++){
            String ref = list[o];
            if(in_map.containsKey(ref)){
                comp = in_map.get(ref);
                the_one = o;
            }
        }

        try {
            //Get messages list from cache
            List<PinMessage> msg_list = (List<PinMessage>) InternalStorage.readObject(this.getContext(), list[the_one]);

            //Find the message selected for deletion
            Iterator<PinMessage> iterator = msg_list.iterator();
            while (iterator.hasNext()) {
                PinMessage c = iterator.next();
                Log.d("ID:", String.valueOf(c.getID()));
                if (c.getID() == comp) {
                    iterator.remove();
                }
            }

            //Delete previous copy
            String path = getContext().getFilesDir().getAbsolutePath() + "/" + list[the_one]; //PROBLEM
            File file = new File(path);
            file.delete();

            //Add updated copy to cache
            InternalStorage.writeObject(this.getContext(), list[the_one], msg_list);

            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.toString());
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        atualizeList();
    }

    //Atualize the ListView with the messages from the sent and received lists
    public void atualizeList(){
        int o = 0;
        messages.clear();

        String[] list = {"sent_local", "sent_server", "received"};
        String[] name_msg = {"Sent Message ", "Sent Message ", "Received Message "};

        try {
            List<PinMessage> sent_S = (List<PinMessage>) InternalStorage.readObject(this.getContext(), "sent_server");
            List<PinMessage> sent_L = (List<PinMessage>) InternalStorage.readObject(this.getContext(), "sent_local");
            List<PinMessage> received = (List<PinMessage>) InternalStorage.readObject(this.getContext(), "received");

            List<PinMessage> entry = null;
            int k=1;

            for (int i=0; i<3; i++){
                if (i==0){entry = sent_L; }
                else if (i==1) {entry = sent_S;}
                else if (i==2) {entry = received;}

                for (PinMessage p:entry){
                    Map<String,Integer> in_map = new HashMap<String, Integer>();
                    Map<String,Map<String,Integer>> out_map = new HashMap<String, Map<String,Integer>>();

                    in_map.put(list[i], new Integer(p.getID()));
                    String title = name_msg[i] + k;
                    out_map.put(title, in_map);
                    messages.add(out_map);
                    k++;
                }

                k=1;
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.toString());
        }

        adapter.notifyDataSetChanged();
    }
    
     public void sendLocalMessages(){
        MainActivity m_act = new MainActivity();
        try {
            // Retrieve the list from internal storage
            List<PinMessage> entries = (List<PinMessage>) InternalStorage.readObject(this.getContext(), "sent_local");

            if (entries.isEmpty()==false){
                for (PinMessage p : entries){
                   if (isInPretendedLocation(p.getLocation())){
                       m_act.termiteSender(p);
                   }
                }
            }

        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.toString());
        }
    }

    private boolean isInPretendedLocation(String location) {
        TrackGPS gps = new TrackGPS(this.getContext());
        DBHandler db = new DBHandler(this.getContext());
        String[] temp;
        String viewGps="";
        double longitude=0, latitude=0;

        //Get GPS coordinates of current location
        if(gps.canGetLocation()) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
        }
        else {gps.showSettingsAlert();}

        //Get GPS coordinates of message location
        Cursor c = db.getAllLocals();

        c.moveToFirst();
        while(!c.isAfterLast()) {
            if (location.equals(c.getString(c.getColumnIndex(KEY_LOCAL)))){
                viewGps = c.getString(c.getColumnIndex(KEY_GPS));
            }
            c.moveToNext();
        }

        c.close();

        //Get the lagitude and latitude from the string
        temp = viewGps.split("/");
        double long_comp = Double.parseDouble(temp[0]);
        double lat_comp = Double.parseDouble(temp[1]);

        if (distance(lat_comp, latitude, long_comp, longitude)<=20){
            return true;
        }else{return false;}
    }

    //Calculates the distance beteween two points
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

}
