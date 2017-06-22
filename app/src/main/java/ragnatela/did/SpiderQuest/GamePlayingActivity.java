package ragnatela.did.SpiderQuest;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

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

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        ragnatelaHandler.resumeMusic();
    }

    @Override
    protected void onPause() {
        Context context = getApplicationContext();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                ragnatelaHandler.pauseMusic();
                //Toast.makeText(context, "YOU LEFT YOUR APP", Toast.LENGTH_SHORT).show();
            }
            else {
                //Toast.makeText(context, "YOU SWITCHED ACTIVITIES WITHIN YOUR APP", Toast.LENGTH_SHORT).show();
            }
        }
        super.onPause();
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
