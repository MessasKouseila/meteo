package main.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by loubia on 14/10/17.
 */

public class City implements Serializable {

    protected long id;

    protected String name;
    protected String country;
    protected String lastReport;
    protected float windSpeed;
    protected String windDirection;
    protected float pressure;
    protected float airTemperature;

    public City() {
    }


    public City(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public static City custorToCity(Cursor cursor) {
        City city = new City();
        city.setName(cursor.getString(cursor.getColumnIndex(CityDB.COLUMN_NAME)));
        city.setCountry(cursor.getString(cursor.getColumnIndex(CityDB.COLUMN_COUNTRY)));
        city.setLastReport(cursor.getString(cursor.getColumnIndex(CityDB.COLUMN_LAST_REPORT)));
        city.setWindDirection(cursor.getString(cursor.getColumnIndex(CityDB.COLUMN_WIND_DIRECTION)));
        city.setPressure(Float.parseFloat(cursor.getString(cursor.getColumnIndex(CityDB.COLUMN_PRESSURE))));
        city.setAirTemperature(Float.parseFloat(cursor.getString(cursor.getColumnIndex(CityDB.COLUMN_AIR_TEMPERATURE))));
        city.setWindSpeed(Float.parseFloat(cursor.getString(cursor.getColumnIndex(CityDB.COLUMN_WIND_SPEED))));
        return city;
    }

    @Override
    public String toString() {
        return this.getName() + " (" + this.getCountry() + ")";
    }

    public ContentValues getContent() {
        ContentValues content = new ContentValues();
        content.put(CityDB.COLUMN_NAME, name);
        content.put(CityDB.COLUMN_COUNTRY, country);
        content.put(CityDB.COLUMN_AIR_TEMPERATURE, airTemperature);
        content.put(CityDB.COLUMN_LAST_REPORT, lastReport);
        content.put(CityDB.COLUMN_PRESSURE, pressure);
        content.put(CityDB.COLUMN_WIND_DIRECTION, windDirection);
        content.put(CityDB.COLUMN_WIND_SPEED, windSpeed);
        return content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLastReport() {
        return lastReport;
    }

    public void setLastReport(String lastReport) {
        this.lastReport = lastReport;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(float airTemperature) {
        this.airTemperature = airTemperature;
    }
}
