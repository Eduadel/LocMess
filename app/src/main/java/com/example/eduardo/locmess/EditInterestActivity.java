package com.example.eduardo.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditInterestActivity extends AppCompatActivity {

    private final static String ACTION_BAR_TITLE = "Edit Interest";
    private final static String ERROR_EMPTY = "This field cannot be emtpy.";
    private final static String ERROR_LENGTH = "This field has a maximum of 10 characters.";
    private final static String ERROR_DEFAULT = "An error as occurred";
    private final static String SUCCESS_SAVE = "Changes saved successfully";

    // session manager
    SessionManager session;

    // text fields
    EditText keyField, valueField;

    // interest info id
    int infoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_interest);
        getSupportActionBar().setTitle(ACTION_BAR_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // session manager
        session = new SessionManager(getApplicationContext());

        // get clicked item info
        infoId = getIntent().getIntExtra(ListInterestsActivity.KEY_EXTRA_INFO_ID, 0);
        String infoKey = getIntent().getStringExtra(ListInterestsActivity.KEY_EXTRA_INFO_KEY);
        String infoValue = getIntent().getStringExtra(ListInterestsActivity.KEY_EXTRA_INFO_VALUE);

        // get text fields
        keyField = (EditText) findViewById(R.id.edit_key);
        valueField = (EditText) findViewById(R.id.edit_value);

        // update key and value fields
        keyField.setText(infoKey);
        valueField.setText(infoValue);
    }

    // button event listener
    public void saveChanges(View v) {
        // check user login
        session.checkLogin();

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

        // update new key / value
        if (!db.updateInterest(new Integer(infoId), key, value)) {
            // try again
            if (!db.updateInterest(new Integer(infoId), key, value)) {
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

    // button event listener
    public void deleteInterest(View v) {
        // check user login
        session.checkLogin();

        // new database handler
        DBHandler db = new DBHandler(getApplicationContext());

        // delete interest
        db.deleteInterest(infoId);

        // display success message
        Toast.makeText(getApplicationContext(), SUCCESS_SAVE, Toast.LENGTH_SHORT).show();

        // go to previous activity
        Intent intent = new Intent(v.getContext(), ListInterestsActivity.class);
        startActivity(intent);
        finish();
    }

}
