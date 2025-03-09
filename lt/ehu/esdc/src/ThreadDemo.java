
// Using Thread class
class MyThread extends Thread {
    public void run() {
        System.out.println("Thread running using Thread class");
    }
}

// Using Runnable interface
class MyRunnable implements Runnable {
    public void run() {
        System.out.println("Thread running using Runnable interface");
    }
}

// Usage
public class ThreadDemo {
    public static void main(String[] args) {
        // Using Thread class
        MyThread thread1 = new MyThread();
        thread1.start();

        // Using Runnable interface
        Thread thread2 = new Thread(new MyRunnable());
        thread2.start();

        // Using Lambda (modern approach)
        Thread thread3 = new Thread(() -> {
            System.out.println("Thread running using Lambda");
        });
        thread3.start();
    }
}
