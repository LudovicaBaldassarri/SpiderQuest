package com.example.alessia.appragni;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Locale;

public class Inizio extends AppCompatActivity {

    int beginXRA=9;
    int beginYRA=15;
    int beginXGA=15;
    int beginYGA=20;
    int beginXBA=21;
    int beginYBA=15;
    int endXRA;
    int endYRA;
    int endXGA;
    int endYGA;
    int endXBA;
    int endYBA;
    int distXRA;
    int distYRA;
    int distXGA;
    int distYGA;
    int distXBA;
    int distYBA;
    int xRA=0;
    int yRA=0;
    int xGA=0;
    int yGA=0;
    int xBA=0;
    int yBA=0;
    long startTime;
    long waitTime;
    float movementDuration=2.5f; //time in seconds
    int[] exit={1,2,3,4,5};
    int[] dest = new int[3];
    int rdestp;
    int rdest;
    int gdestp;
    int gdest;
    int bdestp;
    int bdest;
    int i;
    int rd;

    Unbinder unbinder;

    private String host_url = "192.168.1.32";
    private int host_port = 8080;



    @BindViews({R.id.first_byte_ip, R.id.second_byte_ip, R.id.third_byte_ip, R.id.fourth_byte_ip})
    List<EditText> ip_address_bytes;

    @BindView(R.id.host_port)
    EditText hostPort;

    @BindView(R.id.play)
    Button destinationCalculate;


    private TextWatcher myIpTextWatcher;
    private JSONArray pixels_array;

    private Handler mNetworkHandler, mMainHandler;

