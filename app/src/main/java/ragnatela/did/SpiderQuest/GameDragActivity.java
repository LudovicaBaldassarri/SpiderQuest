package ragnatela.did.SpiderQuest;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class GameDragActivity extends AppCompatActivity implements View.OnDragListener, View.OnTouchListener {

    private String host_url = "192.168.1.32";
    private int host_port = 8080;

    private int gameSpeed;

    private int redPosition, greenPosition, bluePosition;

    private static final String LOGCAT = null;
    private Bitmap ragnatela_map;
    private RelativeLayout ragnatela, ragnatela_drop;
    private String TAG = getClass().getSimpleName();
    private int ragnoR=15, ragnoB=15, ragnoG=15;

    int g1 = Color.argb(255, 255, 240, 75);
    int g2 = Color.argb(255, 255, 190, 75);
    int g3 = Color.argb(255, 255, 161, 75);
    int g4 = Color.argb(255, 255, 125, 75);
    int g5 = Color.argb(255, 255, 103, 75);
    int b1 = Color.argb(255, 57, 197, 212);
    int b2 = Color.argb(255, 57, 179, 212);
    int b3 = Color.argb(255, 57, 157, 212);
    int b4 = Color.argb(255, 57, 134, 212);
    int b5 = Color.argb(255, 57, 103, 212);
    int v1 = Color.argb(255, 174, 248, 85);
    int v2 = Color.argb(255, 174, 217, 85);
    int v3 = Color.argb(255, 174, 195, 85);
    int v4 = Color.argb(255, 174, 170, 85);
    int v5 = Color.argb(255, 174, 152, 85);
    int origin = 0;

    int [] nodi = { v1, b1, g1, v2, b2, g2, v3, b3, g3, v4, b4, g4, v5, b5, g5, origin};

    private RagnatelaHandler ragnatelaHandler;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_drag);

        //prelevo host url e port
        host_url = getIntent().getExtras().getString("hostUrl");
        host_port = getIntent().getExtras().getInt("hostPort");

        gameSpeed = getIntent().getExtras().getInt("gameSpeed");

        Context context = GameDragActivity.this;

        ragnatelaHandler = new RagnatelaHandler(host_url, host_port, gameSpeed, context);

        redPosition =ragnatelaHandler.getRstopped();
        greenPosition = ragnatelaHandler.getGstopped();
        bluePosition = ragnatelaHandler.getBstopped();

        ragnatela_map = ((BitmapDrawable)getResources().getDrawable(R.drawable.ragnatela_bitmap_04)).getBitmap();
        ragnatela = (RelativeLayout)findViewById(R.id.ragnatelaLayout);

        findViewById(R.id.ragnatelaLayout).setOnDragListener(this);
        findViewById(R.id.ragnoR).setOnTouchListener(this);
        findViewById(R.id.ragnoG).setOnTouchListener(this);
        findViewById(R.id.ragnoB).setOnTouchListener(this);

        Thread t= new Thread(new Runnable() {
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ragnatelaHandler.showArrow();
                    }
                }, 200);
            }
        });
        t.start();

    }

//    protected void onResume(){
//        super.onResume();
//        ragnatelaHandler.resumeMusic();
//    }
//
//    @Override
//    protected void onPause(){
//        super.onPause();
//        ragnatelaHandler.pauseMusic();
//    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ragnatelaHandler.destroy();
    }

    @Override
    public boolean onDrag(View layoutview, DragEvent dragEvent) {
        int action = dragEvent.getAction();
        View view = (View) dragEvent.getLocalState();

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                //ragnatela_drop.setVisibility(View.VISIBLE);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                //ragnatela_drop.setVisibility(View.INVISIBLE);
                break;
            case DragEvent.ACTION_DROP:
                //ragnatela_drop.setVisibility(View.INVISIBLE);

                float vRatio = (float) ragnatela.getHeight() / (float) ragnatela_map.getHeight();
                float hRatio = (float) ragnatela.getWidth() / (float) ragnatela_map.getWidth();

                int color = ragnatela_map.getPixel((int) (dragEvent.getX() / hRatio), (int) (dragEvent.getY() / vRatio));

                for (int i = 0; i < nodi.length; i++) {
                    if (color == nodi[i]) {
                        if(color != nodi[ragnoG]&& color != nodi[ragnoR] && color != nodi[ragnoB]) {

                            String tagRagno = (String) view.getTag();


                            if (view.getTag().equals("ragnoR")) {
                                ragnoR = i;
                            } else if (view.getTag().equals("ragnoB")) {
                                ragnoB = i;
                            } else if (view.getTag().equals("ragnoG")) {
                                ragnoG = i;
                            }

                            Log.d(TAG, "Posizione di " + tagRagno + " si trova nel nodo numero " + i);
                            Log.d(TAG, "ragnoB= " + ragnoB + " ragnoR= " + ragnoR + " ragnoG= " + ragnoG);

                            ViewGroup owner = (ViewGroup) view.getParent();
                            owner.removeView(view);
                            RelativeLayout container = (RelativeLayout) layoutview;
                            RelativeLayout.LayoutParams lp = new RelativeLayout.
                                    LayoutParams(130, 130);

                            lp.setMargins((int) dragEvent.getX() - 130 / 2, (int) dragEvent.getY() - 130 / 2, 0, 0);
                            container.addView(view, lp);
                            container.getId();
                        }

                    }
                }
                view.setVisibility(View.VISIBLE);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                Log.d(LOGCAT, "Drag ended");
                if (dropEventNotHandled(dragEvent)) {
                    view.setVisibility(View.VISIBLE);
                }

                break;
            default:
                break;
        }
        return true;
    }

    private boolean dropEventNotHandled(DragEvent dragEvent) {
        return !dragEvent.getResult();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

            ClipData.Item item = new ClipData.Item((CharSequence)view.getTag());
            String[] clipDescription = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData((CharSequence)view.getTag(), clipDescription, item);
            view.startDrag(dragData, shadowBuilder, view, 0);

            view.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }

    public void onRestoreInstantState(Bundle savedInstateState){
        super.onRestoreInstanceState(savedInstateState);
    }

    public void onClick(View view) {
        if(ragnoR==redPosition && ragnoB==bluePosition && ragnoG==greenPosition){
            Intent intent = new Intent(GameDragActivity.this, GameEndActivity.class);
            intent.putExtra("hostUrl", host_url);
            intent.putExtra("hostPort", host_port);
            intent.putExtra("GameSpeed", gameSpeed);
            intent.putExtra("Victory", true);
            startActivity(intent);
        } else if (ragnoR!=15 && ragnoB!=15 && ragnoG!=15){
            Intent intent = new Intent(this, GameEndActivity.class);
            intent.putExtra("hostUrl", host_url);
            intent.putExtra("hostPort", host_port);
            intent.putExtra("GameSpeed", gameSpeed);
            intent.putExtra("Victory", false);
            startActivity(intent);
        }else{
            Context context = getApplicationContext();
            CharSequence text = getString(R.string.finisci);
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    //diable back button
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
