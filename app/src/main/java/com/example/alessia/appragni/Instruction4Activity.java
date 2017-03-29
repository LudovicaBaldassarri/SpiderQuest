package com.example.alessia.appragni;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Instruction4Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction4);
    }

    public void backtoMenu (View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void backtothirdInstruction (View view){
        Intent intent = new Intent(this, Instruction3Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
