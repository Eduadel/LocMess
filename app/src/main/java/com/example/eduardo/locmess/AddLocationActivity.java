package com.example.eduardo.locmess;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class AddLocationActivity extends AppCompatActivity {

    double longitude;
    double latitude;
    Button btn_get, btn_save;
    private TrackGPS gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            latitude = gps .getLatitude();

            TextView viewGps = (TextView) findViewById(R.id.vwGpsCoor);
            viewGps.setText("\nLongitude: "+Double.toString(longitude)+"\n\nLatitude: "+Double.toString(latitude));
        }
        else
        {
            gps.showSettingsAlert();
        }

    }
    public void SaveLocation(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
