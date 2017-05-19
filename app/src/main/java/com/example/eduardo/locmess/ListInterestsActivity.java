package com.example.eduardo.locmess;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.github.clans.fab.FloatingActionButton;

import java.util.HashMap;

public class ListInterestsActivity extends AppCompatActivity {

    public final static String KEY_EXTRA_INFO_ID = "KEY_EXTRA_INFO_ID";
    public final static String KEY_EXTRA_INFO_KEY = "KEY_EXTRA_INFO_KEY";
    public final static String KEY_EXTRA_INFO_VALUE = "KEY_EXTRA_INFO_VALUE";
    private final static String ACTION_BAR_TITLE = "Interests";

    SessionManager session;
    ListView lv;
    DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_interests);
        getSupportActionBar().setTitle(ACTION_BAR_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // session manager
        session = new SessionManager(getApplicationContext());

        // instantiate database handler
        db = new DBHandler(this);

        // fecth cursor
        final Cursor cursor = db.getInterests();

        String [] columns = new String[] {db.KEY_ID, db.KEY_TOPIC_KEY, db.KEY_TOPIC_VALUE};
        int [] widgets = new int[] {R.id.interestid, R.id.interestkey, R.id.interestvalue};

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.interests_layout, cursor, columns, widgets, 0);
        lv = (ListView) findViewById(R.id.interestsListView);
        lv.setAdapter(cursorAdapter);

        // create event listener for list view
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get values for new activity
                Cursor itemCursor = (Cursor) lv.getItemAtPosition(position);
                int infoID = itemCursor.getInt(itemCursor.getColumnIndex(db.KEY_ID));
                String infoKey = itemCursor.getString(itemCursor.getColumnIndex(db.KEY_TOPIC_KEY));
                String infoValue = itemCursor.getString(itemCursor.getColumnIndex(db.KEY_TOPIC_VALUE));
                itemCursor.close();

                // save values
                Intent intent = new Intent(view.getContext(), EditInterestActivity.class);
                intent.putExtra(KEY_EXTRA_INFO_ID, infoID);
                intent.putExtra(KEY_EXTRA_INFO_KEY, infoKey);
                intent.putExtra(KEY_EXTRA_INFO_VALUE, infoValue);

                // start new activity
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

    // update interests
    public void updateInterests(HashMap<String, String> interests) {

    }
}
