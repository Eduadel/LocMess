package com.example.eduardo.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

public class ShowMessage extends AppCompatActivity {
    public String TAG = "Show Message";     //For exceptions
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");   //To convert from Date to String format

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_message);

        //Back bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get position from previous activity
        Intent myIntent = getIntent(); // gets the previously created intent
        int id = myIntent.getIntExtra("id", 0);
        String list_name = myIntent.getStringExtra("list");

        //Views
        TextView user_id_view = (TextView) findViewById(R.id.user_id_show);
        TextView date_view = (TextView) findViewById(R.id.date_show);
        TextView message_view = (TextView) findViewById(R.id.message_content);

        setTitle(myIntent.getStringExtra("title"));     //set view title on top


        PinMessage m = getPinMessage(list_name, id);       //Get PinMessage from cache

        //Display the message
        user_id_view.setText(m.getSender());
        date_view.setText(df.format(m.getPublicationDate()));
        message_view.setText(m.getContent());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return true;
    }

    //Get selected message from cache
    public PinMessage getPinMessage(String list, int id){
        PinMessage pm = null;

        try {
            List<PinMessage> list_messages = (List<PinMessage>) InternalStorage.readObject(this, list);

            Iterator<PinMessage> iterator = list_messages.iterator();
            while (iterator.hasNext()) {
                PinMessage c = iterator.next();
                if (c.getID() == id) {
                    pm = c;
                }
            }
        }catch (IOException e) {
            Log.e(TAG, e.toString());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.toString());
        }

        return pm;
    }


}
