package com.example.loubia.tp_meteo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddCityActivity extends AppCompatActivity {

    private EditText country_add;
    private EditText city_add;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        city_add = (EditText) findViewById(R.id.city_add);
        country_add = (EditText) findViewById(R.id.country_add);
        btn = (Button) findViewById(R.id.btn_add);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!city_add.getText().toString().isEmpty() && !country_add.getText().toString().isEmpty()) {
                    City tmpCity = new City(city_add.getText().toString(), country_add.getText().toString());
                    Intent intent = new Intent();
                    intent.putExtra("City", tmpCity);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Veuillez remplir le formulaire", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
