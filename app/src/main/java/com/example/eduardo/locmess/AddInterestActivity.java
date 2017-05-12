package com.example.eduardo.locmess;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddInterestActivity extends AppCompatActivity {

    private final static String ACTION_BAR_TITLE = "Add New Interest";
    private final static String ERROR_EMPTY = "This field cannot be emtpy.";
    private final static String ERROR_LENGTH = "This field has a maximum of 10 characters.";
    private final static String ERROR_DEFAULT = "An error as occurred";
    private final static String SUCCESS_SAVE = "Changes saved successfully";

    // session manager
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interest);
        getSupportActionBar().setTitle(ACTION_BAR_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // session manager
        session = new SessionManager(getApplicationContext());
    }

    // button event listener
    public void addInterest(View v) {
        // check user login
        session.checkLogin();

        // get text fields
        EditText keyField = (EditText) findViewById(R.id.add_key);
        EditText valueField = (EditText) findViewById(R.id.add_value);

        // get key
        String key = keyField.getText().toString();

        // get value
        String value = valueField.getText().toString();

        // check key
        if (key.isEmpty()) {
            keyField.setError(ERROR_EMPTY);
            return;
        } else if (key.length() > 10) {
            keyField.setError(ERROR_LENGTH);
            return;
        }

        // check value
        if (value.isEmpty()) {
            valueField.setError(ERROR_EMPTY);
            return;
        } else if (value.length() > 10) {
            valueField.setError(ERROR_LENGTH);
            return;
        }

        // new database handler
        DBHandler db = new DBHandler(getApplicationContext());

        // add interest
        if (!db.addInterest(key, value)) {
            // try again
            if (!db.addInterest(key, value)) {
                // display error message
                Toast.makeText(getApplicationContext(), ERROR_DEFAULT, Toast.LENGTH_SHORT).show();
            } else {
                // display success message
                Toast.makeText(getApplicationContext(), SUCCESS_SAVE, Toast.LENGTH_SHORT).show();
            }
        } else {
            // display success message
            Toast.makeText(getApplicationContext(), SUCCESS_SAVE, Toast.LENGTH_SHORT).show();
        }

        // go to previous activity
        Intent intent = new Intent(v.getContext(), ListInterestsActivity.class);
        startActivity(intent);
        finish();
    }
}
