package za.ac.uct.cs.videodatausageapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class WebSocketConnector {

    private static final String TAG = "WebSocketConnector";

    private static Context context;
    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable;
    private static WebSocketConnector instance;

    private WebSocketConnector() {
    }

    public static WebSocketConnector getInstance() {
        if (instance == null) {
            instance = new WebSocketConnector();
        }
        return instance;
    }

    public static synchronized void setContext(Context newContext){
        assert newContext != null;
        context = newContext;
    }

    private List<Disposable> getSubscriptions(){
        return new ArrayList<Disposable>(){{
            add(subscribeToControlTopic());
        }};
    }

    private Disposable subscribeToControlTopic() {
        return subscribeToTopic(Config.STOMP_SERVER_CONTROL_ENDPOINT, result -> {
            try {
                JSONObject timeRange = new JSONObject(result.getPayload());
                NetworkSummaryCollector collector = new NetworkSummaryCollector(context);
                String summary = collector.collectSummary(getDeviceId(), timeRange.getLong("start"), timeRange.getLong("end"));
                sendMessage(Config.STOMP_SERVER_SUMMARY_REPORT_ENDPOINT, summary);
            } catch (JSONException e) {
                Log.e(TAG, "subscribeToControlTopic: Invalid response from server");
            }
        });
    }

    public void connectWebSocket(String target) {
        if(target == null)
            return;
        String deviceId = getDeviceId();
        OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @SuppressLint("BadHostnameVerifier")
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, target, null, client);
        List<StompHeader> headers = new ArrayList<StompHeader>() {{
            add(new StompHeader("deviceId", deviceId));
        }};
        resetSubscriptions();

        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            Log.d(TAG, "Stomp connection error");
                            break;
                        case CLOSED:
                            Log.d(TAG, "Stomp connection closed");
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.d(TAG, "Stomp failed server heartbeat");
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);
        List<Disposable> subscriptions = getSubscriptions();
        for(Disposable subscription : subscriptions)
            compositeDisposable.add(subscription);
        mStompClient.connect(headers);
    }

    public String getDeviceId() {
        String uuid;
        SharedPreferences uniqueIdPref = context.getSharedPreferences(Config.PREF_KEY_UNIQUE_ID, Context.MODE_PRIVATE);
        uuid = uniqueIdPref.getString(Config.PREF_KEY_UNIQUE_ID, null);
        if(uuid == null) {
            uuid = UUID.randomUUID().toString()+"_"+Util.hashTimeStamp();
            SharedPreferences.Editor edit = uniqueIdPref.edit();
            edit.putString(Config.PREF_KEY_UNIQUE_ID, uuid);
            edit.apply();
        }
        return uuid;
    }

    public void sendMessage(String endpoint, String content) {
        compositeDisposable.add(mStompClient.send(endpoint, content)
                .compose(applySchedulers())
                .subscribe(
                        () -> Log.d(TAG, String.format("Message sent successfully to %s", endpoint)),
                        (throwable) -> Log.d(TAG, String.format("Error sending message to %s", endpoint, throwable))
                ));
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    public void disconnect() {
        mStompClient.disconnect();
    }

    public boolean isConnected() {
        if(mStompClient == null) return false;
        return mStompClient.isConnected();
    }

    private Disposable subscribeToTopic(String endpoint, SubscriptionCallbackInterface callback){
        return mStompClient.topic(endpoint)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onSubscriptionResult, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
    }
}
