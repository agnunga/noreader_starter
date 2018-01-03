package com.ag.noreader.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHandler extends  SQLiteOpenHelper {
    private static final String TAG = DataBaseHandler.class.getName();
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "NoReaderDB";
// Table Name

    private static final String USERS_TABLE = "user";
    // Demo Table Columns names
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String PASS = "pass";
//    private static final String ATTEMPTS = "attempts";

    public DataBaseHandler (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
// TODO Auto-generated method stub
        String createUserTable = "CREATE TABLE " + USERS_TABLE + "("
                + ID + " INTEGER PRIMARY KEY," //AUTOINCREMENT
                + NAME + " TEXT NOT NULL,"
                + EMAIL + " TEXT  NOT NULL UNIQUE,"
                + PASS + " TEXT NOT NULL" + ")";
        db.execSQL(createUserTable);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
// Drop older table if existed

        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        onCreate(db);
    }
    // Saving to DB
    public boolean saveData(User user){
// TODO Auto-generated method stub
        boolean inserted = false;
        System.out.println(user.getName() +" " + user.getEmail() +" " + user.getPassword());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EMAIL, user.getEmail());
        values.put(NAME, user.getName());
        values.put(PASS, user.getPassword());

        if(db.insert(USERS_TABLE, null, values) > 0){
            inserted = true;
        }

        db.close();
        return  inserted;
    }

    // Retrieve from DB
    public List<User> retrieveData() {
        String selectQuery = "SELECT  * FROM " + USERS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<User> users = new ArrayList<>();
        if (cursor.moveToFirst()) {
            User user = new User();
            do {
                user.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(PASS)));
                users.add(user);
            } while (cursor.moveToNext());
        }
        db.close();
        return users;
    }

    // Getting row count from table
    public int getTable_Count(){

        String selectQuery = null;
        selectQuery = "SELECT  * FROM " + USERS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount();
    }

    public User processLogin(String email, String password) {
        String selectQuery = "SELECT  * FROM " + USERS_TABLE +" " +
                " where " + EMAIL + " = '" + email + "' AND " + PASS + " = '" + password + "'";

        Log.d(TAG, " Query ::: " + selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        User user = new User();
        if (cursor.moveToFirst()) {
            do {
                user.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(PASS)));
            } while (cursor.moveToNext());
        }
        db.close();
        return user;
    }
}