package com.example.loubia.tp_meteo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class AddCityActivity extends AppCompatActivity {

    private EditText city_add = (EditText) findViewById(R.id.city);
    private EditText country_add = (EditText) findViewById(R.id.country);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        City tmpCity = new City(city_add.getText().toString(), country_add.getText().toString());
        Intent intent = new Intent();

        intent.putExtra("City", tmpCity);
        setResult(RESULT_OK, intent);
        finish();
    }
}
