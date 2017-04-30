package com.example.eduardo.locmess;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import static com.example.eduardo.locmess.R.id.parent;


public class LocationFragment extends Fragment {

    String[] teste = {"Almada", "Lisboa", "Oeiras", "Cacém", "Loures", "Setúbal", "Corroios", "Seixal", "Costa da Caparica", "Sesimbra", "Faro", "Coimbra", "Leiria"};


    private FloatingActionButton fgps, fssid;
    private FloatingActionMenu fab;
    double longitude;
    double latitude;
    private TrackGPS gps;

    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        //Floating Action Button Action
        fgps = (FloatingActionButton) rootView.findViewById(R.id.fabgps);
        fssid = (FloatingActionButton) rootView.findViewById(R.id.fabssid);
        fab = (FloatingActionMenu) rootView.findViewById(R.id.locatAdd);

        //handling each floating action button clicked
        fgps.setOnClickListener(onButtonClick());
        fssid.setOnClickListener(onButtonClick());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fab.isOpened()) {
                    fab.close(true);
                }
            }
        });

        final ListView lv = (ListView) rootView.findViewById(R.id.locationListView);
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, teste);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EditLocationActivity.class);
                String listItem = (String)lv.getItemAtPosition(position);
                intent.putExtra("Local", listItem);
                startActivity(intent);

                //Toast.makeText(getActivity(), "Escolheu " + position, Toast.LENGTH_SHORT).show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.v("long clicked", "pos:" + position);
                setHasOptionsMenu(true);//ActionBar Icons Actions
                return true;
            }
        });

        return rootView;
    }

    private View.OnClickListener onButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == fgps) {
                    Intent intent = new Intent(getActivity(), AddLocationActivity.class);
                    startActivity(intent);
                } else if (view == fssid) {
                    Intent intent = new Intent(getActivity(), AddSSIDLocationActivity.class);
                    startActivity(intent);
                }
                fab.close(true);
            }
        };
    }



    //ActionBar Icons
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.action_buttons, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
