package airport.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import airport.control.ControlTower;

public class Plane implements Runnable {
    public enum Size { 
        SMALL(2000), 
        MEDIUM(3000), 
        LARGE(4000);
        
        private final int serviceTime;
        
        Size(int serviceTime) {
            this.serviceTime = serviceTime;
        }
        
        public int getServiceTime() {
            return serviceTime;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(Plane.class.getName());
    private final String id;
    private final Size size;
    private final ControlTower controlTower;
    private final boolean isArriving;

    public Plane(String id, Size size, ControlTower controlTower, boolean isArriving) {
        this.id = id;
        this.size = size;
        this.controlTower = controlTower;
        this.isArriving = isArriving;
    }

    @Override
    public void run() {
        try {
            if (isArriving) {
                handleArrival();
            } else {
                handleDeparture();
            }
        } catch (InterruptedException e) {
            logger.error("Plane {} operation interrupted", id);
            Thread.currentThread().interrupt();
        }
    }

    private void handleArrival() throws InterruptedException {
        // Request landing
        logger.info("Plane {} requesting landing clearance", id);
        controlTower.requestLanding();
        logger.info("Plane {} cleared for landing", id);
        
        // Landing
        Thread.sleep(size.getServiceTime() / 2);
        logger.info("Plane {} has landed", id);
        controlTower.finishLanding();

        // Request parking
        int parkingSpot = controlTower.requestParking();
        logger.info("Plane {} assigned to parking spot {}", id, parkingSpot);
        
        // Ground service
        controlTower.requestGroundService();
        logger.info("Plane {} starting ground service", id);
        Thread.sleep(size.getServiceTime());
        controlTower.finishGroundService();
        
        // Release parking
        controlTower.releaseParking(parkingSpot);
        logger.info("Plane {} completed all operations", id);
    }

    private void handleDeparture() throws InterruptedException {
        // Request parking
        int parkingSpot = controlTower.requestParking();
        logger.info("Plane {} preparing for departure at spot {}", id, parkingSpot);
        
        // Ground service
        controlTower.requestGroundService();
        logger.info("Plane {} starting pre-flight service", id);
        Thread.sleep(size.getServiceTime());
        controlTower.finishGroundService();
        
        // Request takeoff
        controlTower.requestLanding(); // Using same runway semaphore
        logger.info("Plane {} cleared for takeoff", id);
        Thread.sleep(size.getServiceTime() / 2);
        controlTower.finishLanding();
        
        // Release parking
        controlTower.releaseParking(parkingSpot);
        logger.info("Plane {} has departed", id);
    }
}
