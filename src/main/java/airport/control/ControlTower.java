package airport.control;
import java.util.concurrent.Semaphore;
import airport.config.AirportConfig;

public class ControlTower {
    private final Semaphore runway;
    private final Semaphore[] parkingSpots;
    private final Semaphore groundService;
    private final boolean[] parkingSpotAvailable;

    public ControlTower(AirportConfig config) {
        this.runway = new Semaphore(1, true);
        this.parkingSpots = new Semaphore[config.getParkingSpots()];
        this.parkingSpotAvailable = new boolean[config.getParkingSpots()];
        this.groundService = new Semaphore(config.getGroundServiceTeams(), true);
        
        for (int i = 0; i < parkingSpots.length; i++) {
            parkingSpots[i] = new Semaphore(1, true);
            parkingSpotAvailable[i] = true;
        }
    }

    public void requestLanding() throws InterruptedException {
        runway.acquire();
    }

    public void finishLanding() {
        runway.release();
    }

    public synchronized int requestParking() throws InterruptedException {
        // Find first available parking spot
        int spot = -1;
        for (int i = 0; i < parkingSpotAvailable.length; i++) {
            if (parkingSpotAvailable[i]) {
                spot = i;
                break;
            }
        }
        
        if (spot == -1) {
            throw new InterruptedException("No parking spots available");
        }

        parkingSpots[spot].acquire();
        parkingSpotAvailable[spot] = false;
        return spot;
    }

    public synchronized void releaseParking(int spot) {
        parkingSpotAvailable[spot] = true;
        parkingSpots[spot].release();
    }

    public void requestGroundService() throws InterruptedException {
        groundService.acquire();
    }

    public void finishGroundService() {
        groundService.release();
    }
}
