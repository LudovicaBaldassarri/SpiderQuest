package ragnatela.did.SpiderQuest;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

class RagnatelaHandler {

    private static int[] nodeA_a = {5,46,522};
    private static int[] nodeA_b = {15,36,790};
    private static int[] nodeA_c = {25, 1071};

    private static int[] nodeB_a = {65,172,543};
    private static int[] nodeB_b = {86,150,651};
    private static int[] nodeB_c = {108,128,843};

    private static int[] nodeC_a = {202,302,558};
    private static int[] nodeC_b = {218,285,685};
    private static int[] nodeC_c = {234,270,904};

    private static int[] nodeD_a = {330,410,577};
    private static int[] nodeD_b = {345,395,728};
    private static int[] nodeD_c = {360,382,975};

    private static int[] nodeE_a = {434,512,598};
    private static int[] nodeE_b = {446,498,765};
    private static int[] nodeE_c = {462,482,1032};

    private static int[][] nodes = {nodeA_a,nodeA_b,nodeA_c,nodeB_a,nodeB_b,nodeB_c,nodeC_a,nodeC_b,nodeC_c,nodeD_a,nodeD_b,nodeD_c,nodeE_a,nodeE_b,nodeE_c};

    private static int node; //index

    private int centerLogoX=15;
    private int getCenterLogoY=15;

    private static int rstopped;
    private static int gstopped;
    private static int bstopped;

    private String red = "ff0000";
    private String blu = "0000ff";
    private String green = "00ff00";
    private String white = "ffffff";

    private int beginXRA;
    private int beginYRA;
    private int beginXGA;
    private int beginYGA;
    private int beginXBA;
    private int beginYBA;
    private int endXRA;
    private int endYRA;
    private int endXGA;
    private int endYGA;
    private int endXBA;
    private int endYBA;
    private int distXRA;
    private int distYRA;
    private int distXGA;
    private int distYGA;
    private int distXBA;
    private int distYBA;
    private int xRA;
    private int yRA;
    private int xGA;
    private int yGA;
    private int xBA;
    private int yBA;
    private long startTime;
    private long waitTime;
    private float movementDuration=1.5f; //time in seconds
    private int[] exit={1,2,3,4,5};
    private int[] dest = new int[3];
    private int rdest;
    private int gdest;
    private int bdest;
    private int endXtmp;
    private int endYtmp;
    private int desttmp;

    private static JSONArray pixels_array_LED;
    private static JSONArray pixels_array_DISPLAY;

    private static String host_url;
    private static int host_port;
    private static int gameSpeed;

    private boolean exitGame;

    private Handler mNetworkHandler, mMainHandler;
    private NetworkThread mNetworkThread = null;

    private Handler handler = new Handler();

    private Context context;

    private static MediaPlayer mp;

