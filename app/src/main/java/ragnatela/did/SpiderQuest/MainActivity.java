package ragnatela.did.SpiderQuest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends Activity {

    Unbinder unbinder;

    private String host_url = "192.168.1.32";
    private int host_port = 8080;

    @BindView(R.id.start)
    Button startButton;

    @BindViews({R.id.first_byte_ip, R.id.second_byte_ip, R.id.third_byte_ip, R.id.fourth_byte_ip})
    List<EditText> ip_address_bytes;

    @BindView(R.id.host_port)
    EditText hostPort;

    RagnatelaHandler ragnatelaHandler;

    private Handler mNetworkHandler;
    //private Handler mMainHandler;

    //private NetworkThread mNetworkThread = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        startButton.setEnabled(false);

        TextWatcher myIpTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (checkCorrectIp()) {
                    startButton.setEnabled(true);

                    //handleNetworkRequest(NetworkThread.SET_SERVER_DATA, host_url, host_port, 0);
                    ragnatelaHandler = new RagnatelaHandler(host_url, host_port, 0, MainActivity.this);
                    mNetworkHandler = ragnatelaHandler.getmNetworkHandler();

                    Message msg = mNetworkHandler.obtainMessage();
                    msg.what = NetworkThread.SET_SERVER_DATA;
                    msg.obj = host_url;
                    msg.arg1 = host_port;
                    msg.sendToTarget();
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

//        mMainHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
//            }
//        };
//
//        startHandlerThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        //destroy();
        ragnatelaHandler.destroy();
    }

    @OnClick(R.id.start)
    void startApp(){
        Intent intent = new Intent(this, GameMenuActivity.class);
        intent.putExtra("hostUrl", host_url);
        intent.putExtra("hostPort", host_port);
        startActivity(intent);
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

        //String ip = NetworkThread.getMobileIP();

        port = Integer.parseInt(hostPort.getText().toString());
        if (validIP(sb.toString())
                //&& sb.toString().equals(ip)
                && port >= 0 & port <= 65535) {
            host_url = sb.toString();
            host_port = port;
            return true;
        } else
            return false;
    }

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
            return !ip.endsWith(".");

        } catch (NumberFormatException nfe) {
            return false;
        }
    }

//    public void startHandlerThread() {
//        mNetworkThread = new NetworkThread(mMainHandler);
//        mNetworkThread.start();
//        mNetworkHandler = mNetworkThread.getNetworkHandler();
//    }
//
//    private void handleNetworkRequest(int what, Object payload, int arg1, int arg2) {
//        Message msg = mNetworkHandler.obtainMessage();
//        msg.what = what;
//        msg.obj = payload;
//        msg.arg1 = arg1;
//        msg.arg2 = arg2;
//        msg.sendToTarget();
//    }
//
//    void destroy(){
//        if (mNetworkThread != null && mNetworkHandler != null) {
//            mNetworkHandler.removeMessages(NetworkThread.SET_PIXELS);
//            mNetworkHandler.removeMessages(NetworkThread.SET_DISPLAY_PIXELS);
//            mNetworkHandler.removeMessages(NetworkThread.SET_SERVER_DATA);
//            mNetworkThread.quit();
//            try {
//                mNetworkThread.join(100);
//            } catch (InterruptedException ie) {
//                throw new RuntimeException(ie);
//            } finally {
//                mNetworkThread = null;
//                mNetworkHandler = null;
//            }
//        }
//    }

    //diable back button
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
