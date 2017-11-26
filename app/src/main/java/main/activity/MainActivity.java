package main.activity;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.loubia.tp_meteo.R;

import main.model.City;
import main.model.CityDB;
import main.model.WeatherProvider;
import main.taskSync.AuthentificatorService;
import main.taskSync.SynchronizerAdapter;

import static main.model.City.custorToCity;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    public static ContentResolver resolver;
    public static ProgressBar mProgressBar;

    protected static CursorAdapter adapter;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mListView = (ListView) findViewById(R.id.listView);
        mProgressBar = (ProgressBar) findViewById(R.id.pBAsync);
        setSupportActionBar(toolbar);

        resolver = getContentResolver();
        AuthentificatorService.CreateSyncAccount(this);

        String[] columns = new String[]{CityDB.COLUMN_NAME, CityDB.COLUMN_COUNTRY};
        int[] to = new int[]{android.R.id.text1, android.R.id.text2};
        Cursor cursor = resolver.query(WeatherProvider.CONTENT_URI, CityDB.projection, null, null, CityDB.sortOrder);

        adapter = new SimpleCursorAdapter(getApplicationContext(), android.R.layout.simple_list_item_2, cursor, columns, to, 0);

        mListView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.getCursor().moveToPosition(position);
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("City", custorToCity(adapter.getCursor()));
                startActivity(intent);
            }
        });
        registerForContextMenu(mListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Intent intent = new Intent(MainActivity.this, AddCityActivity.class);
            startActivityForResult(intent, 1000);
            return true;
        }
        if (id == R.id.action_refresh) {
            Log.d("button refresh", "button pressed ");
            SynchronizerAdapter.performSync();
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
            long id = resolver.delete(WeatherProvider.CONTENT_URI, "_id = ? ", new String[]{adapter.getItemId(menuinfo.position) + ""});
            if (id != 0) {
                this.adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Suppression reussit", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Suppression échoué", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            try {
                getContentResolver().insert(WeatherProvider.CONTENT_URI, ((City) data.getSerializableExtra("City")).getContent());
                adapter.notifyDataSetChanged();
            } catch (Exception e)  {
                Toast.makeText(MainActivity.this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getApplicationContext(),
                WeatherProvider.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


}