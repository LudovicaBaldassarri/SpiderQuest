package com.example.alessia.appragni;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.text.Editable;
import android.text.TextWatcher;
import android.os.Handler;
import android.os.Message;
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
 * Created by Michele on 03/05/2017.
 */

public class LooseActivity extends AppCompatActivity {

    int beginXRA=9;
    int beginYRA=-7;
    int beginXGA=15;
    int beginYGA=-7;
    int beginXBA=21;
    int beginYBA=-7;
    int xRA=0;
    int yRA=0;
    int xGA=0;
    int yGA=0;
    int xBA=0;
    int yBA=0;
    long waitTime;
    long startTime;
    int endXRA=21;
    int endYRA=29;
    int endXGA=9;
    int endYGA=29;
    int endXBA=15;
    int endYBA=29;
    int distXRA;
    int distYRA;
    int distXGA;
    int distYGA;
    int distXBA;
    int distYBA;
    float movementDuration=1.5f;
    int speed = 100;


    Unbinder unbinder;

    private String host_url;
    private int host_port;

    private TextWatcher myIpTextWatcher;
    private JSONArray pixels_array;

    private Handler mNetworkHandler, mMainHandler;

    private NetworkThread mNetworkThread = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loose);

        // mi prendo l'indirizzo IP inserito all'inizio di game1activity per la connessione e tutte le funzioni annesse
        host_url = getIntent().getExtras().getString("hostUrl");
        host_port = getIntent().getExtras().getInt("hostPort");


        unbinder = ButterKnife.bind(this);

        myIpTextWatcher = new TextWatcher() {
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

        };



        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(LooseActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
            }
        };

        startHandlerThread();
        handleNetworkRequest(NetworkThread.SET_SERVER_DATA, host_url, host_port ,0);
        pixels_array = preparePixelsArray();
    }

    public void backtoMenu (View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openInizio(View view){
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

    private boolean checkCorrectIp() {


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

    }

    @Override
    protected void onStart(){
        super.onStart();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                showSpiders();
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                showLeds();
            }
        });
        t1.start();
        t2.start();
    }

    @Override
    protected void onStop(){
        super.onStop();
        spegniTutto();
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

    void spegniTutto() {
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
        try{

            waitTime=System.currentTimeMillis();
            long thisTime;
            int lastR=-1;
            int lastG=-1;
            int lastB=-1;
            int currentR;
            int currentG;
            int currentB;


            do{
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
                    spegniTutto();
                    drawSpider("r", xRA, yRA);
                    drawSpider("g", xGA, yGA);
                    drawSpider("b", xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0 ,0);
                    lastR=currentR;
                }

                if(lastG!=currentG){
                    spegniTutto();
                    drawSpider("r", xRA, yRA);
                    drawSpider("g", xGA, yGA);
                    drawSpider("b", xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0 ,0);
                    lastG=currentG;
                }

                if(lastB!=currentB){
                    spegniTutto();
                    drawSpider("r", xRA, yRA);
                    drawSpider("g", xGA, yGA);
                    drawSpider("b", xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0 ,0);
                    lastB=currentB;
                }

                thisTime=(System.currentTimeMillis()-waitTime);

            } while(thisTime<200);

            moveSpiders();


        }catch(JSONException e){

        }

    }




    void moveSpiders(){
        try{

            startTime=System.currentTimeMillis();
            float pct;
            int lastR=-1;
            int lastG=-1;
            int lastB=-1;
            int currentR;
            int currentG;
            int currentB;

            distXRA = endXRA-beginXRA;
            distYRA = endYRA-beginYRA;
            distXGA = endXGA-beginXGA;
            distYGA = endYGA-beginYGA;
            distXBA = endXBA-beginXBA;
            distYBA = endYBA-beginYBA;

            do{
                pct=(System.currentTimeMillis()-startTime)/(movementDuration*speed*10);  //tempo attuale - tempo di partenza minore della durata totale in millisecondi
                //Log.d("PCT", "pct= "+ pct);
                xRA=Math.round(beginXRA+pct*distXRA);
                yRA=Math.round(beginYRA+pct*distYRA);
                xGA=Math.round(beginXGA+pct*distXGA);
                yGA=Math.round(beginYGA+pct*distYGA);
                xBA=Math.round(beginXBA+pct*distXBA);
                yBA=Math.round(beginYBA+pct*distYBA);
                currentR=computeIndex(xRA,yRA);
                currentG=computeIndex(xGA,yGA);
                currentB=computeIndex(xBA,yBA);

                if(lastR!=currentR){
                    spegniTutto();
                    drawSpider("r", xRA, yRA);
                    drawSpider("g", xGA, yGA);
                    drawSpider("b", xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0 ,0);
                    lastR=currentR;
                }

                if(lastG!=currentG){
                    spegniTutto();
                    drawSpider("r", xRA, yRA);
                    drawSpider("g", xGA, yGA);
                    drawSpider("b", xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0 ,0);
                    lastG=currentG;
                }

                if(lastB!=currentB){
                    spegniTutto();
                    drawSpider("r", xRA, yRA);
                    drawSpider("g", xGA, yGA);
                    drawSpider("b", xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array, 0 ,0);
                    lastB=currentB;
                }


            } while(pct<1.0f);

            showSpiders();



        }catch(JSONException e){

        }
    }


    private void showLeds() {
        try {
            JSONArray pixels_array = preparePixelsArray();

            while(true){
                for (int i = 0; i < pixels_array.length(); i++) {
                    ((JSONObject) pixels_array.get(i)).put("r", (int) (Math.random() * 255.0f));
                    ((JSONObject) pixels_array.get(i)).put("g", (int) (Math.random() * 255.0f));
                    ((JSONObject) pixels_array.get(i)).put("b", (int) (Math.random() * 255.0f));
                }
                handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array, 0 ,0);
                Thread.sleep(500);
            }
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
