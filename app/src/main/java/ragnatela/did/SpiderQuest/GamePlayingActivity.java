package ragnatela.did.SpiderQuest;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class GamePlayingActivity extends AppCompatActivity{

    private String host_url = "192.168.1.32";
    private int host_port = 8080;

    private int gameSpeed;

    private RagnatelaHandler ragnatelaHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_playing);

        //prelevo host url e port
        host_url = getIntent().getExtras().getString("hostUrl");
        host_port = getIntent().getExtras().getInt("hostPort");

        gameSpeed = getIntent().getExtras().getInt("GameSpeed");

        Context context = GamePlayingActivity.this;

        ragnatelaHandler = new RagnatelaHandler(host_url, host_port, gameSpeed, context);

        Thread t= new Thread(new Runnable() {
            public void run() {
                ragnatelaHandler.startGame();
            }
        });
        t.start();
    }

    protected void onResume(){
        super.onResume();
        if(!GameMenuActivity.mp.isPlaying()){
            GameMenuActivity.mp.start();
            GameMenuActivity.mp.setLooping(true);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        GameMenuActivity.mp.pause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ragnatelaHandler.destroy();
    }

    //diable back button
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
