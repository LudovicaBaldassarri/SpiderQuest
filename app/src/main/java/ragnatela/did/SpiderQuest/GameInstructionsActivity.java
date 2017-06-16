package ragnatela.did.SpiderQuest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class GameInstructionsActivity extends AppCompatActivity {

    String host_url;
    int host_port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_instructions1);

        //prelevo host url e port
        host_url = getIntent().getExtras().getString("hostUrl");
        host_port = getIntent().getExtras().getInt("hostPort");
    }

//    protected void onResume(){
//        super.onResume();
//        if(!GameMenuActivity.mp.isPlaying()){
//            GameMenuActivity.mp.start();
//            GameMenuActivity.mp.setLooping(true);
//        }
//    }
//
//    @Override
//    protected void onPause(){
//        super.onPause();
//        GameMenuActivity.mp.pause();
//    }

    public void openFirstInstruction(View view) {
        setContentView(R.layout.activity_game_instructions1);
    }

    public void openSecondInstruction(View view){
        setContentView(R.layout.activity_game_instructions2);
    }

    public void openThirdInstruction(View view){
        setContentView(R.layout.activity_game_instructions3);
    }

    public void openFourthInstruction(View view){
        setContentView(R.layout.activity_game_instructions4);
    }

    public void backToMenu(View view){
        Intent intent = new Intent(this, GameMenuActivity.class);
        intent.putExtra("hostUrl", host_url);
        intent.putExtra("hostPort", host_port);
        startActivity(intent);
    }

    //diable back button
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
