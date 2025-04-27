package airport.config;

import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AirportConfig {
    private static final Logger logger = LoggerFactory.getLogger(AirportConfig.class);
    private final int parkingSpots;
    private final int groundServiceTeams;
    private final int smallPlanes;
    private final int mediumPlanes;
    private final int largePlanes;

    public AirportConfig(Properties props) {
        this.parkingSpots = Integer.parseInt(props.getProperty("parking.spots", "3"));
        this.groundServiceTeams = Integer.parseInt(props.getProperty("ground.service.teams", "1"));
        this.smallPlanes = Integer.parseInt(props.getProperty("planes.small", "2"));
        this.mediumPlanes = Integer.parseInt(props.getProperty("planes.medium", "2"));
        this.largePlanes = Integer.parseInt(props.getProperty("planes.large", "1"));
    }

    public int getParkingSpots() {
        return parkingSpots;
    }

    public int getGroundServiceTeams() {
        return groundServiceTeams;
    }
    
    public int getSmallPlanes() {
        return smallPlanes;
    }

    public int getMediumPlanes() {
        return mediumPlanes;
    }

    public int getLargePlanes() {
        return largePlanes;
    }

    /**
     * Optionally load AirportConfig from a CSV line (for future extensibility).
     */
    public static AirportConfig fromCSV(String csvLine) throws IOException {
        // Example: id,name,parkingSpots,groundServiceTeams,smallPlanes,mediumPlanes,largePlanes
        String[] parts = csvLine.split(",");
        Properties props = new Properties();
        if (parts.length >= 7) {
            props.setProperty("parking.spots", parts[2]);
            props.setProperty("ground.service.teams", parts[3]);
            props.setProperty("planes.small", parts[4]);
            props.setProperty("planes.medium", parts[5]);
            props.setProperty("planes.large", parts[6]);
        }
        AirportConfig config = new AirportConfig(props);
        logger.info("Airport config: {}", config);
        return config;
    }
}
