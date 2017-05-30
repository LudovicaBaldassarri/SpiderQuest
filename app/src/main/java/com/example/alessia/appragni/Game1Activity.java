package com.example.alessia.appragni;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

@SuppressLint("NewApi")
public class Game1Activity extends AppCompatActivity {

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
    float movementDuration=1.5f; //time in seconds
    int[] exit={1,2,3,4,5};
    int[] dest = new int[3];
    int rdestp;
    int rdest;
    int gdestp;
    int gdest;
    int bdestp;
    int bdest;
    //int i;
    int rd;
    int endXtmp;
    int endYtmp;
    int desttmp;

    int[] nodeA_a = {5,46,522};
    int[] nodeA_b = {15,36,790};
    int[] nodeA_c = {25, 1071};

    int[] nodeB_a = {65,172,543};
    int[] nodeB_b = {86,150,651};
    int[] nodeB_c = {108,128,843};

    int[] nodeC_a = {202,302,558};
    int[] nodeC_b = {218,285,685};
    int[] nodeC_c = {234,270,904};

    int[] nodeD_a = {330,410,577};
    int[] nodeD_b = {345,395,728};
    int[] nodeD_c = {360,382,975};

    int[] nodeE_a = {434,512,598};
    int[] nodeE_b = {446,498,765};
    int[] nodeE_c = {462,482,1032};

    int[][] nodes = {nodeA_a,nodeA_b,nodeA_c,nodeB_a,nodeB_b,nodeB_c,nodeC_a,nodeC_b,nodeC_c,nodeD_a,nodeD_b,nodeD_c,nodeE_a,nodeE_b,nodeE_c};

    int node; //index

    int rstopped;
    int gstopped;
    int bstopped;

    int speed = 100;

    Unbinder unbinder;

    private String host_url = "192.168.1.32";
    private int host_port = 8080;



    @BindViews({R.id.first_byte_ip, R.id.second_byte_ip, R.id.third_byte_ip, R.id.fourth_byte_ip})
    List<EditText> ip_address_bytes;

    @BindView(R.id.host_port)
    EditText hostPort;

    @BindView(R.id.play)
    Button destinationCalculate;

    @BindView(R.id.settings)
    Button b;

    PopupMenu popup;


    private TextWatcher myIpTextWatcher;
    private JSONArray pixels_array;

    private Handler mNetworkHandler, mMainHandler;

    private NetworkThread mNetworkThread = null;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        unbinder = ButterKnife.bind(this);

        destinationCalculate.setEnabled(false);

