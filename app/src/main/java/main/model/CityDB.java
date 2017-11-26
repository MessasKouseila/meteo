package main.model;

/**
 * Created by loubia on 22/10/17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CityDB extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "city";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_LAST_REPORT = "last_report";
    public static final String COLUMN_WIND_SPEED = "wind_speed";
    public static final String COLUMN_WIND_DIRECTION = "wind_direction";
    public static final String COLUMN_PRESSURE = "pressure";
    public static final String COLUMN_AIR_TEMPERATURE = "air_temperature";


    private static final String DATABASE_NAME = "city.db";
    private static final int DATABASE_VERSION = 1;
    private List<City> listCity = new ArrayList<City>(128);

    // Commande sql pour la création de la base de données
    private static final String DATABASE_CREATE = " CREATE TABLE "
            + TABLE_NAME + " ( " +
            COLUMN_ID + " integer PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " INTEGER NOT NULL, " +
            COLUMN_COUNTRY + " INTEGER NOT NULL, " +
            COLUMN_LAST_REPORT + " TEXT, " +
            COLUMN_WIND_SPEED + " TEXT, " +
            COLUMN_WIND_DIRECTION + " TEXT, " +
            COLUMN_PRESSURE + " TEXT, " +
            COLUMN_AIR_TEMPERATURE + " TEXT, " +
            "UNIQUE ("+COLUMN_NAME+","+ COLUMN_COUNTRY+") " +
            ");";

    public CityDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void initList() {
        listCity.add(new City("Brest", "France"));
        listCity.add(new City("Marseille", "France"));
        listCity.add(new City("Montreal", "Canada"));
        listCity.add(new City("Istanbul", "Turkey"));
        listCity.add(new City("Seaoul", "Korea"));
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        initList();
        database.execSQL(DATABASE_CREATE);
        for (City city : listCity) {
            database.insert(TABLE_NAME, null, city.getContent());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(CityDB.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public static final String sortOrder = CityDB.COLUMN_NAME+ " ASC";

    public static final String[] projection = {
            CityDB.COLUMN_ID,
            CityDB.COLUMN_COUNTRY,
            CityDB.COLUMN_NAME,
            CityDB.COLUMN_WIND_SPEED,
            CityDB.COLUMN_AIR_TEMPERATURE,
            CityDB.COLUMN_PRESSURE,
            CityDB.COLUMN_LAST_REPORT,
            CityDB.COLUMN_WIND_DIRECTION
    };
}
