import java.util.concurrent.TimeUnit;

public class Main {

    public static void doSomething(String message) {
        for (int i = 0; i < 10; i++) {
            System.out.println(message);
            try {
                TimeUnit.MILLISECONDS.sleep(10);
                if (i == 5) {
                    throw new InterruptedException("Thread " + Thread.currentThread().getName() + " interrupted");
                }
            } catch (InterruptedException e) {
                System.out.println("Thread " + Thread.currentThread().getName() + " interrupted");
            }
        }
    }

    public static void main(String[] args) {
        // Create a thread that will sleep for a long time
        Thread sleepingThread = new Thread(() -> {
            try {
                System.out.println("Thread is going to sleep for 10 seconds...");
                Thread.sleep(10000); // Sleep for 10 seconds
                System.out.println("Thread woke up naturally!");
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted! Exception message: " + e.getMessage());
                // Restore the interrupted status
                Thread.currentThread().interrupt();
            }
        });

        // Create a thread that will interrupt the sleeping thread
        Thread interruptingThread = new Thread(() -> {
            try {
                // Wait for 2 seconds before interrupting
                Thread.sleep(2000);
                System.out.println("Interrupting the sleeping thread...");
                sleepingThread.interrupt();
            } catch (InterruptedException e) {
                System.out.println("Interrupting thread was interrupted!");
            }
        });

        sleepingThread.start();
        interruptingThread.start();
    }
}
