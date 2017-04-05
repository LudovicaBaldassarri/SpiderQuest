package com.example.alessia.appragni;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Instruction1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction1);
    }

    public void opensecondInstruction (View view){
        Intent intent = new Intent(this, Instruction2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void backtoMenu (View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public void openInizio(View view){
        Intent intent=new Intent(this, Inizio.class);
        startActivity(intent);
    }
}
