package ragnatela.did.SpiderQuest;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class GameEndActivity extends AppCompatActivity {

    private String host_url;
    private int host_port;
    private int gameSpeed;
    private RagnatelaHandler ragnatelaHandler;

    MediaPlayer mp2;

    boolean win;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // mi prendo l'indirizzo IP inserito all'inizio di game1activity per la connessione e tutte le funzioni annesse
        host_url = getIntent().getExtras().getString("hostUrl");
        host_port = getIntent().getExtras().getInt("hostPort");
        gameSpeed = getIntent().getExtras().getInt("gameSpeed");

        win = getIntent().getExtras().getBoolean("Victory");

        if (win) {
            setContentView(R.layout.activity_game_won);
            mp2 = MediaPlayer.create(getApplicationContext(), R.raw.win_sound);
        } else{
            setContentView(R.layout.activity_game_lost);
            mp2 = MediaPlayer.create(getApplicationContext(), R.raw.fail_sound);
        }

        GameMenuActivity.mp.stop();
        GameMenuActivity.mp = null;
        mp2.start();

        Context context = GameEndActivity.this;

        ragnatelaHandler = new RagnatelaHandler(host_url, host_port, gameSpeed, context);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (win) {
                    ragnatelaHandler.showSpidersWin();
                } else
                    ragnatelaHandler.showSpidersLoose();
            }
        });
        t.start();
    }

    @Override
    protected void onStop(){
        super.onStop();
        mp2.stop();
        mp2.release();
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ragnatelaHandler.destroy();
    }

    public void backtoMenu(View view){
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
