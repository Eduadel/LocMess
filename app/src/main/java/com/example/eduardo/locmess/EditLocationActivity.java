package com.example.eduardo.locmess;

import android.content.Intent;
import android.database.Cursor;
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

import static com.example.eduardo.locmess.LocationFragment.KEY_EXTRA_INFO_ID;

public class EditLocationActivity extends AppCompatActivity {

    double longitude, latitude;
    float radius;
    Button btn_pos, btn_edit;
    EditText local, viewRadius, viewGps, txtSSID;
    private TrackGPS gps;
    DBHandler db = new DBHandler(this);
    String loc,GPS,RADIUS,SSID;
    Intent mintent;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mintent = getIntent();
        id = mintent.getIntExtra(KEY_EXTRA_INFO_ID,0);

        local = (EditText) findViewById(R.id.txtLocal);
        viewRadius = (EditText) findViewById(R.id.vwGpsRadius);
        viewGps = (EditText) findViewById(R.id.vwGpsCoor);
        txtSSID = (EditText) findViewById(R.id.txtSSID);

        btn_pos = (Button) findViewById(R.id.btnGetPos);
        btn_edit = (Button) findViewById(R.id.btnSaveEdit);

        Cursor rs = db.getLocal(id);
        rs.moveToFirst();
        String localName = rs.getString(rs.getColumnIndex(db.KEY_LOCAL));
        String gpsInfo = rs.getString(rs.getColumnIndex(db.KEY_GPS));
        String radiusInfo = rs.getString(rs.getColumnIndex(db.KEY_RADIUS));
        String ssidInfo = rs.getString(rs.getColumnIndex(db.KEY_SSID));
        if (!rs.isClosed()) {
            rs.close();
        }

        local.setText(localName);
        viewGps.setText(gpsInfo);
        viewRadius.setText(radiusInfo);
        txtSSID.setText(ssidInfo);

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

            viewGps.setText("\nLongitude: "+Double.toString(longitude)+"\n\nLatitude: "+Double.toString(latitude));
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
        RADIUS = viewRadius.getText().toString();
        SSID = txtSSID.getText().toString();

        if(db.updateLocal(id, loc, GPS, RADIUS, SSID)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Local Update Successful", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "Local Update Failed", Toast.LENGTH_SHORT).show();
        }

    }
}
