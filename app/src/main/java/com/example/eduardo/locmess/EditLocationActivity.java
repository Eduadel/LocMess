package com.example.eduardo.locmess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class EditLocationActivity extends AppCompatActivity {

    double longitude;
    double latitude;
    Button btn_pos;
    Button btn_edit;
    private TrackGPS gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String val = getIntent().getExtras().getString("Local");

        TextView txtEdit = (TextView) findViewById(R.id.txtLocal);

        txtEdit.setText(val);

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
