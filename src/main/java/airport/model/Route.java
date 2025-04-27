package airport.model;

import java.util.*;

import airport.factory.impl.RouteFactory;

public class Route {
    private final String routeId;
    // Adjacency map: Airport -> (Neighbor Airport -> Distance)
    private final Map<Airport, Map<Airport, Double>> adjacencyMap;

    // Public static factory for Route objects
    public static final RouteFactory FACTORY = new RouteFactory();

    public Route(String routeId) {
        this.routeId = Objects.requireNonNull(routeId, "routeId must not be null");
        this.adjacencyMap = new HashMap<>();
    }

    public String getRouteId() {
        return routeId;
    }

    /**
     * Adds a directed edge from source to destination with the given distance.
     * Cannot add edge if destination is already in the adjacency map.
     */
    public void addEdge(Airport source, Airport destination, double distance) {
        if (adjacencyMap.containsKey(destination)) {
            throw new IllegalArgumentException("Destination airport already exists in the route.");
        }
        adjacencyMap.computeIfAbsent(source, k -> new HashMap<>()).put(destination, distance);
    }

    /**
     * Returns a set of all airports (nodes) in the graph.
     */
    public List<Airport> getAirports() {
        return Collections.unmodifiableList(new ArrayList<>(adjacencyMap.keySet()));
    }

    /**
     * Returns a map of neighbors and distances for a given airport.
     */
    public Map<Airport, Double> getNeighbors(Airport airport) {
        return adjacencyMap.getOrDefault(airport, Collections.emptyMap());
    }

    /**
     * Gets the distance between two airports if a direct edge exists, else null.
     * Note: the route has to be a single path. No loops allowed.
     */
    public Double getDistance(Airport from) {
        if (from == null) {
            throw new IllegalArgumentException("Airport cannot be null");
        }
        Map<Airport, Double> neighbors = adjacencyMap.get(from);
        if (neighbors == null) {
            return 0.0;
        }
        return neighbors.values().stream().reduce(0.0, Double::sum);
    }

    @Override
    public String toString() {
        return String.format("Route{routeId='%s', adjacencyMap=%s}", routeId, adjacencyMap);
    }
}