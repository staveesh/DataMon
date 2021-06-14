package za.ac.uct.cs.videodatausageapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                Log.d(TAG, "onReceive: Not connected");
            } else {
                Log.d(TAG, "onReceive: Connected");
                WebSocketConnector connector = WebSocketConnector.getInstance();
                WebSocketConnector.setContext(context);
                if(!connector.isConnected()) connector.connectWebSocket(Util.getWebSocketTarget());
            }
        }
    }
}