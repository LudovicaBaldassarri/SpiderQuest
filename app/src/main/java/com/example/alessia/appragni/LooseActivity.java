package com.example.alessia.appragni;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Michele on 03/05/2017.
 */

public class LooseActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loose);
    }

    public void backtoMenu (View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openInizio(View view){
        Intent intent=new Intent(this, Game1Activity.class);
        startActivity(intent);
    }
}
