package com.example.alessia.appragni;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ThreadFactory;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
/**
 * Created by ludo_monkey on 03/05/17.
 */

public class VictoryActivity extends AppCompatActivity {

    int beginXRA=9;
    int beginYRA=10;
    int beginXGA=15;
    int beginYGA=20;
    int beginXBA=21;
    int beginYBA=10;
    int xRA=0;
    int yRA=0;
    int xGA=0;
    int yGA=0;
    int xBA=0;
    int yBA=0;
    long waitTime;

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

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victory);

        // mi prendo l'indirizzo IP inserito all'inizio di game1activity per la connessione e tutte le funzioni annesse
        host_url = getIntent().getExtras().getString("hostUrl");
        host_port = getIntent().getExtras().getInt("hostPort");


        unbinder = ButterKnife.bind(this);

        /*myIpTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (checkCorrectIp()) {

                    Message msg = mNetworkHandler.obtainMessage();
                    msg.what = NetworkThread.SET_SERVER_DATA;
                    msg.obj = host_url;
                    msg.arg1 = host_port;
                    msg.sendToTarget();

                    handleNetworkRequest(NetworkThread.SET_SERVER_DATA, host_url, host_port ,0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        };*/



        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(VictoryActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
            }
        };

        startHandlerThread();
        handleNetworkRequest(NetworkThread.SET_SERVER_DATA, host_url, host_port ,0);
        pixels_array = preparePixelsArray();
        pixels_array_LED = preparePixelsArray();
    }



    public void backtoMenu (View view) throws JSONException{
        //per fermare l'handler altrimenti i ragnetti continuerebbero a ballare all'infinito
        handler.removeCallbacks(runnable);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openInizio(View view) throws JSONException{
        //per fermare l'handler altrimenti i ragnetti continuerebbero a ballare all'infinito
        handler.removeCallbacks(runnable);

        Intent intent=new Intent(this, Game1Activity.class);
        startActivity(intent);
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

    /*private boolean checkCorrectIp() {


        if (validIP(host_url) && host_port >= 0 & host_port <= 65535) {
            return true;
        } else
            return false;
    }

    //from http://stackoverflow.com/questions/4581877/validating-ipv4-string-in-java
    public static boolean validIP(String ip) {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            if (ip.endsWith(".")) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }

    }*/

    @Override
    protected void onStart(){
        super.onStart();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                showSpiders();
            }
        });
        t.start();

        MainActivity.mp.stop();
        MainActivity.mpCount = 0;
        mp = MediaPlayer.create(getApplicationContext(), R.raw.fail_sound);
        mp.start();
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

        mp.stop();
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

    void turnOnLed(String spiderColor, int x, int y) throws JSONException{
        if(x<0 || y<0 || x>31 || y>31)return;
        int current=computeIndex(x,y);
        ((JSONObject) pixels_array.get(current)).put(spiderColor, 255); //utilizzo funzione computeIndex per calcolare indice di volta in volta
    }

    void drawSpider(String spiderColor, int x, int y) throws JSONException{
        turnOnLed(spiderColor, x,y);
        // turnOnLed(spiderColor, x+1,y+1);
        turnOnLed(spiderColor, x+2,y+2);
        turnOnLed(spiderColor, x+3,y+3);
        turnOnLed(spiderColor, x+1,y-1);
        turnOnLed(spiderColor, x+2,y-2);
        turnOnLed(spiderColor, x+3,y-3);
        turnOnLed(spiderColor, x-1,y-1);
        turnOnLed(spiderColor, x-2,y-2);
        turnOnLed(spiderColor, x-3,y-3);
        // turnOnLed(spiderColor, x-1,y+1);
        turnOnLed(spiderColor, x-2,y+2);
        turnOnLed(spiderColor, x-3,y+3);

        turnOnLed(spiderColor, x-3,y-1);
        turnOnLed(spiderColor, x-2,y-1);
        turnOnLed(spiderColor, x,y-1);
        turnOnLed(spiderColor, x+2,y-1);
        turnOnLed(spiderColor, x+3,y-1);
        turnOnLed(spiderColor, x,y-2);
        turnOnLed(spiderColor, x-2, y);
        turnOnLed(spiderColor, x-1,y);
        turnOnLed(spiderColor, x+1,y);
        turnOnLed(spiderColor, x+2,y);
        turnOnLed(spiderColor, x-3,y+1);
        turnOnLed(spiderColor, x-2,y+1);
        turnOnLed(spiderColor, x,y+1);
        turnOnLed(spiderColor, x+2,y+1);
        turnOnLed(spiderColor, x+3,y+1);
        turnOnLed(spiderColor, x-1,y+2);
        turnOnLed(spiderColor, x,y+2);
        turnOnLed(spiderColor, x+1,y+2);
        turnOnLed(spiderColor, x-1,y+3);
        turnOnLed(spiderColor, x+1,y+3);

    }

    void drawSpiderUp(String spiderColor, int x, int y) throws JSONException{
        turnOnLed(spiderColor, x,y);
        turnOnLed(spiderColor, x+1,y+1);
        turnOnLed(spiderColor, x+2,y+2);
        turnOnLed(spiderColor, x+3,y+3);
        turnOnLed(spiderColor, x+1,y-1);
        turnOnLed(spiderColor, x+2,y-2);
        turnOnLed(spiderColor, x+3,y-3);
        turnOnLed(spiderColor, x-1,y-1);
        turnOnLed(spiderColor, x-2,y-2);
        turnOnLed(spiderColor, x-3,y-3);
        turnOnLed(spiderColor, x-1,y+1);
        turnOnLed(spiderColor, x-2,y+2);
        turnOnLed(spiderColor, x-3,y+3);

        turnOnLed(spiderColor, x-1,y-2);
        turnOnLed(spiderColor, x,y-2);
        turnOnLed(spiderColor, x+1,y-2);
        turnOnLed(spiderColor, x-3,y-1);
        turnOnLed(spiderColor, x-2,y-1);
        turnOnLed(spiderColor, x,y-1);
        turnOnLed(spiderColor, x+2,y-1);
        turnOnLed(spiderColor, x+3,y-1);
        turnOnLed(spiderColor, x-2, y);
        turnOnLed(spiderColor, x,y);
        turnOnLed(spiderColor, x+1,y);
        turnOnLed(spiderColor, x-3,y+1);
        turnOnLed(spiderColor, x-2,y+1);
        turnOnLed(spiderColor, x,y+1);
        turnOnLed(spiderColor, x+2,y+1);
        turnOnLed(spiderColor, x+3,y+1);
        turnOnLed(spiderColor, x,y+2);

    }

    void showSpiders(){


                //long thisTime;
                int lastR=-1;
                int lastG=-1;
                int lastB=-1;
                int currentR;
                int currentG;
                int currentB;
        try{

            //waitTime=System.currentTimeMillis();


                //tempo attuale - tempo di partenza minore della durata totale in millisecondi
                //Log.d("PCT", "pct= "+ pct);
                xRA=Math.round(beginXRA);
                yRA=Math.round(beginYRA);
                xGA=Math.round(beginXGA);
                yGA=Math.round(beginYGA);
                xBA=Math.round(beginXBA);
                yBA=Math.round(beginYBA);
                currentR=computeIndex(xRA,yRA);
                currentG=computeIndex(xGA,yGA);
                currentB=computeIndex(xBA,yBA);

                if(lastR!=currentR){
                    spegniSchermo();
                    drawSpider("r", xRA, yRA);
                    drawSpider("g", xGA, yGA);
                    drawSpider("b", xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0 ,0);
                    lastR=currentR;
                }

                if(lastG!=currentG){
                    spegniSchermo();
                    drawSpider("r", xRA, yRA);
                    drawSpider("g", xGA, yGA);
                    drawSpider("b", xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0 ,0);
                    lastG=currentG;
                }

                if(lastB!=currentB){
                    spegniSchermo();
                    drawSpider("r", xRA, yRA);
                    drawSpider("g", xGA, yGA);
                    drawSpider("b", xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0 ,0);
                    lastB=currentB;
                }

            // Lancio tramite handler le due funzioni in modo che luppino ogni 200 millisecondi
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    showSpidersUp();
                }
            }, 200);

        }catch(JSONException e){

        }
    }

    void showSpidersUp(){
        //long thisTime;
        int lastR=-1;
        int lastG=-1;
        int lastB=-1;
        int currentR;
        int currentG;
        int currentB;

        try{

            //waitTime=System.currentTimeMillis();


                //tempo attuale - tempo di partenza minore della durata totale in millisecondi
                //Log.d("PCT", "pct= "+ pct);
                xRA=Math.round(beginXRA);
                yRA=Math.round(beginYRA);
                xGA=Math.round(beginXGA);
                yGA=Math.round(beginYGA);
                xBA=Math.round(beginXBA);
                yBA=Math.round(beginYBA);
                currentR=computeIndex(xRA,yRA);
                currentG=computeIndex(xGA,yGA);
                currentB=computeIndex(xBA,yBA);

                if(lastR!=currentR){
                    spegniSchermo();
                    drawSpiderUp("r", xRA, yRA-2);
                    drawSpiderUp("g", xGA, yGA-2);
                    drawSpiderUp("b", xBA, yBA-2);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0 ,0);
                    lastR=currentR;
                }

                if(lastG!=currentG){
                    spegniSchermo();
                    drawSpiderUp("r", xRA, yRA-2);
                    drawSpiderUp("g", xGA, yGA-2);
                    drawSpiderUp("b", xBA, yBA-2);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0 ,0);
                    lastG=currentG;
                }

                if(lastB!=currentB){
                    spegniSchermo();
                    drawSpiderUp("r", xRA, yRA-2);
                    drawSpiderUp("g", xGA, yGA-2);
                    drawSpiderUp("b", xBA, yBA-2);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0 ,0);
                    lastB=currentB;
                }

            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    showSpiders();
                }
            }, 200);


        }catch(JSONException e){

        }


    }

    private void showLeds() {

        try {

            for (int i = 0; i < pixels_array_LED.length(); i++) {
                ((JSONObject) pixels_array_LED.get(i)).put("r", (int) (Math.random() * 255.0f));
                ((JSONObject) pixels_array_LED.get(i)).put("g", (int) (Math.random() * 255.0f));
                ((JSONObject) pixels_array_LED.get(i)).put("b", (int) (Math.random() * 255.0f));
            }
            handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array_LED, 0 ,0);

            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    showLeds();
                }
            }, 200);

        } catch (Exception e) {
            e.printStackTrace();
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




}
