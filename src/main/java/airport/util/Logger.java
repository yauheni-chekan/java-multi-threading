package airport.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    
    public static synchronized void log(String message) {
        String timestamp = LocalTime.now().format(formatter);
        System.out.printf("[%s] %s%n", timestamp, message);
    }
}
