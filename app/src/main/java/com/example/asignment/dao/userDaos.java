package com.example.asignment.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.asignment.database.SQLiteHelper;
import com.example.asignment.models.userModels;

public class userDaos {
    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;
    private String[] allColumn = {
            SQLiteHelper.USER_COLUMN_ID,
            SQLiteHelper.USER_COLUMN_EMAIL,
            SQLiteHelper.USER_COLUMN_PASSWORD,
            SQLiteHelper.USER_COLUMN_NAME,
            SQLiteHelper.USER_COLUMN_PHONE_NUMBER
    };

    private static userDaos instance;

    private userDaos(Context context) {
        dbHelper = SQLiteHelper.getInstance(context);
    }

    public static synchronized userDaos getInstance(Context context) {
        if (instance == null) {
            instance = new userDaos(context.getApplicationContext());
        }
        return instance;
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public userModels signup(String pEmail,
                             String pPassword,
                             String pName,
                             String pPhoneNumber) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.USER_COLUMN_EMAIL, pEmail);
        values.put(SQLiteHelper.USER_COLUMN_PASSWORD, pPassword);
        values.put(SQLiteHelper.USER_COLUMN_NAME, pName);
        values.put(SQLiteHelper.USER_COLUMN_PHONE_NUMBER, pPhoneNumber);

        long insertId = db.insert(SQLiteHelper.TABLE_USERS, null, values);
        if (insertId == -1) {
            Log.e("SQLite", "Failed to insert user");
            return null;
        }

        Cursor cursor = db.query(SQLiteHelper.TABLE_USERS,
                allColumn,
                SQLiteHelper.USER_COLUMN_ID + " = ? ",
                new String[]{String.valueOf(insertId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            userModels newUser = cursorToUser(cursor);
            cursor.close();
            return newUser;
        } else {
            Log.e("SQLite", "No User found with id: " + insertId);
            return null;
        }
    }

    public userModels signin(String pEmail,
                             String pPassword) {
        Cursor cursor = db.query(SQLiteHelper.TABLE_USERS,
                allColumn,
                SQLiteHelper.USER_COLUMN_EMAIL
                        + " = ? AND "
                        + SQLiteHelper.USER_COLUMN_PASSWORD
                        + " = ? ",
                new String[]{pEmail, pPassword},
                null, null, null);

        userModels user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        } else {
            Log.e("SQLite", "No User found with email: " + pEmail);
        }

        return user;
    }

    private userModels cursorToUser(Cursor cursor) {
       userModels user = new userModels();
       user.setId(cursor.getLong(0));
       user.setEmail(cursor.getString(1));
       user.setPassword(cursor.getString(2));
       user.setName(cursor.getString(3));
       user.setPhoneNumber(cursor.getString(4));
       return user;
    }
}
