package airport;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import airport.config.AirportConfig;
import airport.model.Airport;
import airport.model.Plane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirportSimulation {
    private static final Logger logger = LoggerFactory.getLogger(AirportSimulation.class);

    public static void main(String[] args) {
        try {
            // Load configuration
            AirportConfig config = new AirportConfig();
            
            // Create airports
            Airport jfk = new Airport("JFK", config);
            Airport lax = new Airport("LAX", config);
            
            // Create thread pool
            ExecutorService executorService = Executors.newCachedThreadPool();
            
            // Create and start planes
            createPlanes(config, jfk, lax, executorService);
            
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

    private static void createPlanes(AirportConfig config, 
                                   Airport origin, Airport destination,
                                   ExecutorService executorService) {
        // Create small planes
        for (int i = 0; i < config.getSmallPlanes(); i++) {
            Plane plane = new Plane("S" + i, Plane.Size.SMALL);
            plane.setDestination(origin);
            executorService.submit(plane);
        }
        
        // Create medium planes
        for (int i = 0; i < config.getMediumPlanes(); i++) {
            Plane plane = new Plane("M" + i, Plane.Size.MEDIUM);
            plane.setDestination(origin);
            executorService.submit(plane);
        }
        
        // Create large planes
        for (int i = 0; i < config.getLargePlanes(); i++) {
            Plane plane = new Plane("L" + i, Plane.Size.LARGE);
            plane.setDestination(origin);
            executorService.submit(plane);
        }
    }
}
