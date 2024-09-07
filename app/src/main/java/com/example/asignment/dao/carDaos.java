package com.example.asignment.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.asignment.database.SQLiteHelper;
import com.example.asignment.models.carModels;

import java.util.ArrayList;
import java.util.List;

public class carDaos {
    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;
    private String[] allColumn = {
            SQLiteHelper.CAR_COLUMN_ID,
            SQLiteHelper.CAR_COLUMN_MODEL,
            SQLiteHelper.CAR_COLUMN_COMPANY,
            SQLiteHelper.CAR_COLUMN_DESCRIPTION,
            SQLiteHelper.CAR_COLUMN_PRICE,
            SQLiteHelper.CAR_COLUMN_STATUS,
            SQLiteHelper.CAR_COLUMN_IMAGE,
            SQLiteHelper.CAR_COLUMN_USERID
    };

    private static carDaos instance;

    private carDaos(Context context) {
        dbHelper = SQLiteHelper.getInstance(context);
    }

    public static synchronized carDaos getInstance(Context context) {
        if (instance == null) {
            instance = new carDaos(context.getApplicationContext());
        }
        return instance;
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public carModels createCar(String pModel,
                               String pCompany,
                               String pDescription,
                               int pPrice,
                               String pImagePath,
                               long pUserId) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.CAR_COLUMN_MODEL, pModel);
        values.put(SQLiteHelper.CAR_COLUMN_COMPANY, pCompany);
        values.put(SQLiteHelper.CAR_COLUMN_DESCRIPTION, pDescription);
        values.put(SQLiteHelper.CAR_COLUMN_PRICE, pPrice);
        values.put(SQLiteHelper.CAR_COLUMN_STATUS, 0);
        values.put(SQLiteHelper.CAR_COLUMN_IMAGE, pImagePath);
        values.put(SQLiteHelper.CAR_COLUMN_USERID, pUserId);

        long insertId = db.insert(SQLiteHelper.TABLE_CARS, null, values);
        if (insertId == -1) {
            Log.e("SQLite", "Failed to insert car");
            return null;
        }

        Cursor cursor = db.query(SQLiteHelper.TABLE_CARS,
                allColumn,
                SQLiteHelper.CAR_COLUMN_ID + " = ? ",
                new String[]{String.valueOf(insertId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            carModels newCar = cursorToCar(cursor);
            cursor.close();
            return newCar;
        } else {
            Log.e("SQLite", "No Car found with id: " + insertId);
            return null;
        }
    }

    public List<carModels> getAllCars() {
        List<carModels> cars = new ArrayList<carModels>();

        Cursor cursor = db.query(
                SQLiteHelper.TABLE_CARS,
                allColumn,
                SQLiteHelper.CAR_COLUMN_STATUS + " = ? ",
                new String[]{String.valueOf(0)},
                null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            carModels car = cursorToCar(cursor);
            cars.add(car);
            cursor.moveToNext();
        }

        cursor.close();
        return cars;
    }

    public List<carModels> getCarsByBillUserId(long userId) {
        List<carModels> cars = new ArrayList<>();

        Cursor billCursor = db.query(
                SQLiteHelper.TABLE_BILLS,
                new String[]{SQLiteHelper.BILL_COLUMN_CARID},
                SQLiteHelper.BILL_COLUMN_USERID + " = ? ",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (billCursor != null) {
            try {
                int carIdColumnIndex = billCursor.getColumnIndex(SQLiteHelper.BILL_COLUMN_CARID);
                if (carIdColumnIndex == -1) {
                    Log.e("SQLite", "Column " + SQLiteHelper.BILL_COLUMN_CARID + " does not exist");
                    return cars;
                }

                while (billCursor.moveToNext()) {
                    long carId = billCursor.getLong(carIdColumnIndex);

                    Cursor carCursor = db.query(
                            SQLiteHelper.TABLE_CARS,
                            allColumn,
                            SQLiteHelper.CAR_COLUMN_ID + " = ? ",
                            new String[]{String.valueOf(carId)},
                            null, null, null);

                    if (carCursor != null && carCursor.moveToFirst()) {
                        carModels car = cursorToCar(carCursor);
                        cars.add(car);
                        carCursor.close();
                    }
                }
            } finally {
                billCursor.close();
            }
        } else {
            Log.e("SQLite", "No Bills found for userId: " + userId);
        }

        return cars;
    }

    public List<carModels> getCarsByUserId(long userId) {
        List<carModels> cars = new ArrayList<>();

        Cursor cursor = db.query(
                SQLiteHelper.TABLE_CARS,
                allColumn,
                SQLiteHelper.BILL_COLUMN_USERID + " = ? AND "
                        + SQLiteHelper.CAR_COLUMN_STATUS + " = ? ",
                new String[]{String.valueOf(userId), String.valueOf(0)},
                null, null, null);

        if (cursor != null) {
            try {
                cursor.moveToFirst();
                while(!cursor.isAfterLast()) {
                    carModels car = cursorToCar(cursor);
                    cars.add(car);
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        } else {
            Log.e("SQLite", "No car found for userId: " + userId);
        }

        return cars;
    }

    public carModels findCarById(long carId) {
        carModels car = null;
        Cursor cursor = null;

        try {
            cursor = db.query(
                    SQLiteHelper.TABLE_CARS,
                    allColumn,
                    SQLiteHelper.CAR_COLUMN_ID + " = ? ",
                    new String[]{String.valueOf(carId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                car = cursorToCar(cursor);
            } else {
                Log.e("SQLite", "No car found with id: " + carId);
            }
        } catch (Exception e) {
            Log.e("SQLite", "Error while trying to find car by id: " + carId, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return car;
    }

    public boolean deleteCar(long carId) {
        int rowAffected = db.delete(SQLiteHelper.TABLE_CARS,
                SQLiteHelper.CAR_COLUMN_ID + " = " + carId,
                null);

        if (rowAffected > 0) {
            Log.i("SQLite", "Car deleted successfully with id: " + carId);
            return true;
        } else {
            Log.e("SQLite", "Failed to delete car with id: " + carId);
            return false;
        }
    }

    public boolean updateCar(long carId,
                             String pModel,
                             String pCompany,
                             String pDescription,
                             int pPrice,
                             String pImagePath) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.CAR_COLUMN_MODEL, pModel);
        values.put(SQLiteHelper.CAR_COLUMN_COMPANY, pCompany);
        values.put(SQLiteHelper.CAR_COLUMN_DESCRIPTION, pDescription);
        values.put(SQLiteHelper.CAR_COLUMN_PRICE, pPrice);
        values.put(SQLiteHelper.CAR_COLUMN_IMAGE, pImagePath);

        int rowAffected = db.update(SQLiteHelper.TABLE_CARS,
                values,
                SQLiteHelper.CAR_COLUMN_ID + " = ? ",
                new String[]{String.valueOf(carId)});

        if (rowAffected > 0) {
            Log.i("SQLite", "Car updated successfully with id: " + carId);
            return true;
        } else {
            Log.e("SQLite", "Failed to update car with id: " + carId);
            return false;
        }
    }

    private carModels cursorToCar(Cursor cursor) {
        carModels car = new carModels();
        car.setId(cursor.getLong(0));
        car.setModel(cursor.getString(1));
        car.setCompany(cursor.getString(2));
        car.setDescription(cursor.getString(3));
        car.setPrice(cursor.getInt(4));
        car.setStatus(cursor.getInt(5));
        car.setImagePath(cursor.getString(6));
        car.setUserId(cursor.getLong(7));
        return car;
    }
}







