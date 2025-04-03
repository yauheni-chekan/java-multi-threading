package airport;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import airport.config.AirportConfig;
import airport.control.ControlTower;
import airport.model.Plane;

public class AirportSimulation {
    public static void main(String[] args) {
        try {
            // Load configuration
            AirportConfig config = new AirportConfig();
            ControlTower controlTower = new ControlTower(config);
            
            // Create thread pool
            ExecutorService executorService = Executors.newCachedThreadPool();
            
            // Create and start planes
            createPlanes(config, controlTower, executorService);
            
            // Shutdown executor and wait for completion
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
            
        } catch (IOException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Simulation interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private static void createPlanes(AirportConfig config, ControlTower controlTower, 
                                   ExecutorService executorService) {
        // Create small planes
        for (int i = 0; i < config.getSmallPlanes(); i++) {
            executorService.submit(new Plane("S" + i, Plane.Size.SMALL, controlTower, true));
        }
        
        // Create medium planes
        for (int i = 0; i < config.getMediumPlanes(); i++) {
            executorService.submit(new Plane("M" + i, Plane.Size.MEDIUM, controlTower, true));
        }
        
        // Create large planes
        for (int i = 0; i < config.getLargePlanes(); i++) {
            executorService.submit(new Plane("L" + i, Plane.Size.LARGE, controlTower, true));
        }
    }
}
