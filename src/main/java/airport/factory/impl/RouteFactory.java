package airport.factory.impl;

import airport.model.Route;
import airport.factory.Factory;
import airport.model.Airport;

import java.util.*;
import java.util.function.Function;

public class RouteFactory implements Factory<Route> {
    public static Route create(String routeId) {
        return new Route(routeId);
    }
    /**
     * Loads routes from a CSV file with columns: routeId,sourceId,destId,distance
     * Returns a map of routeId to Route.
     * Implements Factory<Route> interface, but requires airportResolver for correct airport lookup.
     */
    public Map<String, Route> loadFromCSV(String csvPath, Function<String, Airport> airportResolver) throws Exception {
        Map<String, Route> routes = new HashMap<>();
        try (java.io.BufferedReader reader = java.nio.file.Files.newBufferedReader(java.nio.file.Paths.get(csvPath))) {
            reader.lines()
                .skip(1) // Skip header
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .forEach(line -> {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 4) {
                        String routeId = parts[0].trim();
                        String sourceId = parts[1].trim();
                        String destId = parts[2].trim();
                        double distance = Double.parseDouble(parts[3].trim());
                        Route route = routes.computeIfAbsent(routeId, Route::new);
                        Airport source = airportResolver.apply(sourceId);
                        Airport dest = airportResolver.apply(destId);
                        if (source != null && dest != null) {
                            route.addEdge(source, dest, distance);
                        }
                    }
                });
        }
        return routes;
    }
    // Satisfy the Factory interface, but throw if called without airportResolver
    @Override
    public List<Route> loadFromCSV(String csvPath) throws Exception {
        throw new UnsupportedOperationException("Use loadFromCSV(String, Function<String, Airport>) for RouteFactory");
    }
} 