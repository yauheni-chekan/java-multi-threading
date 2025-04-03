package airport.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AirportConfig {
    private final int parkingSpots;
    private final int groundServiceTeams;
    private final int smallPlanes;
    private final int mediumPlanes;
    private final int largePlanes;

    public AirportConfig() throws IOException {
        Properties props = new Properties();
        
        // Load from classpath resources
        try (InputStream input = AirportConfig.class.getClassLoader()
                .getResourceAsStream("airport.properties")) {
            if (input == null) {
                throw new IOException("Unable to find airport.properties in classpath");
            }
            props.load(input);
        }
        
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
}
