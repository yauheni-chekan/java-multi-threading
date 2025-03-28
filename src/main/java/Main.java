import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import prioriry.TalkThread;
import prioriry.WalkThread;

public class Main {
    // Create a logger for this class using Log4j2
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        // Create threads with different priorities
        Thread walkMin = new Thread(new WalkThread(), "WalkMin");
        Thread talkMax = new Thread(new TalkThread(), "TalkMax");
        
        // Set priorities
        walkMin.setPriority(Thread.MIN_PRIORITY);
        talkMax.setPriority(Thread.MAX_PRIORITY);
        
        logger.info("Starting threads with different priorities:");
        logger.info("{} thread priority: {}", walkMin.getName(), walkMin.getPriority());
        logger.info("{} thread priority: {}", talkMax.getName(), talkMax.getPriority());
        
        // Start threads
        walkMin.start();
        talkMax.start();
        
        // Wait for both threads to complete
        try {
            walkMin.join();
            talkMax.join();
        } catch (InterruptedException e) {
            logger.error("Main thread was interrupted", e);
            Thread.currentThread().interrupt();
        }
        
        logger.info("All threads completed");
    }
}
