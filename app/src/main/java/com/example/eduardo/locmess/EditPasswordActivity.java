package com.example.eduardo.locmess;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditPasswordActivity extends AppCompatActivity {

    public final static String ACTION_BAR_TITLE = "Change Password";
    private static final String SERVER_ADDRESS = "http://10.0.2.2:8000/updPass?";

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        getSupportActionBar().setTitle(ACTION_BAR_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // session manager
        session = new SessionManager(getApplicationContext());
    }

    // button event listener
    public void saveChanges(View v) {
        // check user login
        session.checkLogin();

        WebServiceHandler webHandler = new WebServiceHandler(this);

        // get old password
        /*EditText oldPassField = (EditText) findViewById(R.id.old_password);

        String oldPassword = oldPassField.getText().toString();

        // call webserver and check old password
        webHandler.sendRequest(SERVER_ADDRESS + "email=" + session.getUserDetails().get(SessionManager.KEY_EMAIL) + "&password=" + oldPassword);
        */
        // get new password
        EditText newPassField = (EditText) findViewById(R.id.new_password);
        EditText repeatedNewPassField = (EditText) findViewById(R.id.repeat_new_password);

        String newPassword = newPassField.getText().toString();
        String repeatedNewPassword = repeatedNewPassField.getText().toString();

        // check new password
        if (newPassword.isEmpty() || newPassword.length() < 4 || newPassword.length() > 10) {
            newPassField.setError("between 4 and 10 alphanumeric characters");
            return;
        } else {
            newPassField.setError(null);
        }

        // check if new password matches
        if (!newPassword.equals(repeatedNewPassword)) {
            repeatedNewPassField.setError("passwords mismatch");
            return;
        } else {
            repeatedNewPassField.setError(null);
        }

        // send request to web server
        webHandler.sendRequest(SERVER_ADDRESS + "email=" + session.getUserDetails().get(SessionManager.KEY_EMAIL) + "&password=" + newPassword);

        // display success message
        Toast.makeText(getApplicationContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show();

        // logout user
        session.logoutUser();
    }

}
