package com.example.eduardo.locmess;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class WebServiceHandler {

    private static final String TAG = "WebServiceHandler";

    private Context context;

    public WebServiceHandler(Context context) {
        this.context = context;
    }

    public void sendRequest(final String url) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            response = response.getJSONObject("resp");
                            String status = response.getString("status");
                            Log.d(TAG, "Request: '" + url + "' ended with status: '" + status + "'.");

                            // yes, thor was here
                            if (context instanceof LoginActivity) {
                                checkLogin(response);
                            } else if(context instanceof SignupActivity) {
                                checkSignUp(response);
                            } else if (context instanceof ListInterestsActivity) {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        Volley.newRequestQueue(context).add(jsonRequest);
    }

    // check user login
    private void checkLogin(JSONObject json) throws JSONException {
        LoginActivity activity = (LoginActivity) context;
        activity.progressDialog.dismiss();

        if (!json.getString("username").isEmpty()
                && !json.getString("email").isEmpty()
                && !json.getString("password").isEmpty()) {

            // create session for this user
            activity.session.createLoginSession(json.getString("username"), json.getString("email"));

            activity.onLoginSuccess();
        } else {
            activity.onLoginFailed();
        }
    }

    // check user sign up
    private void checkSignUp(JSONObject json) throws JSONException {
        SignupActivity activity = (SignupActivity) context;
        activity.progressDialog.dismiss();

        if ("OK".equals(json.getString("status"))) {
            activity.onSignupSuccess();
        } else {
            activity.onSignupFailed();
        }
    }

    // update interests
    private void updateInterests(JSONObject json) throws JSONException {
        ListInterestsActivity activity = (ListInterestsActivity) context;

        // TODO
        //activity.updateInterests();
    }
}
