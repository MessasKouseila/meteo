package main.taskSync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import main.activity.MainActivity;
import main.model.City;
import main.model.CityDB;
import main.model.WeatherProvider;


public class SynchronizerAdapter extends AbstractThreadedSyncAdapter {
    ContentResolver mContentResolver;


    public SynchronizerAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Cursor c = MainActivity.resolver.query(WeatherProvider.CONTENT_URI, null, null, null, null, null);
        Long id;
        City cityTmp = new City();
        MainActivity.mProgressBar.setProgress(0);
        int progress = 0;
        while (c.moveToNext()) {
            syncResult.stats.numEntries++;
            id = c.getLong(c.getColumnIndex(CityDB.COLUMN_ID));
            cityTmp.setName(c.getString(c.getColumnIndex(CityDB.COLUMN_NAME)));
            cityTmp.setCountry(c.getString(c.getColumnIndex(CityDB.COLUMN_COUNTRY)));

            try {
                URL url = new URL("https://query.yahooapis.com/v1/public/yql?q=select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"" + cityTmp.getName() + "\")&format=json&env=store://datatables.org/alltableswithkeys");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                List<String> res = new JSONResponseHandler().handleResponse(in, "UTF-8");
                Log.d("UPDATEVILLE", res.toString());
                cityTmp.setWindSpeed(Float.parseFloat(res.get(0).split(" ")[0]));
                cityTmp.setWindDirection(res.get(0).split(" ")[1].replace("(", ""));
                cityTmp.setAirTemperature(Float.parseFloat(res.get(1)));
                cityTmp.setPressure(Float.parseFloat(res.get(2)));
                cityTmp.setLastReport(res.get(3));
                Log.d("UPDATEVILLE", cityTmp.toString());
                ContentValues values = cityTmp.getContent();
                MainActivity.resolver.update(WeatherProvider.CONTENT_URI, values,"_id = ? ", new String[]{id+""});
                progress = progress + (100 / c.getCount());
                MainActivity.mProgressBar.setProgress(progress);
            } catch (Exception e1) {
                e1.printStackTrace();
                try {
                    MainActivity.resolver.delete(WeatherProvider.CONTENT_URI, "_id = ? ", new String[]{id+""});
                } catch (Exception e2){
                    e1.printStackTrace();
                }
            }

        }
        MainActivity.mProgressBar.setProgress(100);
    }

    public static void  performSync(){
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.setSyncAutomatically(AuthentificatorService.GetAccount(), WeatherProvider.AUTHORITY,true);
        ContentResolver.requestSync(AuthentificatorService.GetAccount(), WeatherProvider.AUTHORITY, b);
    }
}
