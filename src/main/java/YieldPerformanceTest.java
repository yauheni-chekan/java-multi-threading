import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YieldPerformanceTest {
    private static final Logger logger = LogManager.getLogger(YieldPerformanceTest.class);

    private static final int ITERATIONS = 6;
    private static final int COMPUTE_ITERATIONS = 100_000_000;

    static class ComputeThread implements Runnable {
        private final boolean useYield;
        
        ComputeThread(boolean useYield) {
            this.useYield = useYield;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            double result = 0;
            
            for (int i = 0; i < COMPUTE_ITERATIONS; i++) {
                result += Math.sin(i) * Math.cos(i);
                if (useYield && i % 1000 == 0) {
                    Thread.yield();
                }
            }
            
            long duration = System.currentTimeMillis() - start;
            logger.info("Thread {} completed in {}ms", 
                Thread.currentThread().getName(), duration);
        }
    }

    public static void main(String[] args) {
        Thread[] threads = new Thread[ITERATIONS];
        
        for (int i = 0; i < ITERATIONS; i++) {
            boolean useYield = i % 2 == 0;
            threads[i] = new Thread(new ComputeThread(useYield));
            threads[i].setName("Thread-" + i + "-" + (useYield ? "Y" : "N"));
            threads[i].start();
        }

        for (int i = 0; i < ITERATIONS; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                logger.error("Thread was interrupted!", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
