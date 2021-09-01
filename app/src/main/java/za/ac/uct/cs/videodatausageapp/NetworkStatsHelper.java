package za.ac.uct.cs.videodatausageapp;

import android.annotation.TargetApi;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


@TargetApi(Build.VERSION_CODES.M)
public class NetworkStatsHelper {

    private static final String TAG = "NetworkStatsHelper";

    NetworkStatsManager networkStatsManager;
    int packageUid;

    public NetworkStatsHelper(NetworkStatsManager networkStatsManager) {
        this.networkStatsManager = networkStatsManager;
    }

    public NetworkStatsHelper(NetworkStatsManager networkStatsManager, int packageUid) {
        this.networkStatsManager = networkStatsManager;
        this.packageUid = packageUid;
    }

    public void setUid(int uid) {
        packageUid = uid;
    }

    public List<UsageBucket> getPackageRxBytesWifi(long startTime, long endTime) {
        List<UsageBucket> buckets = new ArrayList<>();
        NetworkStats startStats = null;
        startStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_WIFI,
                "",
                0,
                endTime,
                packageUid);
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (startStats.hasNextBucket()) {
            startStats.getNextBucket(bucket);
            if (bucket.getStartTimeStamp() <= endTime && startTime <= bucket.getEndTimeStamp()) {
                UsageBucket usageBucket = new UsageBucket(bucket.getStartTimeStamp(),
                        bucket.getEndTimeStamp(), (float) bucket.getRxBytes() / (1024 * 1024));
                buckets.add(usageBucket);
            }
        }
        startStats.close();

        return buckets;
    }

    public List<UsageBucket> getPackageTxBytesWifi(long startTime, long endTime) {
        List<UsageBucket> buckets = new ArrayList<>();
        NetworkStats startStats = null;
        startStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_WIFI,
                "",
                0,
                endTime,
                packageUid);
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (startStats.hasNextBucket()) {
            startStats.getNextBucket(bucket);
            if (bucket.getStartTimeStamp() <= endTime && startTime <= bucket.getEndTimeStamp()) {
                UsageBucket usageBucket = new UsageBucket(bucket.getStartTimeStamp(),
                        bucket.getEndTimeStamp(), (float) bucket.getTxBytes() / (1024 * 1024));
                buckets.add(usageBucket);
            }
        }
        startStats.close();

        return buckets;
    }

    public List<UsageBucket> getPackageRxBytesMobile(long startTime, long endTime, String subscriberId) {
        List<UsageBucket> buckets = new ArrayList<>();
        NetworkStats startStats = null;
        startStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,
                subscriberId,
                0,
                endTime,
                packageUid
        ) ;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (startStats.hasNextBucket()) {
            startStats.getNextBucket(bucket);
            if (bucket.getStartTimeStamp() <= endTime && startTime <= bucket.getEndTimeStamp()) {
                UsageBucket usageBucket = new UsageBucket(bucket.getStartTimeStamp(),
                        bucket.getEndTimeStamp(), (float) bucket.getRxBytes() / (1024 * 1024));
                buckets.add(usageBucket);
            }
        }
        startStats.close();
        return buckets;
    }

    public List<UsageBucket> getPackageTxBytesMobile(long startTime, long endTime, String subscriberId) {
        List<UsageBucket> buckets = new ArrayList<>();
        NetworkStats startStats = null;
        startStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,
                subscriberId,
                0,
                endTime,
                packageUid
        ) ;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (startStats.hasNextBucket()) {
            startStats.getNextBucket(bucket);
            if (bucket.getStartTimeStamp() <= endTime && startTime <= bucket.getEndTimeStamp()) {
                UsageBucket usageBucket = new UsageBucket(bucket.getStartTimeStamp(),
                        bucket.getEndTimeStamp(), (float) bucket.getTxBytes() / (1024 * 1024));
                buckets.add(usageBucket);
            }
        }
        startStats.close();
        return buckets;
    }

}