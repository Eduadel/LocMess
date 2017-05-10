package com.example.eduardo.locmess;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddInterestActivity extends AppCompatActivity {

    public final static String ACTION_BAR_TITLE = "Add New Interest";

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
        String value = keyField.getText().toString();

        // check key
        if (key.isEmpty()) {
            keyField.setError("This field cannot be emtpy.");
            return;
        }

        // check value
        if (value.isEmpty()) {
            valueField.setError("This field cannot be emtpy.");
            return;
        }

        // update new key / value
        // TODO CALL DB

        // display success message
        Toast.makeText(getApplicationContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show();

        // go to previous activity
        Intent intent = new Intent(v.getContext(), ListInterestsActivity.class);
        startActivity(intent);
    }
}
