package com.ag.noreader.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class DataBaseHandler extends  SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "NoReaderDB";
// Table Name

    private static final String USERS_TABLE = "user";
    // Demo Table Columns names
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PASS = "pass";
    private static final String ATTEMPTS = "attempts";

    public DataBaseHandler (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
// TODO Auto-generated method stub
        String CREATE_DEMO_TABLE = "CREATE TABLE " + USERS_TABLE + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " TEXT,"
                + PASS + " TEXT" + ")";
        db.execSQL(CREATE_DEMO_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
// Drop older table if existed

        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        onCreate(db);
    }
    // Saving to DB
    public void saveDemo_data(String name, String pass){
// TODO Auto-generated method stub

        System.out.println(name +" " + pass);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(PASS, pass);

        db.insert(USERS_TABLE, null, values);

        db.close();
    }
    // Getting row count from table
    public int getTable_Count(){

        String selectQuery = null;
        selectQuery = "SELECT  * FROM " + USERS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount();
    }
}