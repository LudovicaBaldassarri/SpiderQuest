package ragnatela.did.SpiderQuest;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class NetworkThread extends HandlerThread {

    final static int SET_PIXELS = 1;
    final static int SET_DISPLAY_PIXELS = 2;
    final static int SET_SERVER_DATA = 3;
    private static final String TAG = "bah";

    private Handler mWorkHandler;
    private Handler mMainThreadHandler;
    private OkHttpClient okHttpClient = new OkHttpClient();

    private String host_url = "192.168.1.32";
    private int host_port = 8080;

    NetworkThread(Handler mainThreadHandler) {
        super("NetworkThread");
        this.setDaemon(false);
        this.mMainThreadHandler = mainThreadHandler;
    }

    @Override
    protected void onLooperPrepared() {
        synchronized (this) {
            this.mWorkHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    JSONArray pixels_array;
                    String command;

                    switch (msg.what) {
                        case SET_PIXELS:
                            pixels_array = (JSONArray) msg.obj;
                            command = "setPixels";
                            performNetworkRequest(command, pixels_array);
                            break;
                        case SET_DISPLAY_PIXELS:
                            pixels_array = (JSONArray) msg.obj;
                            command = "setDisplayPixels";
                            performNetworkRequest(command, pixels_array);
                            break;
                        case SET_SERVER_DATA:
                            host_url = (String) msg.obj;
                            host_port = msg.arg1;
                            break;
                        default:
                            break;
                    }
                }
            };
            notifyAll();
        }
    }

    private void performNetworkRequest(String command, JSONArray payload) {
        RequestBody body = new FormBody.Builder()
                .add("pixels", payload.toString())
                .build();
        String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
        Request request = new Request.Builder()
                .url(new HttpUrl.Builder()
                        .scheme("http")
                        .host(host_url)
                        .port(host_port).build().toString() + command)
                .addHeader("content-type", CONTENT_TYPE_JSON)
                .post(body)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            Log.d("POST_RESULT", response.body().string());
        } catch (IOException e) {
            //network error, notify the main thread throught it's handler
            Message msg = mMainThreadHandler.obtainMessage();
            msg.obj = e.getMessage();
            msg.sendToTarget();
        }
    }

    synchronized Handler getNetworkHandler() {
        while (mWorkHandler == null) {
            try {
                wait();
            } catch (InterruptedException exception) {
                break;
            }
        }
        return mWorkHandler;
    }

    public static String getMobileIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, "Exception in Get IP Address: " + ex.toString());
        }
        return null;
    }
}
