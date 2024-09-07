package com.example.asignment.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.asignment.database.SQLiteHelper;
import com.example.asignment.models.billModels;

public class billDaos {
    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;
    private String[] allColumn = {
        SQLiteHelper.BILL_COLUMN_ID,
        SQLiteHelper.BILL_COLUMN_USERID,
        SQLiteHelper.BILL_COLUMN_CARID,
        SQLiteHelper.BILL_COLUMN_TOTAL
    };

    private static billDaos instance;

    private billDaos(Context context) {
        dbHelper = SQLiteHelper.getInstance(context);
    }

    public static synchronized billDaos getInstance(Context context) {
        if (instance == null) {
            instance = new billDaos(context.getApplicationContext());
        }
        return instance;
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public billModels createBill(Long pUserId,
                                 Long pCarId,
                                 int pTotal) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.BILL_COLUMN_USERID, pUserId);
        values.put(SQLiteHelper.BILL_COLUMN_CARID, pCarId);
        values.put(SQLiteHelper.BILL_COLUMN_TOTAL, pTotal);

        long insertId = db.insert(SQLiteHelper.TABLE_BILLS, null, values);
        if (insertId == -1) {
            Log.e("SQLite", "Failed to insert bill");
            return null;
        }

        ContentValues carValues = new ContentValues();
        carValues.put(SQLiteHelper.CAR_COLUMN_STATUS, 1);

        int rowsUpdated = db.update(
                SQLiteHelper.TABLE_CARS,
                carValues,
                SQLiteHelper.CAR_COLUMN_ID + " = ? ",
                new String[]{String.valueOf(pCarId)}
        );

        if (rowsUpdated == 0) {
            Log.e("SQLite", "Failed to update car status for carId: " + pCarId);
        }

        Cursor cursor = db.query(SQLiteHelper.TABLE_BILLS,
                allColumn,
                SQLiteHelper.BILL_COLUMN_ID + " = ? ",
                new String[]{String.valueOf(insertId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            billModels newBill = cursorToBill(cursor);
            cursor.close();
            return newBill;
        } else {
            Log.e("SQLite", "No Bill found with id: " + insertId);
            return null;
        }
    }

    private billModels cursorToBill(Cursor cursor) {
        billModels bill = new billModels();
        bill.setId(cursor.getLong(0));
        bill.setId_user(cursor.getLong(1));
        bill.setId_car(cursor.getLong(2));
        bill.setTotal(cursor.getInt(3));
        return bill;
    }
}
