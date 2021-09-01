package za.ac.uct.cs.videodatausageapp;

public class UsageBucket {
    private long start;
    private long end;
    private float megabytes;

    public UsageBucket(long start, long end, float megabytes) {
        this.start = start;
        this.end = end;
        this.megabytes = megabytes;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public float getMegabytes() {
        return megabytes;
    }

    public void setMegabytes(float megabytes) {
        this.megabytes = megabytes;
    }
}
