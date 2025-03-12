import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParametrizedThread {
    private final String[] messages;
    private final String threadName;
    private static final Logger logger = LogManager.getLogger(ParametrizedThread.class);

    public ParametrizedThread(String threadName, String[] messages) {
        this.threadName = threadName;
        this.messages = messages;
    }

    public void start() {
        Thread thread = new Thread(() -> {
            logger.info("Thread " + threadName + " started");
            for (String message : messages) {
                logger.info("Thread " + threadName + ": " + message);
                try {
                    TimeUnit.MILLISECONDS.sleep(100); // Small delay between messages
                } catch (InterruptedException e) {
                    logger.error("Thread " + threadName + " was interrupted: " + e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            logger.info("Thread " + threadName + " finished");
        });
        thread.start();
    }

    public static void main(String[] args) {
        // Create and start N threads (between 4 and 6)
        int N = 5; // You can change this value between 4 and 6
        
        for (int i = 1; i <= N; i++) {
            // Create different message sequences for each thread
            String[] messages = new String[3];
            for (int j = 0; j < messages.length; j++) {
                messages[j] = "Message " + (j + 1) + " from sequence " + i;
            }
            
            // Create and start the thread
            ParametrizedThread paramThread = new ParametrizedThread("Thread-" + i, messages);
            paramThread.start();
        }
    }
}
