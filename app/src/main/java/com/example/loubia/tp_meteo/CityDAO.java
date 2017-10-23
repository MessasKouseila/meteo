package com.example.loubia.tp_meteo;

/**
 * Created by loubia on 22/10/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class CityDAO {

    // Champs de la base de données
    private SQLiteDatabase database;
    private CityDB dbHelper;
    private String[] allColumns = {
            CityDB.COLUMN_ID,
            CityDB.COLUMN_NAME,
            CityDB.COLUMN_COUNTRY,
            CityDB.COLUMN_AIR_TEMPERATURE,
            CityDB.COLUMN_PRESSURE,
            CityDB.COLUMN_LAST_REPORT,
            CityDB.COLUMN_WIND_DIRECTION,
            CityDB.COLUMN_WIND_SPEED
    };

    public CityDAO(Context context) {
        dbHelper = new CityDB(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long createCity(City city) {

        ContentValues values = new ContentValues();
        values.put(CityDB.COLUMN_NAME, city.getName());
        values.put(CityDB.COLUMN_COUNTRY, city.getCountry());
        values.put(CityDB.COLUMN_LAST_REPORT, city.getLastReport());
        values.put(CityDB.COLUMN_WIND_SPEED, city.getWindSpeed());
        values.put(CityDB.COLUMN_WIND_DIRECTION, city.getWindDirection());
        values.put(CityDB.COLUMN_AIR_TEMPERATURE, city.getAirTemperature());

        long insertId = database.insert(CityDB.TABLE_CITY, null,
                values);
/*
        Cursor cursor = database.query(CityDB.TABLE_CITY,
                allColumns, CityDB.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        City newCity = cursorToCity(cursor);
        cursor.close();
*/
        return insertId;

    }

    public void deleteCity(City city) {
        long id = city.getId();
        System.out.println("City deleted with id: " + id);
        database.delete(CityDB.TABLE_CITY, CityDB.COLUMN_ID
                + " = " + id, null);
    }

    public List<City> getAllCity() {
        List<City> citys = new ArrayList<City>(256);

        Cursor cursor = database.query(CityDB.TABLE_CITY,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            City city = cursorToCity(cursor);
            citys.add(city);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        return citys;
    }

    private City cursorToCity(Cursor cursor) {
        City city = new City();

        city.setId(cursor.getLong(0));
        city.setName(cursor.getString(1));
        city.setCountry(cursor.getString(2));
        city.setAirTemperature(cursor.getFloat(3));
        city.setPressure(cursor.getFloat(4));
        city.setLastReport(cursor.getString(5));
        city.setWindDirection(cursor.getString(6));
        city.setWindSpeed(cursor.getFloat(7));

        return city;
    }
}