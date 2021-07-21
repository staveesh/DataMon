package za.ac.uct.cs.videodatausageapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NetworkSummaryCollector {
    private static final String TAG = "NetworkSummaryCollector";
    Context context;
    String institution;

    public NetworkSummaryCollector(Context context) {
        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences(Config.PREF_KEY_USER_INSTITUTION, Context.MODE_PRIVATE);
        institution = prefs.getString(Config.PREF_KEY_USER_INSTITUTION, null);
    }

    public String collectSummary(String deviceId, long startTime, long endTime) {
        List<Package> packageList = getPackagesData();
        JSONArray wifiSummary = new JSONArray();
        JSONArray mobileSummary = new JSONArray();
        try {
            for (Package pckg : packageList) {
                if (Config.APPS_LIST.contains(pckg.getPackageName())) {
                    Log.d(TAG, "collectSummary: " + pckg.getName());
                    String packageName = pckg.getPackageName();
                    DataPayload wifiPayload = getWifiBytes(packageName, startTime, endTime);
                    SubscriptionManager subscriptionManager = null;
                    subscriptionManager = SubscriptionManager.from(context);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                TelephonyManager manager1 = manager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                                String operatorName = manager1.getNetworkOperatorName();
                                String subscriberId = getSubscriberId(manager1);
                                DataPayload mobileSimPayload = getMobileBytes(packageName, startTime, endTime, subscriberId);
                                if (!mobileSimPayload.isEmptyPayload()) {
                                    JSONObject appData = new JSONObject();
                                    appData.put("operator", operatorName);
                                    appData.put("app", packageName);
                                    appData.put("rx", mobileSimPayload.getRx());
                                    appData.put("tx", mobileSimPayload.getTx());
                                    mobileSummary.put(appData);
                                }
                            }
                        }
                    }
                    if (!wifiPayload.isEmptyPayload()) {
                        JSONObject appData = new JSONObject();
                        appData.put("operator", "WIFI");
                        appData.put("app", packageName);
                        appData.put("rx", wifiPayload.getRx());
                        appData.put("tx", wifiPayload.getTx());
                        wifiSummary.put(appData);
                    }
                }
            }
            JSONObject blob = new JSONObject();
            blob.put("institution", institution);
            blob.put("deviceId", deviceId);
            blob.put("startTime", startTime);
            blob.put("endTime", endTime);
            blob.put("wifiSummary", wifiSummary);
            blob.put("mobileSummary", mobileSummary);
            return blob.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("HardwareIds")
    private String getSubscriberId(TelephonyManager telephonyManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return telephonyManager.getSubscriberId();
        } else {
            return null;
        }
    }

    private List<Package> getPackagesData() {
        PackageManager packageManager = MainActivity.getCurrentApp().getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        List<Package> packageList = new ArrayList<>(packageInfoList.size());
        for (PackageInfo packageInfo : packageInfoList) {
            if (packageManager.checkPermission(Manifest.permission.INTERNET,
                    packageInfo.packageName) == PackageManager.PERMISSION_DENIED) {
                continue;
            }
            Package packageItem = new Package();
            packageItem.setVersion(packageInfo.versionName);
            packageItem.setPackageName(packageInfo.packageName);
            packageList.add(packageItem);
            ApplicationInfo ai = null;
            try {
                ai = packageManager.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (ai == null) {
                continue;
            }
            CharSequence appName = packageManager.getApplicationLabel(ai);
            if (appName != null) {
                packageItem.setName(appName.toString());
            }
        }
        return packageList;
    }

    private DataPayload getWifiBytes(String packageName, long start, long end) {
        int uid = PackageManagerHelper.getPackageUid(context, packageName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
            NetworkStatsHelper networkStatsHelper = new NetworkStatsHelper(networkStatsManager, uid);
            return fillNetworkStatsPackageWifi(networkStatsHelper, start, end);
        }
        return null;
    }

    private DataPayload getMobileBytes(String packageName, long start, long end, String subscriberId) {
        int uid = PackageManagerHelper.getPackageUid(context, packageName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
            NetworkStatsHelper networkStatsHelper = new NetworkStatsHelper(networkStatsManager, uid);
            return fillNetworkStatsPackageMobile(networkStatsHelper, start, end, subscriberId);
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private DataPayload fillNetworkStatsPackageWifi(NetworkStatsHelper networkStatsHelper, long start, long end) {
        long mobileWifiRx = networkStatsHelper.getPackageRxBytesWifi(start, end);
        long mobileWifiTx = networkStatsHelper.getPackageTxBytesWifi(start, end);
        return new DataPayload(mobileWifiRx, mobileWifiTx);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private DataPayload fillNetworkStatsPackageMobile(NetworkStatsHelper networkStatsHelper, long start, long end, String subscriberId) {
        long mobileWifiRx = networkStatsHelper.getPackageRxBytesMobile(start, end, subscriberId);
        long mobileWifiTx = networkStatsHelper.getPackageTxBytesMobile(start, end, subscriberId);
        return new DataPayload(mobileWifiRx, mobileWifiTx);
    }
}