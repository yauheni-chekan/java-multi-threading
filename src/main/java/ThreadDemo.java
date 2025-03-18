
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.TimeUnit;

// Using Thread class
class MyThread extends Thread {
    private static final Logger logger = LogManager.getLogger(MyThread.class);
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                logger.info("Thread running using Thread class");
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            logger.error("Thread was interrupted!", e);
        }
    }
}

// Using Runnable interface
class MyRunnable implements Runnable {
    private static final Logger logger = LogManager.getLogger(MyRunnable.class);
    public void run() { 
        try {
            for (int i = 0; i < 10; i++) {
                logger.info("Thread running using Runnable interface");
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            logger.error("Thread was interrupted!", e);
        }
    }
}

// Usage
public class ThreadDemo {
    private static final Logger logger = LogManager.getLogger(ThreadDemo.class); 

    public static void main(String[] args) {
        // Using Thread class
        MyThread thread1 = new MyThread();

        // Using Runnable interface
        Thread thread2 = new Thread(new MyRunnable());

        // Using Lambda (modern approach)
        Thread thread3 = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    logger.info("Thread-3 running using Lambda");
                    TimeUnit.MILLISECONDS.sleep(50);
                }
            } catch (InterruptedException e) {
                logger.error("Thread was interrupted!", e);
            }
        });

        thread1.start();
        thread2.start();
        // Wait for threads to finish
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            logger.error("Thread was interrupted!", e);
        }
        thread3.start();
    }
}
