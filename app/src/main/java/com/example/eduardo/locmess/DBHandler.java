package com.example.eduardo.locmess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {

    // Database Name
    public static final String DATABASE_NAME = "loc_mess.db";
    // Table names
    public static final String TABLE_USER = "user";
    public static final String TABLE_MESSAGE = "messages";
    public static final String TABLE_LOCAL = "local";
    public static final String TABLE_BLACKLIST = "blacklist";
    public static final String TABLE_WHITELIST = "whitelist";
    public static final String TABLE_INTERESTS = "interest";
    // Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_USER = "username";
    public static final String KEY_PASS = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_INTETOPICS = "inte_topics";
    public static final String KEY_CREATED = "created_at";
    public static final String KEY_UPDATED = "updated_at";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_LTYPE = "list_type";
    public static final String KEY_BTIME = "begin_time";
    public static final String KEY_ETIME = "end_time";
    public static final String KEY_PTIME = "published_time";
    public static final String KEY_POC = "poc";
    public static final String KEY_IDCREATOR = "id_creator";
    public static final String KEY_LOCAL = "local_name";
    public static final String KEY_GPS = "gps_coord";
    public static final String KEY_RADIUS= "gps_radius";
    public static final String KEY_SSID = "ssid_info";
    public static final String KEY_IDMESSAGE = "id_message";
    public static final String KEY_TOINTE = "tointerest";
    public static final String KEY_TOPIC_KEY = "topic_key";
    public static final String KEY_TOPIC_VALUE = "topic_value";

    // Database Version
    private static final int DATABASE_VERSION = 3;

    Long tsLong = System.currentTimeMillis()/1000;
    String ts = tsLong.toString();


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," + KEY_USER + " TEXT,"
                + KEY_PASS + " TEXT NOT NULL, " + KEY_EMAIL + " TEXT NOT NULL, " + KEY_INTETOPICS + " INTEGER, " + KEY_CREATED + " DATETIME, " + KEY_UPDATED + " DATETIME " + ");";
        String CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," + KEY_MESSAGE + " TEXT,"
                + KEY_LTYPE + " BOOLEAN, " + KEY_BTIME + " DATETIME, " + KEY_ETIME + " DATETIME, " + KEY_PTIME + " DATETIME, " + KEY_POC + " INTEGER, " + KEY_IDCREATOR + " INTEGER, "
                + "FOREIGN KEY ("+KEY_IDCREATOR+") REFERENCES " + TABLE_USER + "("+KEY_ID+")," + " FOREIGN KEY ("+KEY_POC+") REFERENCES " + TABLE_LOCAL + "("+KEY_ID+"));";
        String CREATE_LOCAL_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_LOCAL + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," + KEY_LOCAL + " TEXT NOT NULL,"
                + KEY_GPS + " TEXT, " + KEY_RADIUS + " TEXT, " + KEY_SSID + " TEXT, " + KEY_CREATED + " DATETIME, " + KEY_UPDATED + " DATETIME, " + KEY_IDCREATOR + " INTEGER, "
                + "FOREIGN KEY ("+KEY_IDCREATOR+") REFERENCES "+TABLE_USER+"("+KEY_ID+"));";
        String CREATE_BLACK_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_BLACKLIST + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," + KEY_IDMESSAGE + " INTEGER," + KEY_TOINTE + " BOOLEAN, "
                + "FOREIGN KEY ("+KEY_IDMESSAGE+") REFERENCES "+TABLE_MESSAGE+"("+KEY_ID+"));";
        String CREATE_WHITE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," + KEY_IDMESSAGE + " INTEGER," + KEY_TOINTE + " BOOLEAN, "
                + "FOREIGN KEY ("+KEY_IDMESSAGE+") REFERENCES "+TABLE_MESSAGE+"("+KEY_ID+"));";
        String CREATE_INTEREST_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_INTERESTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," + KEY_TOPIC_KEY + " TEXT," + KEY_TOPIC_VALUE + " TEXT);";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
        db.execSQL(CREATE_LOCAL_TABLE);
        db.execSQL(CREATE_BLACK_TABLE);
        db.execSQL(CREATE_WHITE_TABLE);
        db.execSQL(CREATE_INTEREST_TABLE);
        db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCAL );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLACKLIST );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WHITELIST );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERESTS );
        db.close();
        // Creating tables again
        onCreate(db);
    }

    // Adding new GPS local
    public boolean addLocal(String local, String gps, String radius) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOCAL, local); // Local Name
        values.put(KEY_GPS, gps); // Local Coord
        values.put(KEY_RADIUS, radius); // Local Radius
        values.put(KEY_CREATED,ts); //Current time
        // Inserting Row
        db.insert(TABLE_LOCAL, null, values);
        db.close(); // Closing database connection
        return true;
    }

    // Adding new SSID local
    public boolean addLocalSSID(String local, String ssid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOCAL, local); // Local Name
        values.put(KEY_SSID, ssid); // Local SSID
        values.put(KEY_CREATED,ts); //Current time
        // Inserting Row
        db.insert(TABLE_LOCAL, null, values);
        db.close(); // Closing database connection
        return true;
    }

    // Updating local
    public boolean updateLocal(Integer id, String local, String gps, String radius, String ssid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOCAL, local); // Local Name
        values.put(KEY_GPS, gps); // Local Coord
        values.put(KEY_RADIUS, radius); // Local Radius
        values.put(KEY_SSID, ssid); // Local SSID
        values.put(KEY_UPDATED,ts); //Current time
        db.update(TABLE_LOCAL, values, KEY_ID + " = ? ", new String[] { Integer.toString(id) } );
        db.close();
        return true;
    }

    // Getting local
    public Cursor getLocal(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TABLE_LOCAL + " WHERE " +
                KEY_ID + "=?", new String[] { Integer.toString(id) } );
        return res;
    }

    // Getting All locals
    public Cursor getAllLocals() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT " +KEY_ID + "," + KEY_LOCAL + "," + KEY_GPS + " FROM " + TABLE_LOCAL, null );
        return res;
    }

    // Deleting local
    public Integer deleteLocal(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer i = db.delete(TABLE_LOCAL, KEY_ID + " = ? ", new String[] { Integer.toString(id) });
        db.close();
        return i;
    }

    // Adding new User
    public boolean addUser(String user, String pass, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER, user); // User Name
        values.put(KEY_PASS, pass); // Password
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_CREATED,ts); //Current time
        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
        return true;
    }

    // Updating User
    public boolean updateuser(Integer id, String user, String pass, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER, user); // User Name
        values.put(KEY_PASS, pass); // Password
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UPDATED,ts); //Current time
        db.update(TABLE_USER, values, KEY_ID + " = ? ", new String[] { Integer.toString(id) } );
        db.close();
        return true;
    }

    // Getting User
    public boolean getUser(String email, String password) {
        // array of columns to fetch
        String[] columns = {
                KEY_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = KEY_EMAIL + " = ?" + " AND " + KEY_PASS + " = ?";

        // selection arguments
        String[] selectionArgs = {email, password};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    // Deleting user Logout
    public Integer deleteuser(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer i = db.delete(TABLE_USER, KEY_ID + " = ? ", new String[]{Integer.toString(id)});
        db.close();
        return i ;
    }

    // retrieve interest
    public Cursor getInterests() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_ID + "," + KEY_TOPIC_KEY + "," + KEY_TOPIC_VALUE + " FROM " + TABLE_INTERESTS + ";", null);
        return cursor;
    }

    // add interest
    public boolean addInterest(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TOPIC_KEY, key);     // topic key
        values.put(KEY_TOPIC_VALUE, value);     // topic value
        long id = db.insert(TABLE_INTERESTS, null, values);
        db.close();     // close database connection
        return id != -1;
    }

    // update interest
    public boolean updateInterest(Integer id, String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TOPIC_KEY, key);     // topic key
        values.put(KEY_TOPIC_VALUE, value);     // topic value
        int rows = db.update(TABLE_INTERESTS, values, KEY_ID + " = ? ", new String[] { Integer.toString(id) } );
        db.close();     // close database connection
        return rows > 0;
    }

    // delete interest
    public void deleteInterest(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INTERESTS, KEY_ID + " = ? ", new String[] { Integer.toString(id) });
        db.close();
    }
}
