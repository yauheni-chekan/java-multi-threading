package producer_consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunDemo {
    private static final Logger logger = LogManager.getLogger(RunDemo.class);
    public static void main(String[] args) {

        long K = 5000; // 
        long M = 1000; // 
        
        SharedState sharedState = new SharedState();
        
        Thread producerThread = new Thread(new Producer(sharedState, M));
        Thread consumerThread = new Thread(new Consumer(sharedState, K, M/10));
        
        producerThread.start();
        consumerThread.start();
        
        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("Program completed");
    }
}
