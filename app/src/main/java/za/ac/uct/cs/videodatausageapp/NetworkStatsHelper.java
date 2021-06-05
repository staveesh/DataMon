package za.ac.uct.cs.videodatausageapp;

import android.annotation.TargetApi;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Log;


@TargetApi(Build.VERSION_CODES.M)
public class NetworkStatsHelper {

    NetworkStatsManager networkStatsManager;
    int packageUid;

    public NetworkStatsHelper(NetworkStatsManager networkStatsManager) {
        this.networkStatsManager = networkStatsManager;
    }

    public NetworkStatsHelper(NetworkStatsManager networkStatsManager, int packageUid) {
        this.networkStatsManager = networkStatsManager;
        this.packageUid = packageUid;
    }

    public void setUid(int uid){
        packageUid=uid;
    }

    public long getPackageRxBytesWifi(long startTime,long endTime) {
        NetworkStats startStats, endStats = null;
        startStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_WIFI,
                "",
                0,
                startTime,
                packageUid);

        long startBytes = 0L;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (startStats.hasNextBucket()) {
            startStats.getNextBucket(bucket);
            startBytes += bucket.getRxBytes();
        }
        startStats.close();

        endStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_WIFI,
                "",
                0,
                endTime,
                packageUid);

        long endBytes = 0L;
        bucket = new NetworkStats.Bucket();
        while (endStats.hasNextBucket()) {
            endStats.getNextBucket(bucket);
            endBytes += bucket.getRxBytes();
        }
        endStats.close();
        return endBytes-startBytes;
    }

    public long getPackageTxBytesWifi(long startTime,long endTime) {
        NetworkStats startStats, endStats = null;
        startStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_WIFI,
                "",
                0,
                startTime,
                packageUid);

        long startBytes = 0L;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (startStats.hasNextBucket()) {
            startStats.getNextBucket(bucket);
            startBytes += bucket.getTxBytes();
        }
        startStats.close();
        endStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_WIFI,
                "",
                0,
                endTime,
                packageUid);

        long endBytes = 0L;
        bucket = new NetworkStats.Bucket();
        while (endStats.hasNextBucket()) {
            endStats.getNextBucket(bucket);
            endBytes += bucket.getTxBytes();
        }
        endStats.close();
        return endBytes-startBytes;
    }

    public long getPackageRxBytesMobile(long startTime, long endTime, String subscriberId){
        NetworkStats startStats = null, endStats = null;
        startStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,
                subscriberId,
                0,
                startTime,
                packageUid
        );
        long startBytes = 0L, endBytes = 0L;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (startStats.hasNextBucket()) {
            startStats.getNextBucket(bucket);
            startBytes += bucket.getRxBytes();
        }
        startStats.close();
        endStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,
                subscriberId,
                0,
                endTime,
                packageUid
        );
        bucket = new NetworkStats.Bucket();
        while (endStats.hasNextBucket()) {
            endStats.getNextBucket(bucket);
            endBytes += bucket.getRxBytes();
        }
        endStats.close();
        return endBytes-startBytes;
    }

    public long getPackageTxBytesMobile(long startTime, long endTime, String subscriberId){
        NetworkStats startStats = null, endStats = null;
        startStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,
                subscriberId,
                0,
                startTime,
                packageUid
        );
        long startBytes = 0L, endBytes = 0L;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (startStats.hasNextBucket()) {
            startStats.getNextBucket(bucket);
            startBytes += bucket.getTxBytes();
        }
        startStats.close();
        endStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,
                subscriberId,
                0,
                endTime,
                packageUid
        );
        bucket = new NetworkStats.Bucket();
        while (endStats.hasNextBucket()) {
            endStats.getNextBucket(bucket);
            endBytes += bucket.getTxBytes();
        }
        endStats.close();
        return endBytes-startBytes;
    }

}