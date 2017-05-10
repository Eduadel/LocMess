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
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class AddLocationActivity extends AppCompatActivity {

    double longitude, latitude;
    float radius;
    Button btn_get, btn_save;
    EditText local, viewRadius, viewGps;
    private TrackGPS gps;

    String loc, GPS, RADIUS;

    DBHandler db = new DBHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        local = (EditText) findViewById(R.id.txtLocal);
        viewRadius = (EditText) findViewById(R.id.vwGpsRadius);
        viewGps = (EditText) findViewById(R.id.vwGpsCoor);
        btn_get = (Button) findViewById(R.id.btnGetPos);
        btn_save = (Button) findViewById(R.id.btnSavePos);

        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCoordinates();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveLocation();
            }
        });
    }

    public void getCoordinates(){
        gps = new TrackGPS(AddLocationActivity.this);

        if(gps.canGetLocation()){

            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
            radius = gps.getAccuracy();

            viewGps.setText("Longitude: "+Double.toString(longitude)+"\n\nLatitude: "+Double.toString(latitude));
            viewRadius.setText(Double.toString(radius) + " m");
        }
        else
        {
            gps.showSettingsAlert();
        }

    }
    public void SaveLocation(){

        loc = local.getText().toString();
        GPS = viewGps.getText().toString();
        RADIUS = viewRadius.getText().toString();;

        if(db.addLocal(loc,GPS,RADIUS)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Local Inserted", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(), "Could not Insert Local", Toast.LENGTH_SHORT).show();
        }
    }
}
