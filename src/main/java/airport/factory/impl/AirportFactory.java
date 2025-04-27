package airport.factory.impl;

import airport.model.Airport;
import airport.config.AirportConfig;
import airport.factory.Factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class AirportFactory implements Factory<Airport> {
    private static final Logger logger = LoggerFactory.getLogger(AirportFactory.class);
    private final Map<String, Airport> airportMap = new HashMap<>();

    public static Airport create(String id, String name, AirportConfig config) throws Exception {
        return new Airport(id, name, config);
    }

    @Override
    public List<Airport> loadFromCSV(String csvPath) throws Exception {
        List<Airport> airports = new ArrayList<>();
        try (java.io.BufferedReader reader = java.nio.file.Files.newBufferedReader(java.nio.file.Paths.get(csvPath))) {
            reader.lines()
                .skip(1) // Skip header
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .forEach(line -> {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 2) {
                        String id = parts[0].trim();
                        String name = parts[1].trim();
                        try {
                            AirportConfig config = AirportConfig.fromCSV(line);
                            Airport airport = new Airport(id, name, config);
                            airports.add(airport);
                            airportMap.put(id, airport);
                        } catch (Exception e) {
                            logger.error("Failed to create airport {}: {}", id, e.getMessage());
                        }
                    }
                });
        }
        return airports;
    }

    public Airport getInstance(String id) {
        return airportMap.get(id);
    }
} 