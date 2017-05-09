package com.example.eduardo.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class EditLocationActivity extends AppCompatActivity {

    double longitude, latitude;
    float radius;
    Button btn_pos, btn_edit;
    EditText local, viewRadius, viewGps, txtSSID;
    private TrackGPS gps;
    DBHandler db = new DBHandler(this);
    String loc,GPS,RADIUS,SSID;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String val = getIntent().getExtras().getString("Local");

        local = (EditText) findViewById(R.id.txtLocal);
        txtSSID = (EditText) findViewById(R.id.txtSSID);

        local.setText(val);

        btn_pos = (Button) findViewById(R.id.btnGetPos);
        btn_edit = (Button) findViewById(R.id.btnSaveEdit);

        btn_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCoordinates();
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveLocation();
            }
        });
    }

    public void getCoordinates(){
        gps = new TrackGPS(EditLocationActivity.this);

        if(gps.canGetLocation()){

            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
            radius = gps.getAccuracy();

            viewRadius = (EditText) findViewById(R.id.vwGpsRadius);
            viewGps = (EditText) findViewById(R.id.vwGpsCoor);

            viewGps.setText("\nLongitude: "+Double.toString(longitude)+"\n\nLatitude: "+Double.toString(latitude));
            viewRadius.setText(Double.toString(radius) + " m");
        }
        else
        {
            gps.showSettingsAlert();
        }

    }

    public void SaveLocation(){

        id = getIntent().getExtras().getInt("Local");
        loc = local.getText().toString();
        SSID = txtSSID.getText().toString();
        GPS = viewGps.getText().toString();
        RADIUS = viewRadius.getText().toString();
        if(db.updateLocal(id, loc, GPS, RADIUS, SSID)) {
            Toast.makeText(getApplicationContext(), "Local Update Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "Local Update Failed", Toast.LENGTH_SHORT).show();
        }

    }
}
