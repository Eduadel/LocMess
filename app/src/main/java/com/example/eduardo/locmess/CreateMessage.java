package com.example.eduardo.locmess;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateMessage extends AppCompatActivity {
    private static final String TAG = "CreateMessage";
    private PinMessage msg = new PinMessage();
    private String[] local_list = {"Almada", "Lisboa", "Oeiras", "Cacém", "Loures", "Setúbal", "Corroios", "Seixal", "Costa da Caparica", "Sesimbra", "Faro", "Coimbra", "Leiria"};
    private LinearLayout mLayout;
    private Spinner spinner;
    DateFormat formatDateTime = DateFormat.getDateTimeInstance(DateFormat.DATE_FIELD, DateFormat.SHORT);
    Calendar dateTime = Calendar.getInstance();
    Calendar dateTimeEnd = Calendar.getInstance();
    Button date_start, hour_start, date_end, hour_end;
    TextView show_date_start, show_date_end;
    private int start_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_message);

        //Back bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("New Message");    //View title on top

        //Location Selection
        addLocationSelectionSpinner();

        //List Selection
        mLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);

        //Time Window Selection
        addTimeWindowSelection();

        Button final_send_button = (Button) findViewById(R.id.send_button);
        final_send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalSendMessage();
            }
        });

    }

    public void finalSendMessage() {
        finilizeMessage();
        printSendMessage();
        this.finish();
    }

    public void finilizeMessage() {
        msg.generateId();
        msg.setSender("Ana Maria");
        msg.setLocation(getLocation());
        msg.setContent(getMessageContent());
        msg.setEmail(getEmailAddress());
        msg.setPhoneNumber(getPhoneContact());
        msg.setStartDate(getStartTime());
        msg.setEndDate(getEndTime());
        msg.setPublicationDate(getCurrentTime());

        int n = mLayout.getChildCount();
        View v = null;
        for (int k = 0; k != n; k++) {
            v = mLayout.getChildAt(k);
            View vt = v.findViewById(R.id.requirement_edit_text);
            View vs = v.findViewById(R.id.location_spinner);
            if (vs instanceof Spinner) {
                Spinner op = (Spinner) vs;
                EditText txt = (EditText) vt;
                if (op.getSelectedItem().toString().equals("Include")) {
                    msg.addToWhitelist(txt.getText().toString());
                } else if (op.getSelectedItem().toString().equals("Exclude")) {
                    msg.addToBlacklist(txt.getText().toString());
                }
            }
        }

        storeToCache();
    }

    //This method takes care of the storing of the message to the phone cache,
    //so it can be sent
    public void storeToCache(){
        String key;

        if (isDeliveryModeDescenter()==true) {
            key = "sent_local";
        }
        else {
            key = "sent_server";
        }

        try {
            // Retrieve the list from internal storage
            List<PinMessage> entries = (List<PinMessage>) InternalStorage.readObject(this, key);

            //Add message to array
            entries.add(msg);

            //Delete previous copy
            String path = getFilesDir().getAbsolutePath() + "/" + key;
            File file = new File(path);
            file.delete();

            //Save updated version to cache
            InternalStorage.writeObject(this, key, entries);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    //Prints a message to the screen that the message is sent
    public void printSendMessage(){
        Context context = getApplicationContext();
        CharSequence text = "Message Sent";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //Related to Time Window Selection......................................................................................................
    public void addTimeWindowSelection(){
        //Buttons
        date_start = (Button) findViewById(R.id.start_date);
        hour_start = (Button) findViewById(R.id.start_hour);
        date_end = (Button) findViewById(R.id.end_date);
        hour_end = (Button) findViewById(R.id.end_hour);

        //Views
        show_date_start = (TextView) findViewById(R.id.start_date_view);
        show_date_end = (TextView) findViewById(R.id.end_date_view);

        //Click Listeners

        //START
        date_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {updateDate(1);}
        });

        hour_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {updateHour(1);}
        });

        //END
        date_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { updateDate(2);}
        });

        hour_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { updateHour(2);}
        });
    }

    //Updates the label that shows the date and time selected
    public void updateTextLabel(int option){
        switch (option) {
            case 1:
                show_date_start.setText(formatDateTime.format(dateTime.getTime()));
                break;
            case 2:
                show_date_end.setText(formatDateTime.format(dateTimeEnd.getTime()));
                break;
        }
    }

    //Allows the user to change the date
    public void updateDate(int option){
        switch (option){
            case 1:
                new DatePickerDialog(this, d, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case 2:
                new DatePickerDialog(this, k, dateTimeEnd.get(Calendar.YEAR), dateTimeEnd.get(Calendar.MONTH), dateTimeEnd.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }

    }

    //Allows the user to change the hour
    public void updateHour(int option){
        switch(option){
            case 1:
                new TimePickerDialog(this, t, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), true).show();
                break;
            case 2:
                new TimePickerDialog(this, h, dateTimeEnd.get(Calendar.HOUR_OF_DAY), dateTimeEnd.get(Calendar.MINUTE), true).show();
                break;
        }

    }

    //DataPickerDialog for choosing starting date of message
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth){
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateTextLabel(1);
        }
    };

    //DataPickerDialog for choosing starting time of message
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet (TimePicker view, int hourOfDay, int minute){
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);

            updateTextLabel(1);
        }
    };

    public Date getStartTime(){return dateTime.getTime();}

    //DataPickerDialog for choosing ending date of message
    DatePickerDialog.OnDateSetListener k = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth){
            dateTimeEnd.set(Calendar.YEAR, year);
            dateTimeEnd.set(Calendar.MONTH, monthOfYear);
            dateTimeEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateTextLabel(2);
        }
    };

    //DataPickerDialog for choosing ending time of message
    TimePickerDialog.OnTimeSetListener h = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet (TimePicker view, int hourOfDay, int minute){
            dateTimeEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTimeEnd.set(Calendar.MINUTE, minute);

            updateTextLabel(2);
        }
    };

    public Date getEndTime(){return dateTimeEnd.getTime();}

    //Related to Location Selection.........................................................................................
    public void addLocationSelectionSpinner(){
        //Location Selection
        spinner = (Spinner) findViewById(R.id.location_spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, local_list); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    public String getLocation(){
        return spinner.getSelectedItem().toString();
    }

    //Related to List Selection............................................................................................
    //Adds another view after click in ADD button
    public void onAddField(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.field, null);
        // Add the new row before the add field button.
        mLayout.addView(rowView, mLayout.getChildCount() - 1);
    }

    //Related to List Selection
    //Removes the view selected
    public void onDelete(View v) {
        mLayout.removeView((View) v.getParent());
    }

    //Related to Contacts.....................................................................................................

    public String getPhoneContact(){
        EditText phone_contact = (EditText) findViewById(R.id.contact_phone);
        return PhoneNumberUtils.formatNumber(phone_contact.getText().toString());
    }

    public String getEmailAddress(){
        EditText mail_add = (EditText) findViewById(R.id.contact_email);
        return mail_add.getText().toString();
    }

    //Related to Message Content..............................................................................................
    public String getMessageContent(){
        EditText msg_content = (EditText) findViewById(R.id.editMessage);
        return msg_content.getText().toString();
    }

    //Current Time
    public Date getCurrentTime() {
        Calendar dc = Calendar.getInstance();
        return dc.getTime();
    }

    //Related to Delivery Mode (Descentralized)...............................................................................
    public boolean isDeliveryModeDescenter(){
        CheckBox box = (CheckBox) findViewById(R.id.checkBox);
        if(box.isChecked()){return true;}
        else { return false; }
    }

    //To return to previous view
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return true;
    }
}
