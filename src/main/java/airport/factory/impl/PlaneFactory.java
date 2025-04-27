package airport.factory.impl;

import airport.factory.Factory;
import airport.model.Plane;
import airport.model.Route;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaneFactory implements Factory<Plane> {
    private static final Logger logger = LoggerFactory.getLogger(PlaneFactory.class);
    public static Plane create(String id, Plane.Size size) {
        return new Plane(id, size);
    }
    public List<Plane> loadFromCSV(String csvPath, Map<String, Route> routeMap) throws Exception {
        List<Plane> planes = new ArrayList<>();
        try (java.io.BufferedReader reader = java.nio.file.Files.newBufferedReader(java.nio.file.Paths.get(csvPath))) {
            reader.lines()
                .skip(1) // Skip header
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .forEach(line -> {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 3) {
                        String id = parts[0].trim();
                        String sizeStr = parts[1].trim().toUpperCase();
                        String routeId = parts[2].trim();
                        try {
                            Plane.Size size = Plane.Size.valueOf(sizeStr);
                            Plane plane = new Plane(id, size);
                            Route route = routeMap.get(routeId);
                            if (route != null) {
                                plane.assignRoute(route);
                            } else {
                                logger.warn("No route found for routeId {} for plane {}", routeId, id);
                            }
                            planes.add(plane);
                        } catch (Exception e) {
                            logger.error("Invalid plane or route assignment: {}", line);
                        }
                    }
                });
        }
        return planes;
    }
    @Override
    public List<Plane> loadFromCSV(String csvPath) throws Exception {
        throw new UnsupportedOperationException("Use loadFromCSV(String, Map<String, Route>) for route assignment");
    }
} 