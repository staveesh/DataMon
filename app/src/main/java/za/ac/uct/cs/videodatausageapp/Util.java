package za.ac.uct.cs.videodatausageapp;

import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Util {
    private static final String TAG = "Util";
    public static String resolveServer(){
        try {
            InetAddress inetAddress = InetAddress.getByName(Config.SERVER_HOST_ADDRESS);
            return inetAddress.getHostAddress();
        }
        catch (UnknownHostException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getWebSocketTarget() {
        String serverIP = null;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<String> callable = Util::resolveServer;
        Future<String> future = executor.submit(callable);
        try {
            serverIP = future.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Failed to resolve server");
            return null;
        }
        executor.shutdown();
        return "ws://" + serverIP + ":" + Config.SERVER_PORT + Config.STOMP_SERVER_CONNECT_ENDPOINT;
    }
}
