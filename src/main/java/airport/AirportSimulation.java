package airport;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import airport.config.AirportConfig;
import airport.control.ControlTower;
import airport.model.Plane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirportSimulation {
    private static final Logger logger = LoggerFactory.getLogger(AirportSimulation.class.getName());

    public static void main(String[] args) {
        try {
            // Load configuration
            AirportConfig config = new AirportConfig();
            
            // Get the singleton instance of ControlTower
            ControlTower controlTower = ControlTower.getInstance(config);
            
            // Create thread pool
            ExecutorService executorService = Executors.newCachedThreadPool();
            
            // Create and start planes
            createPlanes(config, controlTower, executorService);
            
            // Shutdown executor and wait for completion
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
            
        } catch (IOException e) {
            logger.error("Error loading configuration: " + e.getMessage());
        } catch (InterruptedException e) {
            logger.error("Simulation interrupted: " + e.getMessage());
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
