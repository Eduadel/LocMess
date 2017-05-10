package com.example.eduardo.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditInterestActivity extends AppCompatActivity{

    public final static String ACTION_BAR_TITLE = "Edit Interest";

    // session manager
    SessionManager session;

    // text fields
    EditText keyField, valueField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_interest);
        getSupportActionBar().setTitle(ACTION_BAR_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // session manager
        session = new SessionManager(getApplicationContext());

        // get clicked item
        // TODO COMPLETE
        getIntent().getIntExtra(ListInterestsActivity.KEY_EXTRA_INFO_ID, 0);

        // get text fields
        keyField = (EditText) findViewById(R.id.edit_key);
        valueField = (EditText) findViewById(R.id.edit_value);

        // update key and value fields
        // TODO
    }

    // button event listener
    public void saveChanges(View v) {
        // check user login
        session.checkLogin();

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

    // button event listener
    public void deleteInterest(View v) {
        // check user login
        session.checkLogin();

        // delete interest
        // TODO CALL DB

        // display success message
        Toast.makeText(getApplicationContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show();

        // go to previous activity
        Intent intent = new Intent(v.getContext(), ListInterestsActivity.class);
        startActivity(intent);
    }

}
