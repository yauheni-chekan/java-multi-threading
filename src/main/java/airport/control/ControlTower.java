package airport.control;
import java.util.concurrent.Semaphore;
import airport.config.AirportConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ControlTower {
    private static final Logger logger = LoggerFactory.getLogger(ControlTower.class.getName());
    // Singleton instance
    private static ControlTower instance;
    // Lock for thread-safe initialization
    private static final Semaphore instanceLock = new Semaphore(1, true);
    
    private final Semaphore runway;
    private final Semaphore[] parkingSpots;
    private final Semaphore groundService;
    private final Semaphore parkingSpotLock;
    private final boolean[] parkingSpotAvailable;
    
    // Private constructor to prevent direct instantiation
    private ControlTower(AirportConfig config) {
        this.runway = new Semaphore(1, true);
        this.parkingSpots = new Semaphore[config.getParkingSpots()];
        this.parkingSpotAvailable = new boolean[config.getParkingSpots()];
        this.groundService = new Semaphore(config.getGroundServiceTeams(), true);
        this.parkingSpotLock = new Semaphore(1, true);
        
        for (int i = 0; i < parkingSpots.length; i++) {
            parkingSpots[i] = new Semaphore(1, true);
            parkingSpotAvailable[i] = true;
        }
    }
    
    // Thread-safe getInstance method
    public static ControlTower getInstance(AirportConfig config) throws InterruptedException {
        if (instance == null) {
            instanceLock.acquire();
            try {
                if (instance == null) {
                    instance = new ControlTower(config);
                }
            } finally {
                instanceLock.release();
            }
        }
        return instance;
    }

    public void requestLanding() throws InterruptedException {
        runway.acquire();
    }

    public void finishLanding() {
        runway.release();
    }

    public int requestParking() throws InterruptedException {
        int spot = -1;
        
        parkingSpotLock.acquire();
        try {
            for (int i = 0; i < parkingSpotAvailable.length; i++) {
                if (parkingSpotAvailable[i]) {
                    spot = i;
                    break;
                }
            }
            
            if (spot == -1) {
                logger.error("No parking spots available");
                throw new InterruptedException("No parking spots available");
            }

            parkingSpots[spot].acquire();
            parkingSpotAvailable[spot] = false;
        } finally {
            parkingSpotLock.release();
        }
        
        return spot;
    }

    public void releaseParking(int spot) throws InterruptedException {
        parkingSpotLock.acquire();
        try {
            parkingSpotAvailable[spot] = true;
            parkingSpots[spot].release();
        } finally {
            parkingSpotLock.release();
        }
    }

    public void requestGroundService() throws InterruptedException {
        groundService.acquire();
    }

    public void finishGroundService() {
        groundService.release();
    }
}
