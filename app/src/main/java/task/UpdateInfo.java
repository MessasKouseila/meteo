package task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import activity.MainActivity;
import model.City;
import model.CityDAO;

/**
 * Created by loubia on 23/10/17.
 */

public class UpdateInfo extends AsyncTask<List<City>, Integer, Void> {

    private Context contextApp;
    private ProgressBar ProgressBarApp;
    private CityDAO dataBase;
    private List<City> listCity;
    private ArrayAdapter<City> adapter;

    public UpdateInfo(Context ctx, CityDAO bdd, ProgressBar pBar, List<City> list, ArrayAdapter<City> adapter) {
        this.contextApp = ctx;
        this.ProgressBarApp = pBar;
        this.dataBase = bdd;
        this.listCity = list;
        this.adapter = adapter;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(this.contextApp, "Synchronisation en cours ...", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        // Mise à jour de la ProgressBar
        this.ProgressBarApp.setProgress(values[0]);
    }

    @Override
    protected Void doInBackground(List<City>... cities) {
        int progress = 0;
        List<String> res;
        String urlText = "";
        try {
            for (City c : cities[0]) {
                urlText = "https://query.yahooapis.com/v1/public/yql?q=select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"" + c.toString() + "\")&format=json&env=store://datatables.org/alltableswithkeys";
                URL url = new URL(urlText);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                res = new JSONResponseHandler().handleResponse(connection.getInputStream(), null);
                // si la ville n'est pas reconnu
                if (res.get(0) != null) {
                    c.setWindSpeed(Float.parseFloat(res.get(0).split(" ")[0]));
                    c.setWindDirection(res.get(0).split(" ")[1].replace("(", ""));
                    c.setAirTemperature(Float.parseFloat(res.get(1)));
                    c.setPressure(Float.parseFloat(res.get(2)));
                    c.setLastReport(res.get(3));
                    this.dataBase.updateCity(c);
                } else {
                    // je supprime la ville si elle n'existe pas
                    this.listCity.remove(c);
                    this.dataBase.deleteCity(c);
                }
                progress = progress + (100 / cities[0].size());
                publishProgress(progress);
            }
            // je force la progresse bar a 100%
            publishProgress(100);
            //saveCityPref(MainActivity.listCity);

        } catch (MalformedURLException e) {
            Toast.makeText(this.contextApp, "Echec Synchronisation", Toast.LENGTH_SHORT).show();
        } catch (ProtocolException e) {
            Toast.makeText(this.contextApp, "Echec Synchronisation", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this.contextApp, "Echec Synchronisation", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Toast.makeText(this.contextApp, "Synchronisation Términé", Toast.LENGTH_SHORT).show();
        // on notify des changements sur la list
        this.adapter.notifyDataSetChanged();
    }
}