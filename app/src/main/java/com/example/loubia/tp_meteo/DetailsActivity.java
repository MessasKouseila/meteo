package com.example.loubia.tp_meteo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    private TextView ville;
    private TextView pays;
    private TextView vent;
    private TextView direction;
    private TextView temperature;
    private TextView pression;
    private TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ville = (TextView) findViewById(R.id.idVille);
        pays = (TextView) findViewById(R.id.idPays);
        vent = (TextView) findViewById(R.id.idVent);
        direction = (TextView) findViewById(R.id.idDirection);
        temperature = (TextView) findViewById(R.id.idTempérature);
        pression = (TextView) findViewById(R.id.idPression);
        date = (TextView) findViewById(R.id.idDate);

        Intent intent = getIntent();
        if (intent != null) {
            City _city = (City) intent.getSerializableExtra("City");
            ville.setText("Ville    " + _city.getName());
            pays.setText("Pays    " + _city.getCountry());
            vent.setText("Vent    " + _city.getWindSpeed());
            direction.setText("Direction    " + _city.getWindDirection());
            temperature.setText("Température    " + _city.getAirTemperature());
            pression.setText("Pression    " + _city.getPressure());
            date.setText("Date  " + _city.getLastReport());
        }

    }

}