    private NetworkThread mNetworkThread = null;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inizio);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        unbinder = ButterKnife.bind(this);

        destinationCalculate.setEnabled(false);
        myIpTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (checkCorrectIp()) {

                    destinationCalculate.setEnabled(true);
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

        for (EditText ip_byte : ip_address_bytes) {
            ip_byte.addTextChangedListener(myIpTextWatcher);
        }

        hostPort.addTextChangedListener(myIpTextWatcher);

        pixels_array = preparePixelsArray();

        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(Inizio.this, (String) msg.obj, Toast.LENGTH_LONG).show();
            }
        };

        startHandlerThread();
    }

    public void startHandlerThread() {
        mNetworkThread = new NetworkThread(mMainHandler);
        mNetworkThread.start();
        mNetworkHandler = mNetworkThread.getNetworkHandler();
    }

    private boolean checkCorrectIp() {
        StringBuilder sb = new StringBuilder();
        int port;

        if (hostPort.getText().length() == 0)
            return false;

        for (EditText editText : ip_address_bytes) {
            sb.append(editText.getText().toString());
            sb.append(".");
        }
        //cancello l'ultimo "."
        sb.deleteCharAt(sb.length() - 1);

        port = Integer.parseInt(hostPort.getText().toString());
        if (validIP(sb.toString()) && port >= 0 & port <= 65535) {
            host_url = sb.toString();
            host_port = port;
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

    @OnClick(R.id.play)
    void destinationCalculate(){
        try{



            for(i=0;i<3;i++) {
                int index = new Random().nextInt(exit.length);
                rd = exit[index];

                if (i == 0) {
                    dest[i] = rd;
                } else if (i>0) {
                    for (int j = 0; j < 1; j++) {
                        if ((rd == dest[0])||(rd==dest[1])) {
                            index = new Random().nextInt(exit.length);
                            rd = exit[index];
                            j--;
                        }else if(rd==0){
                            index = new Random().nextInt(exit.length);
                            rd = exit[index];
                            j--;
                        } else {
                            dest[i] = rd;
                        }
                    }

                }
            }
            Log.d("DEST", "0 " + String.valueOf(dest[0]) + " 1 " + String.valueOf(dest[1]) + " 2 " + String.valueOf(dest[2]));
            rdestp = dest[0];
            gdestp = dest[1];
            bdestp = dest[2];


            switch (rdestp) {
                case 1:
                    endXRA = 15;
                    endYRA = -4;
                    rdest = 0;
                    break;
                case 2:
                    endXRA = 35;
                    endYRA = 10;
                    rdest = 52;
                    break;
                case 3:
                    endXRA = 35;
                    endYRA = 26;
                    rdest = 186;
                    break;
                case 4:
                    endXRA = -4;
                    endYRA = 26;
                    rdest = 318;
                    break;
                case 5:
                    endXRA = -4;
                    endYRA = 10;
                    rdest = 424;
                    break;
            }
            switch (gdestp) {
                case 1:
                    endXGA = 15;
                    endYGA = -4;
                    gdest = 0;
                    break;
                case 2:
                    endXGA = 35;
                    endYGA = 10;
                    gdest = 52;
                    break;
                case 3:
                    endXGA = 35;
                    endYGA = 26;
                    gdest = 186;
                    break;
                case 4:
                    endXGA = -4;
                    endYGA = 26;
                    gdest = 318;
                    break;
                case 5:
                    endXGA = -4;
                    endYGA = 10;
                    gdest = 424;
                    break;
            }
            switch (bdestp) {
                case 1:
                    endXBA = 15;
                    endYBA = -4;
                    bdest = 0;
                    break;
                case 2:
                    endXBA = 35;
                    endYBA = 10;
                    bdest = 52;
                    break;
                case 3:
                    endXBA = 35;
                    endYBA = 26;
                    bdest = 186;
                    break;
                case 4:
                    endXBA = -4;
                    endYBA = 26;
                    bdest = 318;
                    break;
                case 5:
                    endXBA = -4;
                    endYBA = 10;
                    bdest = 424;
                    break;
            }


            distXRA = endXRA-beginXRA;
            distYRA = endYRA-beginYRA;
            distXGA = endXGA-beginXGA;
            distYGA = endYGA-beginYGA;
            distXBA = endXBA-beginXBA;
            distYBA = endYBA-beginYBA;


            rdest();
            gdest();
            bdest();

            showSpiders();

        }catch(Exception e){

        }

    }

    int computeIndex(int x, int y){
        return y*32+x;
    }

    void spegniTutto() throws JSONException{
        for (int i = 0; i < pixels_array.length(); i++) {
            ((JSONObject) pixels_array.get(i)).put("r", 0);
            ((JSONObject) pixels_array.get(i)).put("g", 0);
            ((JSONObject) pixels_array.get(i)).put("b", 0);
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


            } while(thisTime<3000);

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

            do{
                pct=(System.currentTimeMillis()-startTime)/(movementDuration*1000);  //tempo attuale - tempo di partenza minore della durata totale in millisecondi
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



        }catch(JSONException e){

        }
    }

    int rdest(){
        Log.d("VALUE", "rdest= "+String.valueOf(rdest));
        return rdest;
    }

    int gdest(){
        Log.d("VALUE", "gdest= "+String.valueOf(gdest));
        return gdest;
    }

    int bdest(){
        Log.d("VALUE", "bdest= "+String.valueOf(bdest));
        return bdest;
    }

    private void handleNetworkRequest(int what, Object payload, int arg1, int arg2) {
        Message msg = mNetworkHandler.obtainMessage();
        msg.what = what;
        msg.obj = payload;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.sendToTarget();
    }

    JSONArray preparePixelsArray() {
        JSONArray pixels_array = new JSONArray();
        JSONObject tmp;
        try {
            for (int i = 0; i < 1072; i++) {
                tmp = new JSONObject();
                tmp.put("a", 0);
                if (i < 522) {
                    tmp.put("g", 255);
                    tmp.put("b", 0);
                    tmp.put("r", 0);
                } else if (i < 613) {
                    tmp.put("r", 255);
                    tmp.put("g", 0);
                    tmp.put("b", 0);
                } else if (i < 791) {
                    tmp.put("b", 255);
                    tmp.put("g", 0);
                    tmp.put("r", 0);
                } else {
                    tmp.put("b", 255);
                    tmp.put("g", 0);
                    tmp.put("r", 255);
                }
                pixels_array.put(tmp);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return pixels_array;
    }

    public void openInstruction(View view) {
        Intent intent = new Intent(this, Instruction1Activity.class);
        startActivity(intent);
    }

}
