package airport;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import airport.model.Airport;
import airport.model.Plane;
import airport.model.Route;
import airport.factory.impl.AirportFactory;
import airport.factory.impl.PlaneFactory;
import airport.factory.impl.RouteFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirportSimulation {
    private static final Logger logger = LoggerFactory.getLogger(AirportSimulation.class);

    public static void main(String[] args) {
        try {
            // Load airports from CSV using path from properties file
            java.util.Properties props = new java.util.Properties();
            try (java.io.InputStream in = AirportSimulation.class.getClassLoader()
                    .getResourceAsStream("airport_sim.properties")) {
                if (in == null) {
                    throw new IOException("Could not find airport_sim.properties in resources.");
                }
                props.load(in);
            }
            String airportsCsvPath = props.getProperty("files.airports");
            String routesCsvPath = props.getProperty("files.routes");
            String planesCsvPath = props.getProperty("files.planes");
            AirportFactory airportFactory = new AirportFactory();
            List<Airport> airports = airportFactory.loadFromCSV(airportsCsvPath);
            logger.info("Loaded {} airports: {}", airports.size(), airports.stream().map(Airport::getName).collect(Collectors.joining(", ")));

            // Create routes
            RouteFactory routeFactory = new RouteFactory();
            Map<String, Route> routes = routeFactory.loadFromCSV(routesCsvPath, airportFactory::getInstance);
            logger.info("Loaded {} routes: {}", routes.size(), routes.keySet().stream().map(String::valueOf).collect(Collectors.joining(", ")));

            // Create thread pool
            ExecutorService executorService = Executors.newCachedThreadPool();
            PlaneFactory planeFactory = new PlaneFactory();
            List<Plane> planes = planeFactory.loadFromCSV(planesCsvPath, routes);
            logger.info("Loaded {} planes: {}", planes.size(), planes.stream().map(Plane::getId).collect(Collectors.joining(", ")));

            // Create and start planes
            for (Plane plane : planes) {
                executorService.submit(plane);
            }
            
            // Shutdown executor and wait for completion
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
            
        } catch (Exception e) {
            logger.error("Simulation error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