        popup = new PopupMenu(Game1Activity.this, b);
        popup.getMenuInflater().inflate(R.menu.activity_difficolta, popup.getMenu());

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.easy:
                                speed = 400;
                                menuItem.setChecked(true);
                                break;
                            case R.id.normal:
                                speed = 200;
                                menuItem.setChecked(true);
                                break;
                            case R.id.hard:
                                speed = 100;
                                menuItem.setChecked(true);
                                break;
                            default:
                                speed = 200;
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

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
                Toast.makeText(Game1Activity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
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

        Thread t= new Thread(new Runnable() {
            public void run() {
                try{

                    JSONArray pixels_array = preparePixelsArray();

                    handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array, 0, 0);

                    for(int i=0;i<3;i++) {
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

                    coordinatesCalculate(rdestp);
                    endXRA = endXtmp;
                    endYRA = endYtmp;
                    rdest = desttmp;

                    coordinatesCalculate(gdestp);
                    endXGA = endXtmp;
                    endYGA = endYtmp;
                    gdest = desttmp;

                    coordinatesCalculate(bdestp);
                    endXBA = endXtmp;
                    endYBA = endYtmp;
                    bdest = desttmp;

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

                    startTest(rdest, gdest, bdest, pixels_array);

                    System.out.print(rstopped);
                    System.out.print(gstopped);
                    System.out.print(bstopped);


                }catch(Exception e){

                }
            }
        });
        t.start();

        try {
            t.join(); // wait for thread to finish

            findViewById(R.id.play).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Game1Activity.this, Game2Activity.class);
                    intent.putExtra("Red", rstopped);
                    intent.putExtra("Green", gstopped);
                    intent.putExtra("Blue", bstopped);
                    intent.putExtra("hostUrl", host_url);
                    intent.putExtra("hostPort", host_port);
                    startActivity(intent);
                }
            },1000);

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private void coordinatesCalculate(int destp) {
        switch (destp) {
            case 1:
                endXtmp = 15;
                endYtmp = -4;
                desttmp = 0;
                break;
            case 2:
                endXtmp = 35;
                endYtmp = 10;
                desttmp = 52;
                break;
            case 3:
                endXtmp = 35;
                endYtmp = 26;
                desttmp = 186;
                break;
            case 4:
                endXtmp = -4;
                endYtmp = 26;
                desttmp = 318;
                break;
            case 5:
                endXtmp = -4;
                endYtmp = 10;
                desttmp = 424;
                break;
        }
    }

    private void startTest(int rdest, int gdest, int bdest, JSONArray pixels_array){
        try{
            Thread.sleep(speed/2);
            int rfirst = rdest;
            int gfirst = gdest;
            int bfirst = bdest;

            int rcurrent = rfirst;
            int gcurrent = gfirst;
            int bcurrent = bfirst;

            int rnext = 0;
            int gnext = 0;
            int bnext = 0;

            int rprevious = 0;
            int gprevious = 0;
            int bprevious = 0;

            int rindex = 0;
            int gindex = 0;
            int bindex = 0;
            Random indexGenerator = new Random();

            boolean rjustTurned = false;
            boolean gjustTurned = false;
            boolean bjustTurned = false;

            boolean rdirection = true; //true; ++; false: --
            boolean gdirection = true; //true; ++; false: --
            boolean bdirection = true; //true; ++; false: --
            Random directionGenerator = new Random();

            boolean rstop = false;
            boolean gstop = false;
            boolean bstop = false;
            Random stopGenerator = new Random();

            int rcounter = 0;
            int gcounter = 0;
            int bcounter = 0;

            while(!(rstop && gstop && bstop)){
                if(!rstop){
                    ((JSONObject) pixels_array.get(rcurrent)).put("r", 255);
                    ((JSONObject) pixels_array.get(rcurrent)).put("g", 0);
                    ((JSONObject) pixels_array.get(rcurrent)).put("b", 0);
                    if(rcurrent!=rfirst) {
                        if (isInNode(rprevious)){
                            if(rprevious == nodes[gstopped][gindex] && gstop){
                                ((JSONObject) pixels_array.get(rprevious)).put("r", 0);
                                ((JSONObject) pixels_array.get(rprevious)).put("g", 255);
                                ((JSONObject) pixels_array.get(rprevious)).put("b", 0);
                            } else {
                                if(rprevious == nodes[bstopped][bindex] && bstop){
                                    ((JSONObject) pixels_array.get(rprevious)).put("r", 0);
                                    ((JSONObject) pixels_array.get(rprevious)).put("g", 0);
                                    ((JSONObject) pixels_array.get(rprevious)).put("b", 255);
                                } else {
                                    ((JSONObject) pixels_array.get(rprevious)).put("r", 0);
                                    ((JSONObject) pixels_array.get(rprevious)).put("g", 0);
                                    ((JSONObject) pixels_array.get(rprevious)).put("b", 0);
                                }
                            }
                        } else {
                            ((JSONObject) pixels_array.get(rprevious)).put("r", 255);
                            ((JSONObject) pixels_array.get(rprevious)).put("g", 255);
                            ((JSONObject) pixels_array.get(rprevious)).put("b", 255);
                        }
                    }
                    rprevious = rcurrent;
                    if (isInNode(rcurrent) && !rjustTurned) {
                        rindex = indexGenerator.nextInt(nodes[node].length);
                        rnext = nodes[node][rindex];
                        rstopped = node;
                        if(rnext != rcurrent){
                            rdirection = directionGenerator.nextBoolean();
                        }
                        rjustTurned = true;
                        if (!(rstopped == gstopped && gstop) && !(rstopped == bstopped && bstop)){
                            rcounter++;
                            rstop = (stopGenerator.nextInt(100) < (rcounter*10));
                        } else rcounter--;
                    } else {
                        if(rdirection){
                            switch(rcurrent){
                                case 46: rnext = 6;
                                    break;
                                case 108: rnext = 129;
                                    break;
                                case 172: rnext = 66;
                                    break;
                                case 234: rnext = 271;
                                    break;
                                case 302: rnext = 203;
                                    break;
                                case 360: rnext = 383;
                                    break;
                                case 410: rnext = 331;
                                    break;
                                case 462: rnext = 483;
                                    break;
                                case 512: rnext = 453;
                                    break;
                                case 612: rnext = 522;
                                    break;
                                case 790: rnext = 613;
                                    break;
                                case 1071: rnext = 791;
                                    break;
                                default: rnext = rcurrent+1;
                                    break;
                            }
                        } else {
                            switch(rcurrent){
                                case 5: rnext = 45;
                                    break;
                                case 65: rnext = 171;
                                    break;
                                case 128: rnext = 107;
                                    break;
                                case 202: rnext = 301;
                                    break;
                                case 270: rnext = 233;
                                    break;
                                case 330: rnext = 409;
                                    break;
                                case 382: rnext = 359;
                                    break;
                                case 434: rnext = 511;
                                    break;
                                case 482: rnext = 461;
                                    break;
                                case 522: rnext = 612;
                                    break;
                                case 613: rnext = 790;
                                    break;
                                case 791: rnext = 1071;
                                    break;
                                default: rnext = rcurrent-1;
                                    break;
                            }
                        }
                        rjustTurned = false;
                    }
                    rcurrent = rnext;
                }
                if(!gstop){
                    ((JSONObject) pixels_array.get(gcurrent)).put("r", 0);
                    ((JSONObject) pixels_array.get(gcurrent)).put("g", 255);
                    ((JSONObject) pixels_array.get(gcurrent)).put("b", 0);
                    if(gcurrent!=gfirst) {
                        if (isInNode(gprevious)){
                            if(gprevious == nodes[rstopped][rindex] && rstop){
                                ((JSONObject) pixels_array.get(gprevious)).put("r", 255);
                                ((JSONObject) pixels_array.get(gprevious)).put("g", 0);
                                ((JSONObject) pixels_array.get(gprevious)).put("b", 0);
                            } else {
                                if(gprevious == nodes[bstopped][bindex] && bstop){
                                    ((JSONObject) pixels_array.get(gprevious)).put("r", 0);
                                    ((JSONObject) pixels_array.get(gprevious)).put("g", 0);
                                    ((JSONObject) pixels_array.get(gprevious)).put("b", 255);
                                } else {
                                    ((JSONObject) pixels_array.get(gprevious)).put("r", 0);
                                    ((JSONObject) pixels_array.get(gprevious)).put("g", 0);
                                    ((JSONObject) pixels_array.get(gprevious)).put("b", 0);
                                }
                            }
                        } else {
                            ((JSONObject) pixels_array.get(gprevious)).put("r", 255);
                            ((JSONObject) pixels_array.get(gprevious)).put("g", 255);
                            ((JSONObject) pixels_array.get(gprevious)).put("b", 255);
                        }
                    }
                    gprevious = gcurrent;
                    if (isInNode(gcurrent) && !gjustTurned) {
                        gindex = indexGenerator.nextInt(nodes[node].length);
                        gnext = nodes[node][gindex];
                        gstopped = node;
                        if(gnext != gcurrent){
                            gdirection = directionGenerator.nextBoolean();
                        }
                        gjustTurned = true;
                        if (!(gstopped == rstopped && rstop) && !(gstopped == bstopped && bstop)){
                            gcounter++;
                            gstop = (stopGenerator.nextInt(100) < (gcounter*10));
                        } else gcounter--;
                    } else {
                        if(gdirection){
                            switch(gcurrent){
                                case 46: gnext = 6;
                                    break;
                                case 108: gnext = 129;
                                    break;
                                case 172: gnext = 66;
                                    break;
                                case 234: gnext = 271;
                                    break;
                                case 302: gnext = 203;
                                    break;
                                case 360: gnext = 383;
                                    break;
                                case 410: gnext = 331;
                                    break;
                                case 462: gnext = 483;
                                    break;
                                case 512: gnext = 453;
                                    break;
                                case 612: gnext = 522;
                                    break;
                                case 790: gnext = 613;
                                    break;
                                case 1071: gnext = 791;
                                    break;
                                default: gnext = gcurrent+1;
                                    break;
                            }
                        } else {
                            switch(gcurrent){
                                case 5: gnext = 45;
                                    break;
                                case 65: gnext = 171;
                                    break;
                                case 128: gnext = 107;
                                    break;
                                case 202: gnext = 301;
                                    break;
                                case 270: gnext = 233;
                                    break;
                                case 330: gnext = 409;
                                    break;
                                case 382: gnext = 359;
                                    break;
                                case 434: gnext = 511;
                                    break;
                                case 482: gnext = 461;
                                    break;
                                case 522: gnext = 612;
                                    break;
                                case 613: gnext = 790;
                                    break;
                                case 791: gnext = 1071;
                                    break;
                                default: gnext = gcurrent-1;
                                    break;
                            }
                        }
                        gjustTurned = false;
                    }
                    gcurrent = gnext;
                }
                if(!bstop){
                    ((JSONObject) pixels_array.get(bcurrent)).put("r", 0);
                    ((JSONObject) pixels_array.get(bcurrent)).put("g", 0);
                    ((JSONObject) pixels_array.get(bcurrent)).put("b", 255);
                    if(bcurrent!=bfirst) {
                        if (isInNode(bprevious)){
                            if(bprevious == nodes[rstopped][rindex] && rstop){
                                ((JSONObject) pixels_array.get(bprevious)).put("r", 255);
                                ((JSONObject) pixels_array.get(bprevious)).put("g", 0);
                                ((JSONObject) pixels_array.get(bprevious)).put("b", 0);
                            } else {
                                if(bprevious == nodes[gstopped][bindex] && gstop){
                                    ((JSONObject) pixels_array.get(bprevious)).put("r", 0);
                                    ((JSONObject) pixels_array.get(bprevious)).put("g", 255);
                                    ((JSONObject) pixels_array.get(bprevious)).put("b", 0);
                                } else {
                                    ((JSONObject) pixels_array.get(bprevious)).put("r", 0);
                                    ((JSONObject) pixels_array.get(bprevious)).put("g", 0);
                                    ((JSONObject) pixels_array.get(bprevious)).put("b", 0);
                                }
                            }
                        } else {
                            ((JSONObject) pixels_array.get(bprevious)).put("r", 255);
                            ((JSONObject) pixels_array.get(bprevious)).put("g", 255);
                            ((JSONObject) pixels_array.get(bprevious)).put("b", 255);
                        }
                    }
                    bprevious = bcurrent;
                    if (isInNode(bcurrent) && !bjustTurned) {
                        bindex = indexGenerator.nextInt(nodes[node].length);
                        bnext = nodes[node][bindex];
                        bstopped = node;
                        if(bnext != bcurrent){
                            bdirection = directionGenerator.nextBoolean();
                        }
                        bjustTurned = true;
                        if (!(bstopped == rstopped && rstop) && !(bstopped == gstopped && gstop)){
                            bcounter++;
                            bstop = (stopGenerator.nextInt(100) < (bcounter*10));
                        } else bcounter--;
                    } else {
                        if(bdirection){
                            switch(bcurrent){
                                case 46: bnext = 6;
                                    break;
                                case 108: bnext = 129;
                                    break;
                                case 172: bnext = 66;
                                    break;
                                case 234: bnext = 271;
                                    break;
                                case 302: bnext = 203;
                                    break;
                                case 360: bnext = 383;
                                    break;
                                case 410: bnext = 331;
                                    break;
                                case 462: bnext = 483;
                                    break;
                                case 512: bnext = 453;
                                    break;
                                case 612: bnext = 522;
                                    break;
                                case 790: bnext = 613;
                                    break;
                                case 1071: bnext = 791;
                                    break;
                                default: bnext = bcurrent+1;
                                    break;
                            }
                        } else {
                            switch(bcurrent){
                                case 5: bnext = 45;
                                    break;
                                case 65: bnext = 171;
                                    break;
                                case 128: bnext = 107;
                                    break;
                                case 202: bnext = 301;
                                    break;
                                case 270: bnext = 233;
                                    break;
                                case 330: bnext = 409;
                                    break;
                                case 382: bnext = 359;
                                    break;
                                case 434: bnext = 511;
                                    break;
                                case 482: bnext = 461;
                                    break;
                                case 522: bnext = 612;
                                    break;
                                case 613: bnext = 790;
                                    break;
                                case 791: bnext = 1071;
                                    break;
                                default: bnext = bcurrent-1;
                                    break;
                            }
                        }
                        bjustTurned = false;
                    }
                    bcurrent = bnext;
                }
                handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array, 0, 0);
                Thread.sleep(speed);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isInNode(int current){
        int tmp;
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[i].length; j++) {
                tmp = nodes[i][j];
                if(tmp == current) {
                    node = i;
                    return true;
                }
            }
        }
        return false;
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


   /* public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.activity_difficolta, popup.getMenu());
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.easy:
                        speed = 400;
                        menuItem.setChecked(true);
                        break;
                    case R.id.normal:
                        speed = 200;
                        menuItem.setChecked(true);
                        break;
                    case R.id.hard:
                        speed = 100;
                        menuItem.setChecked(true);
                        break;
                    default:
                        speed = 200;
                        break;
                }
                return true;
            }
        });
        popup.show();
    }*/

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
                if (isInNode(i)) {
                    tmp.put("g", 0);
                    tmp.put("b", 0);
                    tmp.put("r", 0);
                } else {
                    tmp.put("r", 255);
                    tmp.put("g", 255);
                    tmp.put("b", 255);
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

    public int getStoppedRed(){
        return rstopped;
    }
    public int getStoppedGreen(){
        return gstopped;
    }
    public int getStoppedBlue(){
        return bstopped;
    }

}