package com.example.asignment.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_CARS = "cars";
    public static final String CAR_COLUMN_ID = "id";
    public static final String CAR_COLUMN_MODEL = "model";
    public static final String CAR_COLUMN_COMPANY = "company";
    public static final String CAR_COLUMN_DESCRIPTION = "description";
    public static final String CAR_COLUMN_PRICE = "price";
    public static final String CAR_COLUMN_STATUS = "status";
    public static final String CAR_COLUMN_IMAGE = "imagePath";
    public static final String CAR_COLUMN_USERID = "userId";

    public static final String TABLE_USERS = "users";
    public static final String USER_COLUMN_ID = "id";
    public static final String USER_COLUMN_EMAIL = "email";
    public static final String USER_COLUMN_PASSWORD = "password";
    public static final String USER_COLUMN_NAME = "name";
    public static final String USER_COLUMN_PHONE_NUMBER = "phoneNumber";

    public static final String TABLE_BILLS = "bills";
    public static final String BILL_COLUMN_ID = "id";
    public static final String BILL_COLUMN_USERID = "userId";
    public static final String BILL_COLUMN_CARID = "carId";
    public static final String BILL_COLUMN_TOTAL = "total";

    private static final String DATABASE_NAME = "carManage.db";
    private static final int DATABASE_VERSION = 1;

    private static SQLiteHelper instance;

    private static final String CREATE_TABLE_CARS = "CREATE TABLE " + TABLE_CARS + " ( "
            + CAR_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CAR_COLUMN_MODEL + " TEXT NOT NULL, "
            + CAR_COLUMN_COMPANY + " TEXT NOT NULL, "
            + CAR_COLUMN_DESCRIPTION + " TEXT NOT NULL, "
            + CAR_COLUMN_PRICE + " INTEGER NOT NULL, "
            + CAR_COLUMN_STATUS + " INTEGER NOT NULL, "
            + CAR_COLUMN_IMAGE + " TEXT NOT NULL, "
            + CAR_COLUMN_USERID + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + CAR_COLUMN_USERID + ") REFERENCES " + TABLE_USERS + "(" + USER_COLUMN_ID + "));";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ( "
            + USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + USER_COLUMN_EMAIL + " TEXT NOT NULL, "
            + USER_COLUMN_PASSWORD + " TEXT NOT NULL, "
            + USER_COLUMN_NAME + " TEXT NOT NULL, "
            + USER_COLUMN_PHONE_NUMBER + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_BILLS = "CREATE TABLE " + TABLE_BILLS + " ( "
            + BILL_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BILL_COLUMN_USERID + " INTEGER, "
            + BILL_COLUMN_CARID + " INTEGER, "
            + BILL_COLUMN_TOTAL + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + BILL_COLUMN_USERID + ") REFERENCES " + TABLE_USERS + "(" + USER_COLUMN_ID + "), "
            + "FOREIGN KEY(" + BILL_COLUMN_CARID + ") REFERENCES " + TABLE_CARS + "(" + CAR_COLUMN_ID + "));";

    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized SQLiteHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CARS);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_BILLS);

        db.execSQL("INSERT INTO " + TABLE_USERS + " (" +
                USER_COLUMN_EMAIL + ", " +
                USER_COLUMN_PASSWORD + ", " +
                USER_COLUMN_NAME + ", " +
                USER_COLUMN_PHONE_NUMBER + ") VALUES (" +
                "'admin1@example.com', " +
                "'adminpass1', " +
                "'Admin One', " +
                "'1234567890')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILLS);
        onCreate(db);
    }
}
