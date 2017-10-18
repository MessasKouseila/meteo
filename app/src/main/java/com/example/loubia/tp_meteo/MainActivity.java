package com.example.loubia.tp_meteo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    static protected List<City> listCity = new ArrayList<City>(256);
    static protected ArrayAdapter<City> adapter = null;
    private ListView mListView;
    private Update updateWeather;

    private ProgressBar mProgressBar;
    private Button mButton;

    /**
     * initialisation de la liste des ville de base
     */
    private void initList() {

        listCity.add(new City("Brest", "France"));
        listCity.add(new City("Marseille", "France"));
        listCity.add(new City("Montreal", "Canada"));
        listCity.add(new City("Istanbul", "Turkey"));
        listCity.add(new City("Seaoul", "Korea"));
    }

    /**
     * recupere la liste des villes de base à afficher
     *
     * @return
     */
    private List<City> getCityPref() {

        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString("defaultCity", "");
        Type type = new TypeToken<List<City>>() {
        }.getType();
        List<City> obj = gson.fromJson(json, type);
        return obj;
    }

    /**
     * sauvegarde une liste comme liste de ville par default a afficher
     *
     * @param listOfCity la liste a sauvegarder
     * @return
     */
    private boolean saveCityPref(List<City> listOfCity) {

        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listOfCity);
        prefsEditor.putString("defaultCity", json);
        return prefsEditor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        listCity = this.getCityPref();

        if (listCity.isEmpty()) {
            this.initList();
            this.saveCityPref(listCity);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressBar = (ProgressBar) findViewById(R.id.pBAsync);
        mListView = (ListView) findViewById(R.id.listView);

        adapter = new ArrayAdapter<City>(MainActivity.this,
                android.R.layout.simple_list_item_1, listCity);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("City", adapter.getItem(position));
                startActivity(intent);
            }
        });

        registerForContextMenu(mListView);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent(MainActivity.this, AddCityActivity.class);
            startActivityForResult(intent, 1000);
            return true;
        }
        if (id == R.id.action_refresh) {
            updateWeather = new Update();
            updateWeather.execute(MainActivity.listCity);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Supprimer");//groupId, itemId, order, title
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle() == "Supprimer") {
            AdapterView.AdapterContextMenuInfo menuinfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            listCity.remove(this.adapter.getItem(menuinfo.position));
            this.saveCityPref(listCity);
            this.adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Suppression reussie", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1000 && resultCode == RESULT_OK) {
                listCity.add((City) data.getSerializableExtra("City"));
                this.saveCityPref(listCity);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     *
     */
    private class Update extends AsyncTask<List<City>, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Synchronisation en cours ...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Mise à jour de la ProgressBar
            mProgressBar.setProgress(values[0]);
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
                    } else {
                        // je supprime la ville si elle n'existe pas
                        MainActivity.listCity.remove(c);
                    }
                    progress = progress + (100 / cities[0].size());
                    publishProgress(progress);
                }
                // je force la progresse bar a 100%
                publishProgress(100);
                saveCityPref(MainActivity.listCity);

            } catch (MalformedURLException e) {

            } catch (ProtocolException e) {

            } catch (IOException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getApplicationContext(), "Synchronisation Términé", Toast.LENGTH_SHORT).show();
            // on notify des changement sur la list
            MainActivity.adapter.notifyDataSetChanged();
        }
    }


}