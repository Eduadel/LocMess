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
                            Log.d(TAG, "Request '" + url + "' ended with status: '" + status + "'.");
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
}
