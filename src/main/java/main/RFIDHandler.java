package main;

public class RFIDHandler implements Runnable {

    private volatile boolean cancelled;
    @Override
    public void run() {

    }

    public void cancel()
    {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
