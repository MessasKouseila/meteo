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
    static protected CityDAO dataSource;

    private ListView mListView;
    private UpdateInfo updateWeather;

    private ProgressBar mProgressBar;
    private Button mButton;

    /**
     * initialisation de la liste des ville de base
     */
    private void initList() {

        dataSource = new CityDAO(this);
        dataSource.open();
        listCity = dataSource.getAllCity();
        if (listCity.isEmpty()) {

            listCity.add(new City("Brest", "France"));
            listCity.add(new City("Marseille", "France"));
            listCity.add(new City("Montreal", "Canada"));
            listCity.add(new City("Istanbul", "Turkey"));
            listCity.add(new City("Seaoul", "Korea"));

            for(City c : listCity) {
                dataSource.insertCity(c);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.initList();
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
            updateWeather = new UpdateInfo(getApplicationContext(), dataSource, mProgressBar);
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
            City cityTmp = this.adapter.getItem(menuinfo.position);
            listCity.remove(cityTmp);
            dataSource.deleteCity(cityTmp);
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
                dataSource.insertCity((City) data.getSerializableExtra("City"));
                adapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}