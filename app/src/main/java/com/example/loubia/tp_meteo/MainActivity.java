package com.example.loubia.tp_meteo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    protected List<City> listCity = new ArrayList<City>(256);
    protected ArrayAdapter<City> adapter = null;
    private ListView mListView;

    /**
     * initialisation de la liste des ville de base
     */
    private void initList() {

        this.listCity.add(new City("Brest", "France"));
        this.listCity.add(new City("Marseille", "France"));
        this.listCity.add(new City("Montreal", "Canada"));
        this.listCity.add(new City("Istanbul", "Turkey"));
        this.listCity.add(new City("Seaoul", "Korea"));
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

        this.listCity = this.getCityPref();

        if (this.listCity.isEmpty()) {
            this.initList();
            this.saveCityPref(this.listCity);
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
            this.listCity.remove(this.adapter.getItem(menuinfo.position));
            this.saveCityPref(this.listCity);
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
                this.listCity.add((City) data.getSerializableExtra("City"));
                adapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}