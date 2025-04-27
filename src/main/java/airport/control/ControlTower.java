package airport.control;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import airport.config.AirportConfig;
import airport.model.Airport;
import airport.model.Plane;

public class ControlTower {
    private static final Logger logger = LoggerFactory.getLogger(ControlTower.class.getName());
    private final Airport airport;
    private final Semaphore runway;
    private final Semaphore[] parkingSpots;
    private final Semaphore groundService;
    private final Semaphore parkingSpotLock;
    private final boolean[] parkingSpotAvailable;

    // Dedicated constructor for each airport
    public ControlTower(AirportConfig config, Airport airport) {
        this.airport = airport;
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

    public Airport getAirport() {
        return airport;
    }

    public void requestTakeoff(Plane plane) throws InterruptedException {
        try {
            runway.acquire();
            logger.info("{} -> {}: Takeoff granted.", airport.getId(), plane.getId());
            TimeUnit.SECONDS.sleep(3); // Simulate runway usage time
        } catch (InterruptedException e) {
            logger.error("{} -> {}: Takeoff request interrupted.", airport.getId(), plane.getId());
            throw e;
        }
    }   

    public void finishTakeoff(Plane plane) {
        try {
            runway.release();
            logger.info("{} -> {}: Takeoff finish confirmed.", airport.getId(), plane.getId());
        } catch (Exception e) {
            logger.error("{} -> {}: Error releasing runway.", airport.getId(), plane.getId());
            throw e;
        }
    }

    public void requestLanding(Plane plane) throws InterruptedException {
        try {
            runway.acquire();
            logger.info("{} -> {}: Landing granted.", airport.getId(), plane.getId());
            TimeUnit.SECONDS.sleep(4); // Simulate runway usage time
        } catch (InterruptedException e) {
            logger.error("{} -> {}: Landing request interrupted.", airport.getId(), plane.getId());
            throw e;
        }
    }

    public void finishLanding(Plane plane) {
        try {
            runway.release();
            logger.info("{} -> {}: Landing finish confirmed.", airport.getId(), plane.getId());
        } catch (Exception e) {
            logger.error("{} -> {}: Error releasing runway.", airport.getId(), plane.getId());
            throw e;
        }
    }

    public int requestParking(Plane plane) throws InterruptedException {
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
                logger.error("{} -> {}: No parking spots available.", airport.getId(), plane.getId());
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

    public void requestGroundService(Plane plane) throws InterruptedException {
        logger.info("{} -> {}: Requesting ground service.", airport.getId(), plane.getId());
        try {
            groundService.acquire();
            logger.info("{} -> {}: Ground service granted.", airport.getId(), plane.getId());
        } catch (InterruptedException e) {
            logger.error("{} -> {}: Ground service request interrupted.", airport.getId(), plane.getId());
            throw e;
        }
    }

    public void finishGroundService(Plane plane) {
        try {
            groundService.release();
            logger.info("{} -> {}: Ground service finish confirmed.", airport.getId(), plane.getId());
        } catch (Exception e) {
            logger.error("{} -> {}: Error releasing ground service.", airport.getId(), plane.getId());
            throw e;
        }
    }
}
