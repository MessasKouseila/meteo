package com.example.loubia.tp_meteo;

import java.io.Serializable;

/**
 * Created by loubia on 14/10/17.
 */

public class City implements Serializable {

    protected long id;

    protected  String name;
    protected String country;
    protected String lastReport;
    protected float windSpeed;
    protected String windDirection;
    protected float pressure;
    protected float airTemperature;
    @Override
    public String toString() {
        return this.getName() + " (" + this.getCountry() + ")";
    }



    public City() {
    }

    public City(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getName() { return name; }

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
