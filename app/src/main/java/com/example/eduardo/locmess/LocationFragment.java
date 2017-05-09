package com.example.eduardo.locmess;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.example.eduardo.locmess.R.id.parent;


public class LocationFragment extends Fragment {

    private FloatingActionButton fgps, fssid;
    private FloatingActionMenu fab;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter adapter;
    ListView lv;
    int posit;
    int toRemove;
    DBHandler db;

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

        //Handling each floating action button clicked
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

        db = new DBHandler(getActivity());

        final Cursor cursor = db.getAllLocals();
        String [] columns = new String[] {
                db.KEY_ID,
                db.KEY_LOCAL
        };
        int [] widgets = new int[] {
                R.id.local_id,
                R.id.local_name
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.item_layout,
                cursor, columns, widgets, 0);
        lv = (ListView) rootView.findViewById(R.id.locationListView);
        lv.setAdapter(cursorAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EditLocationActivity.class);
                String listItem = (String)lv.getItemAtPosition(position);
                intent.putExtra("Local", listItem);
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                posit = position;
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
                toRemove = posit;
                AlertDialog.Builder adb=new AlertDialog.Builder(getActivity());
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                                db.deleteLocal(toRemove);
                                Toast.makeText(getActivity().getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                adb.show();
                setHasOptionsMenu(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
