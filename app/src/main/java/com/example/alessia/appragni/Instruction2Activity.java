package com.example.alessia.appragni;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Instruction2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction2);
    }

    public void backtoMenu (View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void backtofirstInstruction (View view){
        Intent intent = new Intent(this, Instruction1Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openthirdInstruction (View view){
        Intent intent = new Intent(this, Instruction3Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
