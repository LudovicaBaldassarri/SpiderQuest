package ragnatela.did.SpiderQuest;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GameMenuActivity extends AppCompatActivity {

    Unbinder unbinder;

    @BindView(R.id.gioca_main)
    Button playButton;

    @BindView(R.id.settings)
    Button settingsButton;

    @BindView(R.id.istruzioni)
    Button instructionsButton;

    PopupMenu popup;

    private static int gameSpeed = 200;

    private String host_url = "192.168.1.32";
    private int host_port = 8080;

    //private GoogleApiClient client;

    private RagnatelaHandler ragnatelaHandler;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        unbinder = ButterKnife.bind(this);

        //creo popup settings
        popup = new PopupMenu(GameMenuActivity.this, settingsButton);
        popup.getMenuInflater().inflate(R.menu.activity_settings, popup.getMenu());

        //prelevo host url e port
        host_url = getIntent().getExtras().getString("hostUrl");
        host_port = getIntent().getExtras().getInt("hostPort");

        Context context = GameMenuActivity.this;
        ragnatelaHandler = new RagnatelaHandler(host_url, host_port, gameSpeed, context);

        Thread t= new Thread(new Runnable() {
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ragnatelaHandler.showLogo();
                    }
                }, 100);
            }
        });
        t.start();

        //inizializzo i Listeners
        initListeners();

        ragnatelaHandler.playMusic(context);
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

    private void initListeners() {

        playButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) { //non posso giocare finchè non setto la difficoltà
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.imposta);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rememberClicked(popup);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        settingsMenu(menuItem);
                        return true;
                    }
                });
                popup.show();

                //posso giocare dopo aver settato la difficoltà
                playButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        initGame();
                    }
                });
            }
        });

        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInstructions();
            }
        });
    }

    private void rememberClicked(PopupMenu popup) {
        boolean i;
        switch(gameSpeed){
            case 100:
                i = popup.getMenu().findItem(R.id.hard).isChecked();
                if(!i)
                    popup.getMenu().findItem(R.id.hard).setChecked(true);
                break;
            case 200:
                i = popup.getMenu().findItem(R.id.normal).isChecked();
                if(!i)
                    popup.getMenu().findItem(R.id.normal).setChecked(true);
                break;
            case 400:
                i = popup.getMenu().findItem(R.id.easy).isChecked();
                if(!i)
                    popup.getMenu().findItem(R.id.easy).setChecked(true);
                break;
            default:
                i = !popup.getMenu().getItem(R.id.normal).isChecked();
                if(i)
                    popup.getMenu().getItem(R.id.normal).setChecked(true);
                break;
        }
    }

    private void settingsMenu(MenuItem menuItem){
        switch (menuItem.getItemId()) {
            case R.id.easy:
                gameSpeed = 400;
                menuItem.setChecked(true);
                break;
            case R.id.normal:
                gameSpeed = 200;
                menuItem.setChecked(true);
                break;
            case R.id.hard:
                gameSpeed = 100;
                menuItem.setChecked(true);
                break;
            default:
                gameSpeed = 200;
                break;
        }
        ragnatelaHandler.setGameSpeed(gameSpeed);
    }

    private void openInstructions() {
        Intent intent = new Intent(this, GameInstructionsActivity.class);
        intent.putExtra("hostUrl", host_url);
        intent.putExtra("hostPort", host_port);
        intent.putExtra("GameSpeed", gameSpeed);
        startActivity(intent);
    }

    private void initGame() {
        Intent intent = new Intent(GameMenuActivity.this, GamePlayingActivity.class);
        intent.putExtra("hostUrl", host_url);
        intent.putExtra("hostPort", host_port);
        intent.putExtra("GameSpeed", gameSpeed);
        startActivity(intent);
    }

    //diable back button
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