    RagnatelaHandler(String hu, int hp, int gs, Context c) {
        host_url = hu;
        host_port = hp;
        gameSpeed = gs;
        context = c;

        exitGame = false;

        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG).show();
            }
        };

        startHandlerThread();
        handleNetworkRequest(NetworkThread.SET_SERVER_DATA, host_url, host_port ,0);
    }

    void playMusic(Context c) {
        if(mp == null){
            mp = MediaPlayer.create(c, R.raw.menu);
            mp.start();
            mp.setLooping(true);
        }
    }

    void resumeMusic(){
        if(!mp.isPlaying()){
            mp.start();
            mp.setLooping(true);
        }
    }

    void pauseMusic(){
        mp.pause();
    }

    void resetMediaPlayer(){
        mp.stop();
        mp = null;
    }

    void setGameSpeed(int gs) {
        gameSpeed = gs;
    }

    Handler getmNetworkHandler(){
        return mNetworkHandler;
    }

    private void init(){
        try{
            pixels_array_LED = prepareLedPixelsArray();
            pixels_array_DISPLAY = prepareDisplayPixelsArray();

            handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array_LED, 0, 0);
            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0, 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static JSONArray prepareLedPixelsArray() {
        JSONArray pixels_array = new JSONArray();
        JSONObject tmp;
        try {
            for (int i = 0; i < 1072; i++) {
                tmp = new JSONObject();
                tmp.put("a", 0);
                if (isInNode(i)) {
                    tmp.put("g", 255);
                    tmp.put("b", 255);
                    tmp.put("r", 255);
                } else {
                    tmp.put("r", 0);
                    tmp.put("g", 0);
                    tmp.put("b", 0);
                }
                pixels_array.put(tmp);
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return pixels_array;
    }

    private static boolean isInNode(int current){
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

    private static JSONArray prepareDisplayPixelsArray() {
        JSONArray pixels_array = new JSONArray();
        JSONObject tmp;
        try {
            for (int i = 0; i < 1072; i++) {
                tmp = new JSONObject();
                tmp.put("a", 0);
                tmp.put("g", 0);
                tmp.put("b", 0);
                tmp.put("r", 0);
                pixels_array.put(tmp);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return pixels_array;
    }

    void showLogo(){
        try{
            init();
            drawLogo(centerLogoX, getCenterLogoY);
            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0, 0);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void drawLogo(int x, int y) throws JSONException{

        turnOnLed(red,x-14,y-11);
        turnOnLed(red,x-14,y-10);
        turnOnLed(red,x-14,y-9);
        turnOnLed(red,x-14,y-8);
        turnOnLed(red,x-14,y-7);
        turnOnLed(red,x-14,y-4);
        turnOnLed(red,x-14,y-3);
        turnOnLed(red,x-14,y+2);
        turnOnLed(red,x-14,y+3);
        turnOnLed(red,x-14,y+4);
        turnOnLed(red,x-14,y+5);
        turnOnLed(red,x-14,y+6);
        turnOnLed(red,x-14,y+7);
        turnOnLed(red,x-14,y+8);
        turnOnLed(red,x-14,y+9);
        turnOnLed(red,x-14,y+10);

        turnOnLed(red,x-13,y-12);
        turnOnLed(red,x-13,y-11);
        turnOnLed(red,x-13,y-10);
        turnOnLed(red,x-13,y-9);
        turnOnLed(red,x-13,y-8);
        turnOnLed(red,x-13,y-7);
        turnOnLed(red,x-13,y-4);
        turnOnLed(red,x-13,y-3);
        turnOnLed(red,x-13,y+3);
        turnOnLed(red,x-13,y+4);
        turnOnLed(red,x-13,y+5);
        turnOnLed(red,x-13,y+6);
        turnOnLed(red,x-13,y+7);
        turnOnLed(red,x-13,y+8);
        turnOnLed(red,x-13,y+9);
        turnOnLed(red,x-13,y+10);
        turnOnLed(red,x-13,y+11);

        turnOnLed(red,x-12,y-12);
        turnOnLed(red,x-12,y-11);
        turnOnLed(red,x-12,y-8);
        turnOnLed(red,x-12,y-7);
        turnOnLed(red,x-12,y-6);
        turnOnLed(red,x-12,y-5);
        turnOnLed(red,x-12,y-4);
        turnOnLed(red,x-12,y-3);
        turnOnLed(red,x-12,y+2);
        turnOnLed(red,x-12,y+3);
        turnOnLed(red,x-12,y+10);
        turnOnLed(red,x-12,y+11);

        turnOnLed(red,x-11,y-12);
        turnOnLed(red,x-11,y-11);
        turnOnLed(red,x-11,y-8);
        turnOnLed(red,x-11,y-7);
        turnOnLed(red,x-11,y-6);
        turnOnLed(red,x-11,y-5);
        turnOnLed(red,x-11,y-4);
        turnOnLed(red,x-11,y+3);
        turnOnLed(red,x-11,y+4);
        turnOnLed(red,x-11,y+5);
        turnOnLed(red,x-11,y+6);
        turnOnLed(red,x-11,y+7);
        turnOnLed(red,x-11,y+8);
        turnOnLed(red,x-11,y+9);
        turnOnLed(red,x-11,y+10);
        turnOnLed(red,x-11,y+11);
        turnOnLed(red,x-11,y+12);
        turnOnLed(red,x-11,y+13);

        turnOnLed(red,x-10,y+4);
        turnOnLed(red,x-10,y+5);
        turnOnLed(red,x-10,y+6);
        turnOnLed(red,x-10,y+7);
        turnOnLed(red,x-10,y+8);
        turnOnLed(red,x-10,y+9);
        turnOnLed(red,x-10,y+10);
        turnOnLed(red,x-10,y+11);
        turnOnLed(red,x-10,y+12);
        turnOnLed(red,x-10,y+13);

        turnOnLed(red,x-9,y-12);
        turnOnLed(red,x-9,y-11);
        turnOnLed(red,x-9,y-10);
        turnOnLed(red,x-9,y-9);
        turnOnLed(red,x-9,y-8);
        turnOnLed(red,x-9,y-7);
        turnOnLed(red,x-9,y-6);
        turnOnLed(red,x-9,y-5);
        turnOnLed(red,x-9,y-4);
        turnOnLed(red,x-9,y-3);

        turnOnLed(red,x-8,y-12);
        turnOnLed(red,x-8,y-11);
        turnOnLed(red,x-8,y-10);
        turnOnLed(red,x-8,y-9);
        turnOnLed(red,x-8,y-8);
        turnOnLed(red,x-8,y-7);
        turnOnLed(red,x-8,y-6);
        turnOnLed(red,x-8,y-5);
        turnOnLed(red,x-8,y-4);
        turnOnLed(red,x-8,y-3);
        turnOnLed(red,x-8,y+2);
        turnOnLed(red,x-8,y+3);
        turnOnLed(red,x-8,y+4);
        turnOnLed(red,x-8,y+5);
        turnOnLed(red,x-8,y+6);
        turnOnLed(red,x-8,y+7);
        turnOnLed(red,x-8,y+8);
        turnOnLed(red,x-8,y+9);
        turnOnLed(red,x-8,y+10);

        turnOnLed(red,x-7,y-12);
        turnOnLed(red,x-7,y-8);
        turnOnLed(red,x-7,y+2);
        turnOnLed(red,x-7,y+3);
        turnOnLed(red,x-7,y+4);
        turnOnLed(red,x-7,y+5);
        turnOnLed(red,x-7,y+6);
        turnOnLed(red,x-7,y+7);
        turnOnLed(red,x-7,y+8);
        turnOnLed(red,x-7,y+9);
        turnOnLed(red,x-7,y+10);
        turnOnLed(red,x-7,y+11);

        turnOnLed(red,x-6,y-12);
        turnOnLed(red,x-6,y-11);
        turnOnLed(red,x-6,y-10);
        turnOnLed(red,x-6,y-9);
        turnOnLed(red,x-6,y-8);
        turnOnLed(red,x-6,y+10);
        turnOnLed(red,x-6,y+11);

        turnOnLed(red,x-5,y+2);
        turnOnLed(red,x-5,y+3);
        turnOnLed(red,x-5,y+4);
        turnOnLed(red,x-5,y+5);
        turnOnLed(red,x-5,y+6);
        turnOnLed(red,x-5,y+7);
        turnOnLed(red,x-5,y+8);
        turnOnLed(red,x-5,y+9);
        turnOnLed(red,x-5,y+10);

        turnOnLed(red,x-4,y-12);
        turnOnLed(red,x-4,y-11);
        turnOnLed(red,x-4,y-10);
        turnOnLed(red,x-4,y-9);
        turnOnLed(red,x-4,y-8);
        turnOnLed(red,x-4,y-7);
        turnOnLed(red,x-4,y-6);
        turnOnLed(red,x-4,y-5);
        turnOnLed(red,x-4,y-4);
        turnOnLed(red,x-4,y-3);
        turnOnLed(red,x-4,y+2);
        turnOnLed(red,x-4,y+3);
        turnOnLed(red,x-4,y+4);
        turnOnLed(red,x-4,y+5);
        turnOnLed(red,x-4,y+6);
        turnOnLed(red,x-4,y+7);
        turnOnLed(red,x-4,y+8);
        turnOnLed(red,x-4,y+9);
        turnOnLed(red,x-4,y+10);

        turnOnLed(red,x-3,y-12);
        turnOnLed(red,x-3,y-11);
        turnOnLed(red,x-3,y-10);
        turnOnLed(red,x-3,y-9);
        turnOnLed(red,x-3,y-8);
        turnOnLed(red,x-3,y-7);
        turnOnLed(red,x-3,y-6);
        turnOnLed(red,x-3,y-5);
        turnOnLed(red,x-3,y-4);
        turnOnLed(red,x-3,y-3);

        turnOnLed(red,x-2,y+2);
        turnOnLed(red,x-2,y+3);
        turnOnLed(red,x-2,y+4);
        turnOnLed(red,x-2,y+5);
        turnOnLed(red,x-2,y+6);
        turnOnLed(red,x-2,y+7);
        turnOnLed(red,x-2,y+8);
        turnOnLed(red,x-2,y+9);
        turnOnLed(red,x-2,y+10);
        turnOnLed(red,x-2,y+11);


        turnOnLed(red,x-1,y-12);
        turnOnLed(red,x-1,y-11);
        turnOnLed(red,x-1,y-10);
        turnOnLed(red,x-1,y-9);
        turnOnLed(red,x-1,y-8);
        turnOnLed(red,x-1,y-7);
        turnOnLed(red,x-1,y-6);
        turnOnLed(red,x-1,y-5);
        turnOnLed(red,x-1,y-4);
        turnOnLed(red,x-1,y-3);
        turnOnLed(red,x-1,y+2);
        turnOnLed(red,x-1,y+3);
        turnOnLed(red,x-1,y+4);
        turnOnLed(red,x-1,y+5);
        turnOnLed(red,x-1,y+6);
        turnOnLed(red,x-1,y+7);
        turnOnLed(red,x-1,y+8);
        turnOnLed(red,x-1,y+9);
        turnOnLed(red,x-1,y+10);
        turnOnLed(red,x-1,y+11);

        turnOnLed(red,x,y-12);
        turnOnLed(red,x,y-11);
        turnOnLed(red,x,y-10);
        turnOnLed(red,x,y-9);
        turnOnLed(red,x,y-8);
        turnOnLed(red,x,y-7);
        turnOnLed(red,x,y-6);
        turnOnLed(red,x,y-5);
        turnOnLed(red,x,y-4);
        turnOnLed(red,x,y-3);
        turnOnLed(red,x,y+2);
        turnOnLed(red,x,y+3);
        turnOnLed(red,x,y+6);
        turnOnLed(red,x,y+7);
        turnOnLed(red,x,y+10);
        turnOnLed(red,x,y+11);

        turnOnLed(red,x+1,y-12);
        turnOnLed(red,x+1,y-11);
        turnOnLed(red,x+1,y-4);
        turnOnLed(red,x+1,y-3);
        turnOnLed(red,x+1,y+2);
        turnOnLed(red,x+1,y+3);
        turnOnLed(red,x+1,y+10);
        turnOnLed(red,x+1,y+11);

        turnOnLed(red,x+2,y-12);
        turnOnLed(red,x+2,y-11);
        turnOnLed(red,x+2,y-4);
        turnOnLed(red,x+2,y-3);

        turnOnLed(red,x+3,y-12);
        turnOnLed(red,x+3,y-11);
        turnOnLed(red,x+3,y-10);
        turnOnLed(red,x+3,y-9);
        turnOnLed(red,x+3,y-8);
        turnOnLed(red,x+3,y-7);
        turnOnLed(red,x+3,y-6);
        turnOnLed(red,x+3,y-5);
        turnOnLed(red,x+3,y-4);
        turnOnLed(red,x+3,y-3);
        turnOnLed(red,x+3,y+3);
        turnOnLed(red,x+3,y+4);
        turnOnLed(red,x+3,y+5);
        turnOnLed(red,x+3,y+6);
        turnOnLed(red,x+3,y+7);
        turnOnLed(red,x+3,y+10);
        turnOnLed(red,x+3,y+11);

        turnOnLed(red,x+4,y-11);
        turnOnLed(red,x+4,y-10);
        turnOnLed(red,x+4,y-9);
        turnOnLed(red,x+4,y-8);
        turnOnLed(red,x+4,y-7);
        turnOnLed(red,x+4,y-6);
        turnOnLed(red,x+4,y-5);
        turnOnLed(red,x+4,y-4);
        turnOnLed(red,x+4,y+2);
        turnOnLed(red,x+4,y+3);
        turnOnLed(red,x+4,y+4);
        turnOnLed(red,x+4,y+5);
        turnOnLed(red,x+4,y+6);
        turnOnLed(red,x+4,y+7);
        turnOnLed(red,x+4,y+10);
        turnOnLed(red,x+4,y+11);

        turnOnLed(red,x+5,y+2);
        turnOnLed(red,x+5,y+3);
        turnOnLed(red,x+5,y+6);
        turnOnLed(red,x+5,y+7);
        turnOnLed(red,x+5,y+8);
        turnOnLed(red,x+5,y+9);
        turnOnLed(red,x+5,y+10);
        turnOnLed(red,x+5,y+11);

        turnOnLed(red,x+6,y-12);
        turnOnLed(red,x+6,y-11);
        turnOnLed(red,x+6,y-10);
        turnOnLed(red,x+6,y-9);
        turnOnLed(red,x+6,y-8);
        turnOnLed(red,x+6,y-7);
        turnOnLed(red,x+6,y-6);
        turnOnLed(red,x+6,y-5);
        turnOnLed(red,x+6,y-4);
        turnOnLed(red,x+6,y-3);
        turnOnLed(red,x+6,y+2);
        turnOnLed(red,x+6,y+3);
        turnOnLed(red,x+6,y+6);
        turnOnLed(red,x+6,y+7);
        turnOnLed(red,x+6,y+8);
        turnOnLed(red,x+6,y+9);
        turnOnLed(red,x+6,y+10);

        turnOnLed(red,x+7,y-12);
        turnOnLed(red,x+7,y-11);
        turnOnLed(red,x+7,y-10);
        turnOnLed(red,x+7,y-9);
        turnOnLed(red,x+7,y-8);
        turnOnLed(red,x+7,y-7);
        turnOnLed(red,x+7,y-6);
        turnOnLed(red,x+7,y-5);
        turnOnLed(red,x+7,y-4);
        turnOnLed(red,x+7,y-3);

        turnOnLed(red,x+8,y-12);
        turnOnLed(red,x+8,y-11);
        turnOnLed(red,x+8,y-8);
        turnOnLed(red,x+8,y-7);
        turnOnLed(red,x+8,y-4);
        turnOnLed(red,x+8,y-3);
        turnOnLed(red,x+8,y+2);
        turnOnLed(red,x+8,y+3);

        turnOnLed(red,x+9,y-12);
        turnOnLed(red,x+9,y-11);
        turnOnLed(red,x+9,y-4);
        turnOnLed(red,x+9,y-3);
        turnOnLed(red,x+9,y+2);
        turnOnLed(red,x+9,y+3);

        turnOnLed(red,x+10,y+2);
        turnOnLed(red,x+10,y+3);
        turnOnLed(red,x+10,y+4);
        turnOnLed(red,x+10,y+5);
        turnOnLed(red,x+10,y+6);
        turnOnLed(red,x+10,y+7);
        turnOnLed(red,x+10,y+8);
        turnOnLed(red,x+10,y+9);
        turnOnLed(red,x+10,y+10);
        turnOnLed(red,x+10,y+11);
        turnOnLed(red,x+10,y+12);
        turnOnLed(red,x+10,y+13);

        turnOnLed(red,x+11,y-12);
        turnOnLed(red,x+11,y-11);
        turnOnLed(red,x+11,y-10);
        turnOnLed(red,x+11,y-9);
        turnOnLed(red,x+11,y-8);
        turnOnLed(red,x+11,y-7);
        turnOnLed(red,x+11,y-6);
        turnOnLed(red,x+11,y-5);
        turnOnLed(red,x+11,y-4);
        turnOnLed(red,x+11,y-3);
        turnOnLed(red,x+11,y+2);
        turnOnLed(red,x+11,y+3);
        turnOnLed(red,x+11,y+4);
        turnOnLed(red,x+11,y+5);
        turnOnLed(red,x+11,y+6);
        turnOnLed(red,x+11,y+7);
        turnOnLed(red,x+11,y+8);
        turnOnLed(red,x+11,y+9);
        turnOnLed(red,x+11,y+10);
        turnOnLed(red,x+11,y+11);
        turnOnLed(red,x+11,y+12);
        turnOnLed(red,x+11,y+13);

        turnOnLed(red,x+12,y-12);
        turnOnLed(red,x+12,y-11);
        turnOnLed(red,x+12,y-10);
        turnOnLed(red,x+12,y-9);
        turnOnLed(red,x+12,y-8);
        turnOnLed(red,x+12,y-7);
        turnOnLed(red,x+12,y-6);
        turnOnLed(red,x+12,y-5);
        turnOnLed(red,x+12,y-4);
        turnOnLed(red,x+12,y-3);
        turnOnLed(red,x+12,y+2);
        turnOnLed(red,x+12,y+3);

        turnOnLed(red,x+13,y-12);
        turnOnLed(red,x+13,y-11);
        turnOnLed(red,x+13,y-7);
        turnOnLed(red,x+13,y-6);
        turnOnLed(red,x+13,y+2);
        turnOnLed(red,x+13,y+3);

        turnOnLed(red,x+14,y-12);
        turnOnLed(red,x+14,y-11);
        turnOnLed(red,x+14,y-10);
        turnOnLed(red,x+14,y-9);
        turnOnLed(red,x+14,y-8);
        turnOnLed(red,x+14,y-7);
        turnOnLed(red,x+14,y-6);
        turnOnLed(red,x+14,y-5);
        turnOnLed(red,x+14,y-4);
        turnOnLed(red,x+14,y-3);
        turnOnLed(red,x+14,y-2);

        turnOnLed(red,x+15,y-11);
        turnOnLed(red,x+15,y-10);
        turnOnLed(red,x+15,y-9);
        turnOnLed(red,x+15,y-8);
        turnOnLed(red,x+15,y-7);
        turnOnLed(red,x+15,y-5);
        turnOnLed(red,x+15,y-4);
        turnOnLed(red,x+15,y-3);
        turnOnLed(red,x+15,y-2);
    }

    void showArrow(){
        try{
            drawArrow(centerLogoX, getCenterLogoY);
            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0, 0);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void drawArrow(int x, int y) throws JSONException{

        turnOnLed(white,x-9,y+3);
        turnOnLed(white,x-9,y+4);
        turnOnLed(white,x-9,y+5);
        turnOnLed(white,x-9,y+6);
        turnOnLed(white,x-9,y+7);
        turnOnLed(white,x-9,y+8);
        turnOnLed(white,x-9,y+9);
        turnOnLed(white,x-9,y+10);

        turnOnLed(white,x-8,y+2);
        turnOnLed(white,x-8,y+3);
        turnOnLed(white,x-8,y+10);
        turnOnLed(white,x-8,y+11);


        turnOnLed(white,x-7,y+1);
        turnOnLed(white,x-7,y+2);
        turnOnLed(white,x-7,y+3);
        turnOnLed(white,x-7,y+4);
        turnOnLed(white,x-7,y+5);
        turnOnLed(white,x-7,y+6);
        turnOnLed(white,x-7,y+7);
        turnOnLed(white,x-7,y+8);
        turnOnLed(white,x-7,y+9);
        turnOnLed(white,x-7,y+10);
        turnOnLed(white,x-7,y+11);
        turnOnLed(white,x-7,y+12);

        turnOnLed(white,x-6,y+1);
        turnOnLed(white,x-6,y+12);

        turnOnLed(white,x-5,y+1);
        turnOnLed(white,x-5,y+12);

        turnOnLed(white,x-4,y-5);
        turnOnLed(white,x-4,y+1);
        turnOnLed(white,x-4,y+12);

        turnOnLed(white,x-3,y-4);
        turnOnLed(white,x-3,y-5);
        turnOnLed(white,x-3,y+1);
        turnOnLed(white,x-3,y+12);

        turnOnLed(white,x-2,y-3);
        turnOnLed(white,x-2,y-4);
        turnOnLed(white,x-2,y-5);
        turnOnLed(white,x-2,y+1);
        turnOnLed(white,x-2,y+12);

        turnOnLed(white,x-1,y-2);
        turnOnLed(white,x-1,y-3);
        turnOnLed(white,x-1,y-4);
        turnOnLed(white,x-1,y-5);
        turnOnLed(white,x-1,y-6);
        turnOnLed(white,x-1,y-7);
        turnOnLed(white,x-1,y-8);
        turnOnLed(white,x-1,y-9);
        turnOnLed(white,x-1,y-10);
        turnOnLed(white,x-1,y-11);
        turnOnLed(white,x-1,y+1);
        turnOnLed(white,x-1,y+12);

        turnOnLed(white,x,y-1);
        turnOnLed(white,x,y-2);
        turnOnLed(white,x,y-3);
        turnOnLed(white,x,y-4);
        turnOnLed(white,x,y-5);
        turnOnLed(white,x,y-6);
        turnOnLed(white,x,y-7);
        turnOnLed(white,x,y-8);
        turnOnLed(white,x,y-9);
        turnOnLed(white,x,y-10);
        turnOnLed(white,x,y-11);
        turnOnLed(white,x,y+1);
        turnOnLed(white,x,y+12);

        turnOnLed(white,x+1,y-1);
        turnOnLed(white,x+1,y-2);
        turnOnLed(white,x+1,y-3);
        turnOnLed(white,x+1,y-4);
        turnOnLed(white,x+1,y-5);
        turnOnLed(white,x+1,y-6);
        turnOnLed(white,x+1,y-7);
        turnOnLed(white,x+1,y-8);
        turnOnLed(white,x+1,y-9);
        turnOnLed(white,x+1,y-10);
        turnOnLed(white,x+1,y-11);
        turnOnLed(white,x+1,y+1);
        turnOnLed(white,x+1,y+12);

        turnOnLed(white,x+2,y-2);
        turnOnLed(white,x+2,y-3);
        turnOnLed(white,x+2,y-4);
        turnOnLed(white,x+2,y-5);
        turnOnLed(white,x+2,y-6);
        turnOnLed(white,x+2,y-7);
        turnOnLed(white,x+2,y-8);
        turnOnLed(white,x+2,y-9);
        turnOnLed(white,x+2,y-10);
        turnOnLed(white,x+2,y-11);
        turnOnLed(white,x+2,y+1);
        turnOnLed(white,x+2,y+12);

        turnOnLed(white,x+3,y-3);
        turnOnLed(white,x+3,y-4);
        turnOnLed(white,x+3,y-5);
        turnOnLed(white,x+3,y+1);
        turnOnLed(white,x+3,y+12);

        turnOnLed(white,x+4,y-4);
        turnOnLed(white,x+4,y-5);
        turnOnLed(white,x+4,y+1);
        turnOnLed(white,x+4,y+12);

        turnOnLed(white,x+5,y-5);
        turnOnLed(white,x+5,y+1);
        turnOnLed(white,x+5,y+12);

        turnOnLed(white,x+6,y+1);
        turnOnLed(white,x+6,y+12);

        turnOnLed(white,x+7,y+1);
        turnOnLed(white,x+7,y+2);
        turnOnLed(white,x+7,y+3);
        turnOnLed(white,x+7,y+4);
        turnOnLed(white,x+7,y+5);
        turnOnLed(white,x+7,y+6);
        turnOnLed(white,x+7,y+7);
        turnOnLed(white,x+7,y+8);
        turnOnLed(white,x+7,y+9);
        turnOnLed(white,x+7,y+10);
        turnOnLed(white,x+7,y+11);
        turnOnLed(white,x+7,y+12);

        turnOnLed(white,x+8,y+1);
        turnOnLed(white,x+8,y+2);
        turnOnLed(white,x+8,y+3);
        turnOnLed(white,x+8,y+4);
        turnOnLed(white,x+8,y+5);
        turnOnLed(white,x+8,y+8);
        turnOnLed(white,x+8,y+9);
        turnOnLed(white,x+8,y+10);
        turnOnLed(white,x+8,y+11);
        turnOnLed(white,x+8,y+12);

        turnOnLed(white,x+9,y+2);
        turnOnLed(white,x+9,y+3);
        turnOnLed(white,x+9,y+4);
        turnOnLed(white,x+9,y+5);
        turnOnLed(white,x+9,y+8);
        turnOnLed(white,x+9,y+9);
        turnOnLed(white,x+9,y+10);
        turnOnLed(white,x+9,y+11);

        turnOnLed(white,x+10,y+3);
        turnOnLed(white,x+10,y+4);
        turnOnLed(white,x+10,y+5);
        turnOnLed(white,x+10,y+6);
        turnOnLed(white,x+10,y+7);
        turnOnLed(white,x+10,y+8);
        turnOnLed(white,x+10,y+9);
        turnOnLed(white,x+10,y+10);
    }

    private void turnOnLed(String colorString, int x, int y) throws JSONException{
        if(x<0 || y<0 || x>31 || y>31)return;
        int current=computeIndex(x,y);
        try {
            int color = (int)Long.parseLong(colorString, 16);
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color) & 0xFF;
            ((JSONObject) pixels_array_DISPLAY.get(current)).put("r", r);
            ((JSONObject) pixels_array_DISPLAY.get(current)).put("g", g);
            ((JSONObject) pixels_array_DISPLAY.get(current)).put("b", b);//utilizzo funzione computeIndex per calcolare indice di volta in volta

        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    void startGame(){

        beginXRA=9;
        beginYRA=15;
        beginXGA=15;
        beginYGA=20;
        beginXBA=21;
        beginYBA=15;
        endXRA=0;
        endYRA=0;
        endXGA=0;
        endYGA=0;
        endXBA=0;
        endYBA=0;
        distXRA=0;
        distYRA=0;
        distXGA=0;
        distYGA=0;
        distXBA=0;
        distYBA=0;
        xRA=0;
        yRA=0;
        xGA=0;
        yGA=0;
        xBA=0;
        yBA=0;
        startTime=0;
        waitTime=0;

        try{

            for(int i=0;i<3;i++) {
                int index = new Random().nextInt(exit.length);
                int rd = exit[index];

                if (i == 0) {
                    dest[i] = rd;
                } else if (i>0) {
                    for (int j = 0; j < 1; j++) {
                        if ((rd == dest[0])||(rd ==dest[1])) {
                            index = new Random().nextInt(exit.length);
                            rd = exit[index];
                            j--;
                        }else if(rd ==0){
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
            int rdestp = dest[0];
            int gdestp = dest[1];
            int bdestp = dest[2];

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

            spidersMoveInLed(rdest, gdest, bdest);

            System.out.print(rstopped);
            System.out.print(gstopped);
            System.out.print(bstopped);

        }catch (Exception e){
            e.printStackTrace();
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, GameDragActivity.class);
                intent.putExtra("hostUrl", host_url);
                intent.putExtra("hostPort", host_port);
                context.startActivity(intent);
            }
        },200);
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

    private int rdest(){
        Log.d("VALUE", "rdest= "+String.valueOf(rdest));
        return rdest;
    }

    private int gdest(){
        Log.d("VALUE", "gdest= "+String.valueOf(gdest));
        return gdest;
    }

    private int bdest(){
        Log.d("VALUE", "bdest= "+String.valueOf(bdest));
        return bdest;
    }

    private void showSpiders(){
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
                    spegniDisplay();
                    drawSpider(red, xRA, yRA);
                    drawSpider(green, xGA, yGA);
                    drawSpider(blu, xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
                    lastR=currentR;
                }

                if(lastG!=currentG){
                    spegniDisplay();
                    drawSpider(red, xRA, yRA);
                    drawSpider(green, xGA, yGA);
                    drawSpider(blu, xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
                    lastG=currentG;
                }

                if(lastB!=currentB){
                    spegniDisplay();
                    drawSpider(red, xRA, yRA);
                    drawSpider(green, xGA, yGA);
                    drawSpider(blu, xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
                    lastB=currentB;
                }

                thisTime=(System.currentTimeMillis()-waitTime);

            } while(thisTime<3000);

            moveSpiders();


        }catch(JSONException e){
            e.printStackTrace();
        }

    }

    private void moveSpiders(){
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
                pct=(System.currentTimeMillis()-startTime)/(movementDuration*gameSpeed*10);  //tempo attuale - tempo di partenza minore della durata totale in millisecondi
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
                    spegniDisplay();
                    drawSpider(red, xRA, yRA);
                    drawSpider(green, xGA, yGA);
                    drawSpider(blu, xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
                    lastR=currentR;
                }

                if(lastG!=currentG){
                    spegniDisplay();
                    drawSpider(red, xRA, yRA);
                    drawSpider(green, xGA, yGA);
                    drawSpider(blu, xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
                    lastG=currentG;
                }

                if(lastB!=currentB){
                    spegniDisplay();
                    drawSpider(red, xRA, yRA);
                    drawSpider(green, xGA, yGA);
                    drawSpider(blu, xBA, yBA);
                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
                    lastB=currentB;
                }


            } while(pct<1.0f);



        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void drawSpider(String spiderColor, int x, int y) throws JSONException{
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

    private void spidersMoveInLed(int rdest, int gdest, int bdest){
        try{
            Thread.sleep(20*gameSpeed/2);
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
                    ((JSONObject) pixels_array_LED.get(rcurrent)).put("r", 255);
                    ((JSONObject) pixels_array_LED.get(rcurrent)).put("g", 0);
                    ((JSONObject) pixels_array_LED.get(rcurrent)).put("b", 0);
                    if(rcurrent!=rfirst) {
                        if (isInNode(rprevious)){
                            if(rprevious == nodes[gstopped][gindex] && gstop){
                                ((JSONObject) pixels_array_LED.get(rprevious)).put("r", 0);
                                ((JSONObject) pixels_array_LED.get(rprevious)).put("g", 255);
                                ((JSONObject) pixels_array_LED.get(rprevious)).put("b", 0);
                            } else {
                                if(rprevious == nodes[bstopped][bindex] && bstop){
                                    ((JSONObject) pixels_array_LED.get(rprevious)).put("r", 0);
                                    ((JSONObject) pixels_array_LED.get(rprevious)).put("g", 0);
                                    ((JSONObject) pixels_array_LED.get(rprevious)).put("b", 255);
                                } else {
                                    ((JSONObject) pixels_array_LED.get(rprevious)).put("r", 255);
                                    ((JSONObject) pixels_array_LED.get(rprevious)).put("g", 255);
                                    ((JSONObject) pixels_array_LED.get(rprevious)).put("b", 255);
                                }
                            }
                        } else {
                            ((JSONObject) pixels_array_LED.get(rprevious)).put("r", 0);
                            ((JSONObject) pixels_array_LED.get(rprevious)).put("g", 0);
                            ((JSONObject) pixels_array_LED.get(rprevious)).put("b", 0);
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
                    ((JSONObject) pixels_array_LED.get(gcurrent)).put("r", 0);
                    ((JSONObject) pixels_array_LED.get(gcurrent)).put("g", 255);
                    ((JSONObject) pixels_array_LED.get(gcurrent)).put("b", 0);
                    if(gcurrent!=gfirst) {
                        if (isInNode(gprevious)){
                            if(gprevious == nodes[rstopped][rindex] && rstop){
                                ((JSONObject) pixels_array_LED.get(gprevious)).put("r", 255);
                                ((JSONObject) pixels_array_LED.get(gprevious)).put("g", 0);
                                ((JSONObject) pixels_array_LED.get(gprevious)).put("b", 0);
                            } else {
                                if(gprevious == nodes[bstopped][bindex] && bstop){
                                    ((JSONObject) pixels_array_LED.get(gprevious)).put("r", 0);
                                    ((JSONObject) pixels_array_LED.get(gprevious)).put("g", 0);
                                    ((JSONObject) pixels_array_LED.get(gprevious)).put("b", 255);
                                } else {
                                    ((JSONObject) pixels_array_LED.get(gprevious)).put("r", 255);
                                    ((JSONObject) pixels_array_LED.get(gprevious)).put("g", 255);
                                    ((JSONObject) pixels_array_LED.get(gprevious)).put("b", 255);
                                }
                            }
                        } else {
                            ((JSONObject) pixels_array_LED.get(gprevious)).put("r", 0);
                            ((JSONObject) pixels_array_LED.get(gprevious)).put("g", 0);
                            ((JSONObject) pixels_array_LED.get(gprevious)).put("b", 0);
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
                    ((JSONObject) pixels_array_LED.get(bcurrent)).put("r", 0);
                    ((JSONObject) pixels_array_LED.get(bcurrent)).put("g", 0);
                    ((JSONObject) pixels_array_LED.get(bcurrent)).put("b", 255);
                    if(bcurrent!=bfirst) {
                        if (isInNode(bprevious)){
                            if(bprevious == nodes[rstopped][rindex] && rstop){
                                ((JSONObject) pixels_array_LED.get(bprevious)).put("r", 255);
                                ((JSONObject) pixels_array_LED.get(bprevious)).put("g", 0);
                                ((JSONObject) pixels_array_LED.get(bprevious)).put("b", 0);
                            } else {
                                if(bprevious == nodes[gstopped][bindex] && gstop){
                                    ((JSONObject) pixels_array_LED.get(bprevious)).put("r", 0);
                                    ((JSONObject) pixels_array_LED.get(bprevious)).put("g", 255);
                                    ((JSONObject) pixels_array_LED.get(bprevious)).put("b", 0);
                                } else {
                                    ((JSONObject) pixels_array_LED.get(bprevious)).put("r", 255);
                                    ((JSONObject) pixels_array_LED.get(bprevious)).put("g", 255);
                                    ((JSONObject) pixels_array_LED.get(bprevious)).put("b", 255);
                                }
                            }
                        } else {
                            ((JSONObject) pixels_array_LED.get(bprevious)).put("r", 0);
                            ((JSONObject) pixels_array_LED.get(bprevious)).put("g", 0);
                            ((JSONObject) pixels_array_LED.get(bprevious)).put("b", 0);
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
                handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array_LED, 0, 0);
                Thread.sleep(gameSpeed);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showSpidersWin() {
        beginXRA=9;
        beginYRA=10;
        beginXGA=15;
        beginYGA=20;
        beginXBA=21;
        beginYBA=10;
        xRA=0;
        yRA=0;
        xGA=0;
        yGA=0;
        xBA=0;
        yBA=0;

        showSpidersDown();
        //showLedWin();
    }

    private void drawSpiderDown(String spiderColor, int x, int y) throws JSONException{
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

    private void drawSpiderUp(String spiderColor, int x, int y) throws JSONException{
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

    private void showSpidersDown(){


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
            xRA = Math.round(beginXRA);
            yRA = Math.round(beginYRA);
            xGA = Math.round(beginXGA);
            yGA = Math.round(beginYGA);
            xBA = Math.round(beginXBA);
            yBA = Math.round(beginYBA);
            currentR=computeIndex(xRA,yRA);
            currentG=computeIndex(xGA,yGA);
            currentB=computeIndex(xBA,yBA);

            if(lastR!=currentR){
                spegniDisplay();
                drawSpiderDown(red, xRA, yRA);
                drawSpiderDown(green, xGA, yGA);
                drawSpiderDown(blu, xBA, yBA);
                handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
                lastR=currentR;
            }

            if(lastG!=currentG){
                spegniDisplay();
                drawSpiderDown(red, xRA, yRA);
                drawSpiderDown(green, xGA, yGA);
                drawSpiderDown(blu, xBA, yBA);
                handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
                lastG=currentG;
            }

            if(lastB!=currentB){
                spegniDisplay();
                drawSpiderDown(red, xRA, yRA);
                drawSpiderDown(green, xGA, yGA);
                drawSpiderDown(blu, xBA, yBA);
                handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
                lastB=currentB;
            }

            if(!exitGame){
                // Lancio tramite handler le due funzioni in modo che luppino ogni 200 millisecondi
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showSpidersUp();
                    }
                }, 200);
            } else{
                spegniDisplay();
                handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
            }

        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void showSpidersUp(){
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
                spegniDisplay();
                drawSpiderUp(red, xRA, yRA-2);
                drawSpiderUp(green, xGA, yGA-2);
                drawSpiderUp(blu, xBA, yBA-2);
                handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
                lastR=currentR;
            }

            if(lastG!=currentG){
                spegniDisplay();
                drawSpiderUp(red, xRA, yRA-2);
                drawSpiderUp(green, xGA, yGA-2);
                drawSpiderUp(blu, xBA, yBA-2);
                handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
                lastG=currentG;
            }

            if(lastB!=currentB){
                spegniDisplay();
                drawSpiderUp(red, xRA, yRA-2);
                drawSpiderUp(green, xGA, yGA-2);
                drawSpiderUp(blu, xBA, yBA-2);
                handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
                lastB=currentB;
            }

            if(!exitGame){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showSpidersDown();
                    }
                }, 200);
            } else{
                spegniDisplay();
                handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
            }


        }catch(JSONException e){
            e.printStackTrace();
        }


    }

    void showSpidersLoose() {
        beginXRA=9;
        beginYRA=-7;
        beginXGA=15;
        beginYGA=-7;
        beginXBA=21;
        beginYBA=-7;
        xRA=0;
        yRA=0;
        xGA=0;
        yGA=0;
        xBA=0;
        yBA=0;
        waitTime = 0;
        startTime = 0;
        endXRA=21;
        endYRA=29;
        endXGA=9;
        endYGA=29;
        endXBA=15;
        endYBA=29;
        distXRA = 0;
        distYRA = 0;
        distXGA = 0;
        distYGA = 0;
        distXBA = 0;
        distYBA = 0;

        moveSpidersFalling();
    }

    private void drawSpiderFalling(String spiderColor, int x, int y) throws JSONException{
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

//    private void showSpidersFalling(){
//        waitTime=System.currentTimeMillis();
//        long thisTime;
//        int lastR=-1;
//        int lastG=-1;
//        int lastB=-1;
//        int currentR;
//        int currentG;
//        int currentB;
//
//        try{
//
//            do {
//
//                //tempo attuale - tempo di partenza minore della durata totale in millisecondi
//                //Log.d("PCT", "pct= "+ pct);
//                xRA = Math.round(beginXRA);
//                yRA = Math.round(beginYRA);
//                xGA = Math.round(beginXGA);
//                yGA = Math.round(beginYGA);
//                xBA = Math.round(beginXBA);
//                yBA = Math.round(beginYBA);
//                currentR = computeIndex(xRA, yRA);
//                currentG = computeIndex(xGA, yGA);
//                currentB = computeIndex(xBA, yBA);
//
//                if (lastR != currentR) {
//                    spegniDisplay();
//                    drawSpiderFalling(red, xRA, yRA);
//                    drawSpiderFalling(green, xGA, yGA);
//                    drawSpiderFalling(blu, xBA, yBA);
//                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0, 0);
//                    lastR = currentR;
//                }
//
//                if (lastG != currentG) {
//                    spegniDisplay();
//                    drawSpiderFalling(red, xRA, yRA);
//                    drawSpiderFalling(green, xGA, yGA);
//                    drawSpiderFalling(blu, xBA, yBA);
//                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0, 0);
//                    lastG = currentG;
//                }
//
//                if (lastB != currentB) {
//                    spegniDisplay();
//                    drawSpiderFalling(red, xRA, yRA);
//                    drawSpiderFalling(green, xGA, yGA);
//                    drawSpiderFalling(blu, xBA, yBA);
//                    handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0, 0);
//                    lastB = currentB;
//                }
//
//                thisTime = System.currentTimeMillis() - waitTime;
//
//            }while(thisTime<200 && !exitGame);
//
//            if(!exitGame){
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        showSpidersFalling();
//
//                    }
//                }, 200);
//            } else{
//                spegniDisplay();
//                handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
//            }
//        }catch(JSONException e){
//            e.printStackTrace();
//        }
//
//    }

    private void moveSpidersFalling(){

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

        try{

            if(!exitGame) {

                pct = (System.currentTimeMillis() - startTime) / (movementDuration * 1000);

                while (!exitGame && (1.0f > pct)) {

                      //tempo attuale - tempo di partenza minore della durata totale in millisecondi

                    if(!exitGame) {

                        //Log.d("PCT", "pct= "+ pct);
                        xRA = Math.round(beginXRA + pct * distXRA);
                        yRA = Math.round(beginYRA + pct * distYRA);
                        xGA = Math.round(beginXGA + pct * distXGA);
                        yGA = Math.round(beginYGA + pct * distYGA);
                        xBA = Math.round(beginXBA + pct * distXBA);
                        yBA = Math.round(beginYBA + pct * distYBA);
                        currentR = computeIndex(xRA, yRA);
                        currentG = computeIndex(xGA, yGA);
                        currentB = computeIndex(xBA, yBA);

                        if (lastR != currentR) {
                            spegniDisplay();
                            drawSpiderFalling(red, xRA, yRA);
                            drawSpiderFalling(green, xGA, yGA);
                            drawSpiderFalling(blu, xBA, yBA);
                            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0, 0);
                            lastR = currentR;
                        }

                        if (lastG != currentG) {
                            spegniDisplay();
                            drawSpiderFalling(red, xRA, yRA);
                            drawSpiderFalling(green, xGA, yGA);
                            drawSpiderFalling(blu, xBA, yBA);
                            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0, 0);
                            lastG = currentG;
                        }

                        if (lastB != currentB) {
                            spegniDisplay();
                            drawSpiderFalling(red, xRA, yRA);
                            drawSpiderFalling(green, xGA, yGA);
                            drawSpiderFalling(blu, xBA, yBA);
                            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0, 0);
                            lastB = currentB;
                        }
                    }
                    pct = (System.currentTimeMillis() - startTime) / (movementDuration * 1000);
                }

                if(!exitGame){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moveSpidersFalling();
                        }
                    }, 200);
                }
            }else{
                spegniDisplay();
                handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS, pixels_array_DISPLAY, 0 ,0);
            }

        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void spegniDisplay() throws JSONException{
        for (int i = 0; i < pixels_array_DISPLAY.length(); i++) {
            ((JSONObject) pixels_array_DISPLAY.get(i)).put("r", 0);
            ((JSONObject) pixels_array_DISPLAY.get(i)).put("g", 0);
            ((JSONObject) pixels_array_DISPLAY.get(i)).put("b", 0);
        }
    }

    private void spegniLeds() throws JSONException{
        try {
            for (int i = 0; i < pixels_array_LED.length(); i++) {
                if (isInNode(i)) {
                    ((JSONObject) pixels_array_LED.get(i)).put("g", 0);
                    ((JSONObject) pixels_array_LED.get(i)).put("b", 0);
                    ((JSONObject) pixels_array_LED.get(i)).put("r", 0);
                } else {
                    ((JSONObject) pixels_array_LED.get(i)).put("r", 255);
                    ((JSONObject) pixels_array_LED.get(i)).put("g", 255);
                    ((JSONObject) pixels_array_LED.get(i)).put("b", 255);
                }
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    int getRstopped(){
        return rstopped;
    }

    int getBstopped(){
        return bstopped;
    }

    int getGstopped(){
        return gstopped;
    }

    boolean getExit(){
        return exitGame;
    }

    void setExit(boolean exit){
        exitGame = exit;
    }

    private int computeIndex(int x, int y){
        return y*32+x;
    }

    private void startHandlerThread() {
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

    void destroy(){
        if (mNetworkThread != null && mNetworkHandler != null) {
            mNetworkHandler.removeMessages(NetworkThread.SET_PIXELS);
            mNetworkHandler.removeMessages(NetworkThread.SET_DISPLAY_PIXELS);
            mNetworkHandler.removeMessages(NetworkThread.SET_SERVER_DATA);
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

    void showLedsWin() {

        try {

            if(!exitGame){
                for (int i = 0; i < pixels_array_LED.length(); i++) {
                    ((JSONObject) pixels_array_LED.get(i)).put("r", (int) (Math.random() * 255.0f));
                    ((JSONObject) pixels_array_LED.get(i)).put("g", (int) (Math.random() * 255.0f));
                    ((JSONObject) pixels_array_LED.get(i)).put("b", (int) (Math.random() * 255.0f));
                }
                handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array_LED, 0 ,0);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showLedsWin();
                    }
                }, 200);
            } else{
                spegniLeds();
                handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array_LED, 0 ,0);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showLedsLoose() {

        try {

            if(!exitGame) {
                for (int i = 0; i < pixels_array_LED.length(); i++) {
                    ((JSONObject) pixels_array_LED.get(i)).put("r", 255);
                    ((JSONObject) pixels_array_LED.get(i)).put("g", 50);
                    ((JSONObject) pixels_array_LED.get(i)).put("b", 50);
                }
                handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array_LED, 0, 0);

                if (!exitGame) {
                    Thread.sleep(1000);

                    if (!exitGame) {
                        for (int i = 0; i < pixels_array_LED.length(); i++) {
                            ((JSONObject) pixels_array_LED.get(i)).put("r", 0);
                            ((JSONObject) pixels_array_LED.get(i)).put("g", 0);
                            ((JSONObject) pixels_array_LED.get(i)).put("b", 0);
                        }
                        handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array_LED, 0, 0);

                        if (!exitGame) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showLedsLoose();
                                }
                            }, 1000);
                        }
                    }
                } else {
                    spegniLeds();
                    handleNetworkRequest(NetworkThread.SET_PIXELS, pixels_array_LED, 0, 0);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
