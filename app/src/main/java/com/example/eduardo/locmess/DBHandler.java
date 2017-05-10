package com.example.eduardo.locmess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {

    // Database Name
    public static final String DATABASE_NAME = "loc_mess.db";
    // Database Version
    private static final int DATABASE_VERSION = 3;
    // Table names
    public static final String TABLE_USER = "user";
    public static final String TABLE_MESSAGE = "messages";
    public static final String TABLE_LOCAL = "local";
    public static final String TABLE_BLACKLIST = "blacklist";
    public static final String TABLE_WHITELIST = "whitelist";
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

    Long tsLong = System.currentTimeMillis()/1000;
    String ts = tsLong.toString();


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," + KEY_USER + " TEXT,"
                + KEY_PASS + " TEXT NOT NULL, " + KEY_EMAIL + " TEXT NOT NULL, " + KEY_INTETOPICS + " TEXT, " + KEY_CREATED + " DATETIME, " + KEY_UPDATED + " DATETIME " + ")";
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

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
        db.execSQL(CREATE_LOCAL_TABLE);
        db.execSQL(CREATE_BLACK_TABLE);
        db.execSQL(CREATE_WHITE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCAL );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLACKLIST );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WHITELIST );
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
        values.put(KEY_GPS, ssid); // Local SSID
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
        Cursor res = db.rawQuery( "SELECT " +KEY_ID + "," + KEY_LOCAL + " FROM " + TABLE_LOCAL, null );
        return res;
    }

    // Deleting local
    public Integer deleteLocal(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_LOCAL, KEY_ID + " = ? ", new String[] { Integer.toString(id) });
    }
}