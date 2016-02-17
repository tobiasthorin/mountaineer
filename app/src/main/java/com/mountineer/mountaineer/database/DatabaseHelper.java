package com.mountineer.mountaineer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tobias on 16/02/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Error codes
    public static final int INSERTION_SUCCESS = 1;
    public static final int INSERTION_DUPLICATE = 2;
    public static final int INSERTION_FAILURE = -1;

    //Database variables
    public static final String DATABASE_NAME = "Mountaineer.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "location_history";

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public int insertData(String date, String location, String altitude) {

        db = this.getWritableDatabase();

        //Check for duplicate
        boolean entryExists = checkForDuplicate(new String[] {"DATE", "LOCATION", "ALTITUDE"}, new String[] {date, location, altitude});

        if (entryExists) {
            return INSERTION_DUPLICATE;
        } else {

            ContentValues contentValues = new ContentValues();
            contentValues.put("DATE", date);
            contentValues.put("LOCATION", location);
            contentValues.put("ALTITUDE", altitude);

            long result = db.insert(TABLE_NAME, null, contentValues);

            if (result == -1) {
                return INSERTION_FAILURE;
            } else {
                return INSERTION_SUCCESS;
            }
        }
    }

    private boolean checkForDuplicate(String[] keys, String[] values) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < keys.length; i++) {
            sb.append(keys[i])
                    .append("=\"")
                    .append(values[i])
                    .append("\" ");
            if (i<keys.length-1) sb.append("AND ");
        }

        Cursor cursor = db.query(TABLE_NAME, null, sb.toString(), null, null, null, null);

        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public Cursor getAllData() {
        db = this.getWritableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, DATE TEXT, LOCATION TEXT, ALTITUDE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
