package producer_consumer;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Producer implements Runnable {
    private final Logger logger = LogManager.getLogger(Producer.class);
    private final SharedState sharedState;
    private final long switchDelay; // M milliseconds
    public Producer(SharedState sharedState, long switchDelay) {
        this.sharedState = sharedState;
        this.switchDelay = switchDelay;
    }

    @Override
    public void run() {
        try {
            while (sharedState.isRunning()) {
                logger.info("Producer: switch={}", !sharedState.getState());
                sharedState.setState(!sharedState.getState());
                TimeUnit.MILLISECONDS.sleep(switchDelay);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
