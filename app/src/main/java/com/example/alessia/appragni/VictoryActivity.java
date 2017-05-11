package com.example.alessia.appragni;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by ludo_monkey on 03/05/17.
 */

public class VictoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victory);
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
