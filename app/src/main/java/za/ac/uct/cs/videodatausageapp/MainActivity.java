package za.ac.uct.cs.videodatausageapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import za.ac.uct.cs.videodatausageapp.CollectionService.CollectionBinder;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_CODE = 6789;
    private CollectionService collector;
    private boolean isBound = false;
    private boolean isBindingToService = false;
    private static MainActivity app;
    private NetworkChangeReceiver connectivityReceiver;
    private String institution = null;
    private String consent = "NO";
    private TextView welcomeText, prText;
    private ProgressBar progressBar;
    private Button uploadBtn;
    private int i = 0;
    private Handler handler = new Handler();
    private NetworkSummaryCollector nCollector;
    private Date currentTime;

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService
            // instance
            CollectionBinder binder = (CollectionBinder) service;
            collector = binder.getService();
            isBound = true;
            isBindingToService = false;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void bindToService() {
        if (!isBindingToService && !isBound) {
            // Bind to the scheduler service if it is not bounded
            Intent intent = new Intent(this, CollectionService.class);
            bindService(intent, serviceConn, Context.BIND_AUTO_CREATE);
            isBindingToService = true;
        }
    }

    public CollectionService getCollector() {
        if (isBound) {
            return this.collector;
        } else {
            bindToService();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = this;
        setContentView(R.layout.activity_main);
        welcomeText = findViewById(R.id.welcomeText);
        progressBar = findViewById(R.id.pBar);
        prText = findViewById(R.id.tView);
        uploadBtn = findViewById(R.id.btnShow);
        if (consent.equalsIgnoreCase("no")) {
            consentDialogWrapper();
        }
        nCollector = new NetworkSummaryCollector(getApplicationContext());
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(now);
                c.add(Calendar.MONTH, -2);
                currentTime = c.getTime();
                i = progressBar.getProgress();
                long diff = 1 + TimeUnit.DAYS.convert(now.getTime() - currentTime.getTime(),
                        TimeUnit.MILLISECONDS);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (currentTime.before(now)) {
                            c.add(Calendar.DATE, 1);
                            Date next = c.getTime();
                            String summary = nCollector.collectSummary(WebSocketConnector.getInstance().getDeviceId(),
                                    currentTime.getTime(), next.getTime());
                            WebSocketConnector.getInstance().
                                    sendMessage(Config.STOMP_SERVER_SUMMARY_REPORT_ENDPOINT, summary);
                            currentTime = next;
                            i += 1;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    int percentage = (int) (i*100 / diff);
                                    progressBar.setProgress(percentage);
                                    prText.setText(percentage+"%");
                                }
                            });
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(100);
                                prText.setText("100%");
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void restoreUserInstitution() {
        SharedPreferences prefs = getSharedPreferences(Config.PREF_KEY_USER_INSTITUTION, MODE_PRIVATE);
        institution = prefs.getString(Config.PREF_KEY_USER_INSTITUTION, null);
    }

    private void restoreUserConsent() {
        SharedPreferences prefs = getSharedPreferences(Config.PREF_KEY_USER_CONSENT, MODE_PRIVATE);
        consent = prefs.getString(Config.PREF_KEY_USER_CONSENT, "no");
    }

    private void consentDialogWrapper() {
        restoreUserConsent();
        if (consent.equalsIgnoreCase("no")) {
            showConsentDialog();
        }
    }

    private void showConsentDialog() {
        DialogFragment consentDialog = ConsentDialog.newInstance();
        consentDialog.show(getSupportFragmentManager(), "consent");
    }

    private void institutionDialogWrapper() {
        restoreUserInstitution();
        if (institution == null) {
            showInstitutionDialog();
        }
    }

    void showInstitutionDialog() {
        DialogFragment selectUni = InstitutionDialog.newInstance();
        selectUni.show(getSupportFragmentManager(), "institution");
    }

    public void userCancelled() {
        Log.i("Institution", "No institution selected!");
        quitApp();
    }

    private void quitApp() {
        if (isBound) {
            unbindService(serviceConn);
            isBound = false;
        }
        if (this.connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
        }

        this.finish();
        System.exit(0);
    }

    public void consentProvided() {
        consent = "YES";
        if (institution == null)
            institutionDialogWrapper();
    }

    public void institutionSelected(String selection) {
        institution = selection;
        SharedPreferences prefs = getSharedPreferences(Config.PREF_KEY_USER_INSTITUTION, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Config.PREF_KEY_USER_INSTITUTION, selection);
        editor.apply();
        initService();
        requestAppPermissions();
        connectivityReceiver = new NetworkChangeReceiver();
        bindToService();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void requestAppPermissions() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Please grant the following permission");
                builder.setMessage("Read phone state");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{
                                        Manifest.permission.READ_PHONE_STATE,
                                },
                                PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{
                                Manifest.permission.READ_PHONE_STATE,
                        },
                        PERMISSIONS_REQUEST_CODE
                );
            }
        }

        if (!hasPermissionToReadNetworkHistory()) {
            return;
        }
    }

    private void initService() {
        Intent intent = new Intent(this, CollectionService.class);
        this.startService(intent);
    }

    public static MainActivity getCurrentApp() {
        return app;
    }

    private boolean hasPermissionToReadNetworkHistory() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }
        appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
                getPackageName(),
                new AppOpsManager.OnOpChangedListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onOpChanged(String op, String packageName) {
                        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                                android.os.Process.myUid(), getPackageName());
                        if (mode != AppOpsManager.MODE_ALLOWED) {
                            return;
                        }
                        appOps.stopWatchingMode(this);
                    }
                });
        requestReadNetworkHistoryAccess();
        return false;
    }

    private void requestReadNetworkHistoryAccess() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }
}