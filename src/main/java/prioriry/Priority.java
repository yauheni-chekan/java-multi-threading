package prioriry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Priority {
    private static final Logger logger = LogManager.getLogger(Priority.class);

    public static void main(String[] args) {
        Thread walkMin = new Thread(new WalkThread(), "WalkMin");
        Thread talkMax = new Thread(new TalkThread(), "TalkMax");
        
        walkMin.setPriority(Thread.MIN_PRIORITY);
        talkMax.setPriority(Thread.MAX_PRIORITY);
        
        logger.info("Starting threads with different priorities:");
        logger.info("{} thread priority: {}", walkMin.getName(), walkMin.getPriority());
        logger.info("{} thread priority: {}", talkMax.getName(), talkMax.getPriority());
        
        walkMin.start();
        talkMax.start();
        
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
