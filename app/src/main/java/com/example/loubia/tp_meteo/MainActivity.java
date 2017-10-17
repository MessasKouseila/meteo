package com.example.loubia.tp_meteo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    static protected List<City> listCity = new ArrayList<City>(256);
    protected ArrayAdapter<City> adapter = null;
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        // On récupère les composants de notre layout
        mProgressBar = (ProgressBar) findViewById(R.id.pBAsync);
        mButton = (Button) findViewById(R.id.btnLaunch);

        // On met un Listener sur le bouton
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                updateWeather = new Update(listCity);
                updateWeather.execute(listCity);
            }
        });

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
    private class Update extends AsyncTask<List<City>, Integer, String> {
        private List<City> list;
        private Update(List<City> list) {
            this.list = list;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Début du traitement asynchrone", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Mise à jour de la ProgressBar
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(List<City>... cities) {
            int progress = 0;
            try {
                JSONResponseHandler jRH = new JSONResponseHandler();
                String search;
                String urlText;
                for(City c : cities[0]) {
                    search = c.toString();
                    urlText = "https://query.yahooapis.com/v1/public/yql?q=select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\""+ search +"\")&format=json&env=store://datatables.org/alltableswithkeys";
                    URL url = new URL(urlText);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    List<String> res = jRH.handleResponse(connection.getInputStream(),null);
                    Log.d("MainActivity", res.toString());
                    progress = progress + (100/cities[0].size());
                    publishProgress(progress);

                    c.setWindSpeed(Float.parseFloat(res.get(0).split(" ")[0]));
                    c.setWindDirection(res.get(0).split(" ")[1].replace("(", ""));
                    c.setAirTemperature(Float.parseFloat(res.get(1)));
                    c.setPressure(Float.parseFloat(res.get(2)));
                    c.setLastReport(res.get(3));
                }
                saveCityPref(MainActivity.listCity);

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Le traitement asynchrone est terminé", Toast.LENGTH_LONG).show();
        }
    }


}