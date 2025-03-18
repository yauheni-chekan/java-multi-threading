package producer_consumer;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Consumer implements Runnable {
    private static final Logger logger = LogManager.getLogger(Consumer.class);

    private final SharedState sharedState;
    private final long countdownStart; // K milliseconds
    private final long timeLogDelay; // M/10 milliseconds
    public Consumer(SharedState sharedState, long countdownStart, long timeLogDelay) {
        this.sharedState = sharedState;
        this.countdownStart = countdownStart;
        this.timeLogDelay = timeLogDelay;
    }

    @Override
    public void run() {
        try {
            long currentCount = countdownStart;
            
            while (currentCount > 0 && sharedState.isRunning()) {
                sharedState.waitForTrue();
                // Wait until the state becomes true
                if (!sharedState.isRunning()) break;

                // Output the current countdown value
                if (currentCount % timeLogDelay == 0) {
                    logger.info("Countdown: {}", currentCount);
                }
                currentCount--;
                logger.info("Consumer: currentCount={}", currentCount);
                TimeUnit.MILLISECONDS.sleep(1);
            }
            
            // Signal the end of work
            sharedState.setRunning(false);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
