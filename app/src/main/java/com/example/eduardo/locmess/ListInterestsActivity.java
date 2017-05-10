package com.example.eduardo.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;

public class ListInterestsActivity extends AppCompatActivity{

    // TODO DELETE
    String[] testList = {"Club", "Job"};

    public final static String KEY_EXTRA_INFO_ID = "KEY_EXTRA_INFO_ID";
    public final static String ACTION_BAR_TITLE = "Interests";

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_interests);
        getSupportActionBar().setTitle(ACTION_BAR_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // session manager
        session = new SessionManager(getApplicationContext());

        // create list view
        ListView lv = (ListView) findViewById(R.id.interestsListView);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, testList);
        lv.setAdapter(adapter);

        // create event listener for list view
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // start new activity
                Intent intent = new Intent(view.getContext(), EditInterestActivity.class);
                intent.putExtra(KEY_EXTRA_INFO_ID, position);
                startActivity(intent);
            }
        });

        // create event listener to add new interest
        FloatingActionButton addIcon = (FloatingActionButton) findViewById(R.id.addInterest);
        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start new activity
                Intent intent = new Intent(view.getContext(), AddInterestActivity.class);
                startActivity(intent);
            }
        });
    }

    // quick workaround to avoid returning to edit/add pages
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // go to main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
