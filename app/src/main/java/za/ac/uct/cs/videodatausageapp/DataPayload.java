package za.ac.uct.cs.videodatausageapp;

import java.util.List;

public class DataPayload {
    private List<UsageBucket> rxBuckets;
    private List<UsageBucket> txBuckets;

    public DataPayload(List<UsageBucket> rxBuckets, List<UsageBucket> txBuckets) {
        this.rxBuckets = rxBuckets;
        this.txBuckets = txBuckets;
    }

    public boolean isEmptyPayload(){
        return (rxBuckets.size() == 0 && txBuckets.size() == 0);
    }

    public List<UsageBucket> getRxBuckets() {
        return rxBuckets;
    }

    public List<UsageBucket> getTxBuckets() {
        return txBuckets;
    }

}
