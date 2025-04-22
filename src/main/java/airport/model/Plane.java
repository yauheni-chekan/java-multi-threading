package airport.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import airport.control.ControlTower;

public class Plane implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Plane.class);
    
    public enum Size { 
        SMALL(2000, 5000), 
        MEDIUM(3000, 7000), 
        LARGE(4000, 9000);
        
        private final int serviceTime;
        private final int cruisingTime;
        
        Size(int serviceTime, int cruisingTime) {
            this.serviceTime = serviceTime;
            this.cruisingTime = cruisingTime;
        }
        
        public int getServiceTime() {
            return serviceTime;
        }

        public int getCruisingTime() {
            return cruisingTime;
        }
    }

    private final String id;
    private final Size size;
    private Airport currentAirport;
    private FlightPhase phase;

    public Plane(String id, Size size) {
        this.id = id;
        this.size = size;
        this.phase = FlightPhase.CRUISING;
    }

    public void setDestination(Airport airport) {
        if (phase != FlightPhase.CRUISING) {
            throw new IllegalStateException("Can't change destination while not cruising");
        }
        this.currentAirport = airport;
        this.phase = FlightPhase.APPROACHING;
        logger.info("Plane {} approaching {}", id, airport.getName());
    }

    private void startCruising() {
        this.currentAirport = null;
        this.phase = FlightPhase.CRUISING;
        logger.info("Plane {} now cruising", id);
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                switch (phase) {
                    case APPROACHING -> handleLanding();
                    case LANDED -> handleGroundOperations();
                    case DEPARTING -> handleTakeoff();
                    case CRUISING -> {
                        // Simulate cruising time
                        Thread.sleep(size.getCruisingTime());
                        // In a real system, this would be triggered by reaching destination
                        // For simulation, we could inject next destination here
                    }
                }
            }
        } catch (InterruptedException e) {
            logger.error("Plane {} operation interrupted", id);
            Thread.currentThread().interrupt();
        }
    }

    private void handleLanding() throws InterruptedException {
        if (currentAirport == null) {
            throw new IllegalStateException("No airport set for landing");
        }

        ControlTower tower = currentAirport.getControlTower();
        
        // Request landing clearance
        logger.info("Plane {} requesting landing clearance at {}", id, currentAirport.getName());
        tower.requestLanding();
        try {
            logger.info("Plane {} cleared for landing at {}", id, currentAirport.getName());
            Thread.sleep(size.getServiceTime() / 2);
            phase = FlightPhase.LANDED;
            logger.info("Plane {} has landed at {}", id, currentAirport.getName());
        } finally {
            tower.finishLanding();
        }
    }

    private void handleGroundOperations() throws InterruptedException {
        ControlTower tower = currentAirport.getControlTower();
        int parkingSpot = tower.requestParking();
        
        try {
            logger.info("Plane {} assigned to parking spot {} at {}", 
                       id, parkingSpot, currentAirport.getName());
            
            tower.requestGroundService();
            try {
                logger.info("Plane {} starting ground service", id);
                Thread.sleep(size.getServiceTime());
                // After ground service, prepare for departure
                phase = FlightPhase.DEPARTING;
            } finally {
                tower.finishGroundService();
            }
        } finally {
            tower.releaseParking(parkingSpot);
        }
    }

    private void handleTakeoff() throws InterruptedException {
        ControlTower tower = currentAirport.getControlTower();
        
        logger.info("Plane {} requesting takeoff clearance from {}", 
                   id, currentAirport.getName());
        tower.requestLanding(); // Using runway for takeoff
        try {
            logger.info("Plane {} cleared for takeoff from {}", 
                       id, currentAirport.getName());
            Thread.sleep(size.getServiceTime() / 2);
            startCruising(); // Reset airport and change phase to CRUISING
            logger.info("Plane {} has departed from {}", id, currentAirport.getName());
        } finally {
            tower.finishLanding();
        }
    }

    public String getId() {
        return id;
    }

    public Size getSize() {
        return size;
    }

    public FlightPhase getPhase() {
        return phase;
    }
}
