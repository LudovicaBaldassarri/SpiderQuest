package ragnatela.did.SpiderQuest;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;

public class GameInstructionsActivity extends AppCompatActivity {

    String host_url;
    int host_port;
    int gameSpeed;
    RagnatelaHandler ragnatelaHandler;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_instructions1);

        //prelevo host url e port
        host_url = getIntent().getExtras().getString("hostUrl");
        host_port = getIntent().getExtras().getInt("hostPort");
        gameSpeed = getIntent().getExtras().getInt("gameSpeed");

        Context context = GameInstructionsActivity.this;

        ragnatelaHandler = new RagnatelaHandler(host_url, host_port, gameSpeed, context);
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(GameInstructionsActivity.this, GameMenuActivity.class);
                    intent.putExtra("hostUrl", host_url);
                    intent.putExtra("hostPort", host_port);
                    startActivity(intent);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, 200);
    }

    //diable back button
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
