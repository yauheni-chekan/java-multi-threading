import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Interruption {
    private static final Logger logger = LogManager.getLogger(Interruption.class);

    public static void main(String[] args) {
       // Create a thread that will sleep for a long time
       Thread sleepingThread = new Thread(() -> {
           try {
               logger.info("Thread is going to sleep for 10 seconds...");
               TimeUnit.SECONDS.sleep(10); // Sleep for 10 seconds
               logger.info("Thread woke up naturally!");
           } catch (InterruptedException e) {
               logger.error("Thread was interrupted!", e);
               // Restore the interrupted status
               Thread.currentThread().interrupt();
           }
       });

       // Create a thread that will interrupt the sleeping thread
       Thread interruptingThread = new Thread(() -> {
           try {
               // Wait for 2 seconds before interrupting
               TimeUnit.SECONDS.sleep(2);
               logger.info("Interrupting the sleeping thread...");
               sleepingThread.interrupt();
               logger.info("Interrupting thread finished. Continuing...");
           } catch (InterruptedException e) {
               logger.error("Interrupting thread was interrupted!", e);
           }
       });

       sleepingThread.start();
       interruptingThread.start();
   }
}
