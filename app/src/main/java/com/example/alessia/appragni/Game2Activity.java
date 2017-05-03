package com.example.alessia.appragni;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Game2Activity extends AppCompatActivity {

    int red, green, blue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);

        red = (int) getIntent().getExtras().getInt("Red");
        green = (int) getIntent().getExtras().getInt("Green");
        blue = (int) getIntent().getExtras().getInt("Blue");

        Log.d("Prova", "Valore di red "+red);
        Log.d("Prova", "Valore di green "+green);
        Log.d("Prova", "Valore di blue "+blue);

        ((TextView)findViewById(R.id.redposition)).setText(String.valueOf(red));
        ((TextView)findViewById(R.id.greenposition)).setText(String.valueOf(green));
        ((TextView)findViewById(R.id.blueposition)).setText(String.valueOf(blue));
    }
}
