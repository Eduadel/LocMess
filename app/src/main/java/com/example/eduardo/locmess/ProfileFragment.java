package com.example.eduardo.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // session manager
        final SessionManager session = new SessionManager(getActivity().getApplicationContext());

        // check user login
        session.checkLogin();

        // inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // get user details from session
        HashMap<String, String> user = session.getUserDetails();
        final String username = user.get(SessionManager.KEY_NAME);
        final String email = user.get(SessionManager.KEY_EMAIL);

        // get text boxes
        final EditText usernameField = (EditText) rootView.findViewById(R.id.edit_username);
        final EditText emailField = (EditText) rootView.findViewById(R.id.edit_email);

        // update text boxes with session values
        usernameField.setText(username);
        emailField.setText(email);

        // add click listeners to buttons
        // add click event to edit interests button
        Button interestsButton = (Button) rootView.findViewById(R.id.btn_edit_interests);
        interestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // change activity
                Intent intent = new Intent(getActivity().getApplicationContext(), ListInterestsActivity.class);
                startActivity(intent);
            }
        });

        // add click event to edit password button
        Button passButton = (Button) rootView.findViewById(R.id.btn_edit_pass);
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // change activity
                Intent intent = new Intent(getActivity().getApplicationContext(), EditPasswordActivity.class);
                startActivity(intent);
            }
        });

        // add click event to save changes on profile button
        Button saveButton = (Button) rootView.findViewById(R.id.btn_save_prof);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check user login
                session.checkLogin();

                // get new username
                String newUsername = usernameField.getText().toString();

                // check username
                if (newUsername.isEmpty() || newUsername.length() < 3) {
                    usernameField.setError("username should at least have 3 characters");
                    return;
                } else {
                    usernameField.setError(null);
                }

                // get new username
                String newEmail = emailField.getText().toString();

                // check email
                if (newEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    emailField.setError("enter a valid email address");
                    return;
                } else {
                    emailField.setError(null);
                }

                // call database
                // TODO

                // display success message
                Toast.makeText(getActivity().getApplicationContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show();

                // logout user
                session.logoutUser();
            }
        });

        // add click event to logout button
        Button logoutButton = (Button) rootView.findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call logout function
                session.logoutUser();
            }
        });

        return rootView;
    }

}
