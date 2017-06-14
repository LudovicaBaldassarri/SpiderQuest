package com.example.alessia.appragni;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class Game2Activity extends AppCompatActivity implements View.OnDragListener, View.OnTouchListener{


    private static final String LOGCAT = null;
    private Bitmap ragnatela_map;
    private RelativeLayout ragnatela, ragnatela_drop;
    private String TAG = getClass().getSimpleName();
    private int ragnoR=15, ragnoB=15, ragnoG=15;

    int red, green, blue;// variabili dell'altra activity

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

    int centerLogoX=15;
    int getCenterLogoY=15;

    private Handler handler = new Handler();
    private Runnable runnable;

    Unbinder unbinder;

    private String host_url;
    private int host_port;

    //private TextWatcher myIpTextWatcher;
    private JSONArray pixels_array;
    private JSONArray pixels_array_LED;

    private Handler mNetworkHandler, mMainHandler;

    private NetworkThread mNetworkThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);

        red = (int) getIntent().getExtras().getInt("Red");
        green = (int) getIntent().getExtras().getInt("Green");
        blue = (int) getIntent().getExtras().getInt("Blue");
        host_url = getIntent().getExtras().getString("hostUrl");
        host_port = getIntent().getExtras().getInt("hostPort");


        ragnatela_map = ((BitmapDrawable)getResources().getDrawable(R.drawable.ragnatela_bitmap_04)).getBitmap();
        ragnatela = (RelativeLayout)findViewById(R.id.ragnatelaLayout);
        //ragnatela_drop = (RelativeLayout) findViewById(R.id.dropLayout) ;

        findViewById(R.id.ragnatelaLayout).setOnDragListener(this);
        //findViewById(R.id.ragnoR).setOnTouchListener(this);
        findViewById(R.id.ragnoR).setOnTouchListener(this);
        findViewById(R.id.ragnoG).setOnTouchListener(this);
        findViewById(R.id.ragnoB).setOnTouchListener(this);

        unbinder = ButterKnife.bind(this);

        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(Game2Activity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
            }
        };

        startHandlerThread();
        handleNetworkRequest(NetworkThread.SET_SERVER_DATA, host_url, host_port ,0);
        pixels_array = preparePixelsArray();
        pixels_array_LED = preparePixelsArray();

    }

    public void startHandlerThread(){
        mNetworkThread = new NetworkThread(mMainHandler);
        mNetworkThread.start();
        mNetworkHandler = mNetworkThread.getNetworkHandler();

    }

    private void handleNetworkRequest(int what, Object payload, int arg1, int arg2) {
        Message msg = mNetworkHandler.obtainMessage();
        msg.what = what;
        msg.obj = payload;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.sendToTarget();
    }

    public boolean onDrag(View layoutview, DragEvent dragevent){
        int action = dragevent.getAction();
        View view = (View) dragevent.getLocalState();

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

                int color = ragnatela_map.getPixel((int) (dragevent.getX() / hRatio), (int) (dragevent.getY() / vRatio));

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

                            lp.setMargins((int) dragevent.getX() - 130 / 2, (int) dragevent.getY() - 130 / 2, 0, 0);
                            container.addView(view, lp);
                            container.getId();
                        }

                    }
                }
                view.setVisibility(View.VISIBLE);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                Log.d(LOGCAT, "Drag ended");
                if (dropEventNotHandled(dragevent)) {
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
        if(ragnoR==red && ragnoB==blue && ragnoG==green){
            Intent intent = new Intent(Game2Activity.this, VictoryActivity.class);
            intent.putExtra("hostUrl", host_url);
            intent.putExtra("hostPort", host_port);
            startActivity(intent);
        } else if (ragnoR!=15 && ragnoB!=15 && ragnoG!=15){
            Intent intent = new Intent(this, LooseActivity.class);
            intent.putExtra("hostUrl", host_url);
            intent.putExtra("hostPort", host_port);
            startActivity(intent);
        }else{
            Context context = getApplicationContext();
            CharSequence text = getString(R.string.finisci);
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    @Override
    protected void onStart(){
        super.onStart();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{


                    for (int i = 0; i < pixels_array.length(); i++) {
                        ((JSONObject) pixels_array.get(i)).put("r", 0);
                        ((JSONObject) pixels_array.get(i)).put("g", 0);
                        ((JSONObject) pixels_array.get(i)).put("b", 0);
                    }
                    showArrow();

                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0, 0);
                    pixels_array = preparePixelsArray();
                } catch(JSONException e){

                }
            }
        });
        t.start();
    }

    @Override
    protected void onStop(){
        super.onStop();
        spegniSchermo();
        try{
            pixels_array_LED = Game1Activity.prepareLedPixelsArray();
            pixels_array = Game1Activity.prepareDisplayPixelsArray();
            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0 ,0);
            handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array_LED, 0 ,0);
        }catch (Exception e){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (mNetworkThread != null && mNetworkHandler != null) {
            mNetworkHandler.removeMessages(mNetworkThread.SET_PIXELS);
            mNetworkHandler.removeMessages(mNetworkThread.SET_DISPLAY_PIXELS);
            mNetworkHandler.removeMessages(mNetworkThread.SET_SERVER_DATA);
            mNetworkThread.quit();
            try {
                mNetworkThread.join(100);
            } catch (InterruptedException ie) {
                throw new RuntimeException(ie);
            } finally {
                mNetworkThread = null;
                mNetworkHandler = null;
            }
        }
    }

    int computeIndex(int x, int y){
        return y*32+x;
    }

    void spegniSchermo() {
        try{
            for (int i = 0; i < pixels_array.length(); i++) {
                ((JSONObject) pixels_array.get(i)).put("r", 0);
                ((JSONObject) pixels_array.get(i)).put("g", 0);
                ((JSONObject) pixels_array.get(i)).put("b", 0);
            }
        } catch(JSONException e){

        }

    }

    void turnOnLed(int x, int y) throws JSONException{
        if(x<0 || y<0 || x>31 || y>31)return;
        int current=computeIndex(x,y);
        ((JSONObject) pixels_array.get(current)).put("r", 255);
        ((JSONObject) pixels_array.get(current)).put("g", 255);
        ((JSONObject) pixels_array.get(current)).put("b", 255);//utilizzo funzione computeIndex per calcolare indice di volta in volta
    }

    void showArrow(){
        try{
            drawArrow(centerLogoX, getCenterLogoY);
        }catch(JSONException e){

        }
    }


    JSONArray preparePixelsArray() {
        JSONArray pixels_array = new JSONArray();
        JSONObject tmp;
        try {
            for (int i = 0; i < 1072; i++) {
                tmp = new JSONObject();
                tmp.put("a", 0);

                pixels_array.put(tmp);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return pixels_array;
    }

    void drawArrow(int x, int y) throws JSONException{
        turnOnLed(x-9,y+3);
        turnOnLed(x-9,y+4);
        turnOnLed(x-9,y+5);
        turnOnLed(x-9,y+6);
        turnOnLed(x-9,y+7);
        turnOnLed(x-9,y+8);
        turnOnLed(x-9,y+9);
        turnOnLed(x-9,y+10);

        turnOnLed(x-8,y+2);
        turnOnLed(x-8,y+3);
        turnOnLed(x-8,y+10);
        turnOnLed(x-8,y+11);


        turnOnLed(x-7,y+1);
        turnOnLed(x-7,y+2);
        turnOnLed(x-7,y+3);
        turnOnLed(x-7,y+4);
        turnOnLed(x-7,y+5);
        turnOnLed(x-7,y+6);
        turnOnLed(x-7,y+7);
        turnOnLed(x-7,y+8);
        turnOnLed(x-7,y+9);
        turnOnLed(x-7,y+10);
        turnOnLed(x-7,y+11);
        turnOnLed(x-7,y+12);

        turnOnLed(x-6,y+1);
        turnOnLed(x-6,y+12);

        turnOnLed(x-5,y+1);
        turnOnLed(x-5,y+12);

        turnOnLed(x-4,y-5);
        turnOnLed(x-4,y+1);
        turnOnLed(x-4,y+12);

        turnOnLed(x-3,y-4);
        turnOnLed(x-3,y-5);
        turnOnLed(x-3,y+1);
        turnOnLed(x-3,y+12);

        turnOnLed(x-2,y-3);
        turnOnLed(x-2,y-4);
        turnOnLed(x-2,y-5);
        turnOnLed(x-2,y+1);
        turnOnLed(x-2,y+12);

        turnOnLed(x-1,y-2);
        turnOnLed(x-1,y-3);
        turnOnLed(x-1,y-4);
        turnOnLed(x-1,y-5);
        turnOnLed(x-1,y-6);
        turnOnLed(x-1,y-7);
        turnOnLed(x-1,y-8);
        turnOnLed(x-1,y-9);
        turnOnLed(x-1,y-10);
        turnOnLed(x-1,y-11);
        turnOnLed(x-1,y+1);
        turnOnLed(x-1,y+12);

        turnOnLed(x,y-1);
        turnOnLed(x,y-2);
        turnOnLed(x,y-3);
        turnOnLed(x,y-4);
        turnOnLed(x,y-5);
        turnOnLed(x,y-6);
        turnOnLed(x,y-7);
        turnOnLed(x,y-8);
        turnOnLed(x,y-9);
        turnOnLed(x,y-10);
        turnOnLed(x,y-11);
        turnOnLed(x,y+1);
        turnOnLed(x,y+12);

        turnOnLed(x+1,y-1);
        turnOnLed(x+1,y-2);
        turnOnLed(x+1,y-3);
        turnOnLed(x+1,y-4);
        turnOnLed(x+1,y-5);
        turnOnLed(x+1,y-6);
        turnOnLed(x+1,y-7);
        turnOnLed(x+1,y-8);
        turnOnLed(x+1,y-9);
        turnOnLed(x+1,y-10);
        turnOnLed(x+1,y-11);
        turnOnLed(x+1,y+1);
        turnOnLed(x+1,y+12);

        turnOnLed(x+2,y-2);
        turnOnLed(x+2,y-3);
        turnOnLed(x+2,y-4);
        turnOnLed(x+2,y-5);
        turnOnLed(x+2,y-6);
        turnOnLed(x+2,y-7);
        turnOnLed(x+2,y-8);
        turnOnLed(x+2,y-9);
        turnOnLed(x+2,y-10);
        turnOnLed(x+2,y-11);
        turnOnLed(x+2,y+1);
        turnOnLed(x+2,y+12);

        turnOnLed(x+3,y-3);
        turnOnLed(x+3,y-4);
        turnOnLed(x+3,y-5);
        turnOnLed(x+3,y+1);
        turnOnLed(x+3,y+12);

        turnOnLed(x+4,y-4);
        turnOnLed(x+4,y-5);
        turnOnLed(x+4,y+1);
        turnOnLed(x+4,y+12);

        turnOnLed(x+5,y-5);
        turnOnLed(x+5,y+1);
        turnOnLed(x+5,y+12);

        turnOnLed(x+6,y+1);
        turnOnLed(x+6,y+12);

        turnOnLed(x+7,y+1);
        turnOnLed(x+7,y+2);
        turnOnLed(x+7,y+3);
        turnOnLed(x+7,y+4);
        turnOnLed(x+7,y+5);
        turnOnLed(x+7,y+6);
        turnOnLed(x+7,y+7);
        turnOnLed(x+7,y+8);
        turnOnLed(x+7,y+9);
        turnOnLed(x+7,y+10);
        turnOnLed(x+7,y+11);
        turnOnLed(x+7,y+12);

        turnOnLed(x+8,y+1);
        turnOnLed(x+8,y+2);
        turnOnLed(x+8,y+3);
        turnOnLed(x+8,y+4);
        turnOnLed(x+8,y+5);
        turnOnLed(x+8,y+8);
        turnOnLed(x+8,y+9);
        turnOnLed(x+8,y+10);
        turnOnLed(x+8,y+11);
        turnOnLed(x+8,y+12);

        turnOnLed(x+9,y+2);
        turnOnLed(x+9,y+3);
        turnOnLed(x+9,y+4);
        turnOnLed(x+9,y+5);
        turnOnLed(x+9,y+8);
        turnOnLed(x+9,y+9);
        turnOnLed(x+9,y+10);
        turnOnLed(x+9,y+11);

        turnOnLed(x+10,y+3);
        turnOnLed(x+10,y+4);
        turnOnLed(x+10,y+5);
        turnOnLed(x+10,y+6);
        turnOnLed(x+10,y+7);
        turnOnLed(x+10,y+8);
        turnOnLed(x+10,y+9);
        turnOnLed(x+10,y+10);
    }
}
