package producer_consumer;

public class SharedState {
    private volatile boolean state = false;
    private volatile boolean isRunning = true;
    private final Object lock = new Object();
    
    public boolean getState() {
        return state;
    }

    public void setState(boolean newState) {
        synchronized (lock) {
            state = newState;
            lock.notifyAll();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        synchronized (lock) {
            isRunning = running;
            if (!isRunning) {
                lock.notifyAll();
            }
        }
    }

    public void waitForTrue() throws InterruptedException {
        synchronized (lock) {
            while (!state && isRunning) {
                lock.wait();
            }
        }
    }
}
