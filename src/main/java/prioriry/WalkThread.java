package prioriry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WalkThread implements Runnable {
    private static final Logger logger = LogManager.getLogger(WalkThread.class);
    private static final int ITERATIONS = 100_000_000;

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        double result = 0;
        
        // CPU-intensive operation
        for (int i = 0; i < ITERATIONS; i++) {
            result += Math.sin(i) * Math.cos(i);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        logger.info("{} thread completed {} iterations in {} ms", 
            Thread.currentThread().getName(), ITERATIONS, duration);
    }
} 