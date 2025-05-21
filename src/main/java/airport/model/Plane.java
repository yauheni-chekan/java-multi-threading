package airport.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import airport.control.ControlTower;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;

import airport.model.state.PlaneState;
import airport.model.state.LandedState;

public class Plane implements Callable<Void> {
    private static final Logger logger = LoggerFactory.getLogger(Plane.class);
    
    public enum Size { 
        SMALL(2000, 750),    // Typical small turboprop: ~750 km/h
        MEDIUM(3000, 850),   // Typical narrow-body jet: ~850 km/h
        LARGE(4000, 900);    // Typical wide-body jet: ~900 km/h
        
        private final int serviceTime;
        private final int cruisingSpeed;
        Size(int serviceTime, int cruisingSpeed) {
            this.serviceTime = serviceTime;
            this.cruisingSpeed = cruisingSpeed;
        }
        
        public int getServiceTime() {
            return serviceTime;
        }

        public int getCruisingSpeed() {
            return cruisingSpeed;
        }
    }

    private final String id;
    private final Size size;
    private Airport currentAirport;
    private Route route;
    private int routePointIndex = 0;
    private PlaneState state;

    public Plane(String id, Size size) {
        this.id = id;
        this.size = size;
        this.route = null;
        this.state = new LandedState(this);
    }

    public void setDestination(Airport airport) {
        this.currentAirport = airport;
    }

    public void assignRoute(Route route) {
        this.route = Objects.requireNonNull(route, "Route must not be null");
        this.routePointIndex = 0;
        this.currentAirport = route.getAirports().get(routePointIndex);
        logger.info("Plane {} assigned to route {}", id, route.getRouteId());
    }

    public Optional<Route> getRoute() {
        return Optional.ofNullable(route);
    }

    @Override
    public Void call() {
        try {
            while (!Thread.interrupted()) {
                state.handle();
            }
        } catch (InterruptedException e) {
            logger.error("Plane {} operation interrupted", id);
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public void run() {
        call();
    }

    public void setState(PlaneState state) {
        this.state = state;
    }

    public void handleLanding() throws InterruptedException {
        if (currentAirport == null) {
            throw new IllegalStateException("No airport set for landing");
        }
        ControlTower tower = currentAirport.getControlTower();
        logger.info("{} -> {}: requesting landing clearance.", id, currentAirport.getId());
        tower.requestLanding(this);
        try {
            TimeUnit.MILLISECONDS.sleep(size.getServiceTime() / 2);
            logger.info("{} -> {}: has landed. Releasing runway.", id, currentAirport.getId());
        } finally {
            tower.finishLanding(this);
        }
    }

    public void handleGroundOperations() throws InterruptedException {
        ControlTower tower = currentAirport.getControlTower();
        int parkingSpot = tower.requestParking(this);
        try {
            logger.info("{} -> {}: Requesting ground service.", id, currentAirport.getId());
            tower.requestGroundService(this);
            try {
                logger.info("{} -> {}: Starting ground service.", id, currentAirport.getId());
                TimeUnit.MILLISECONDS.sleep(size.getServiceTime());
            } finally {
                logger.info("{} -> {}: Finishing ground service.", id, currentAirport.getId());
                tower.finishGroundService(this);
            }
        } finally {
            logger.info("{} -> {}: Ground service finished. Releasing parking spot.", id, currentAirport.getId());
            tower.releaseParking(parkingSpot, this);
        }
    }

    public void handleTakeoff() throws InterruptedException {
        ControlTower tower = currentAirport.getControlTower();
        logger.info("{} -> {}: requesting takeoff clearance.", id, currentAirport.getId());
        tower.requestTakeoff(this); // Using runway for takeoff
        try {
            TimeUnit.MILLISECONDS.sleep(size.getServiceTime() / 2);
            logger.info("{} -> {}: has departed. Releasing runway.", id, currentAirport.getId());
        } finally {
            tower.finishTakeoff(this);
        }
    }

    public void handleCruising() throws InterruptedException {
        if (currentAirport == null) {
            throw new IllegalStateException("No airport set for cruising");
        }
        changeNextDestination();
        double travelTime = route.getDistance(currentAirport) / size.getCruisingSpeed();
        int hours = (int) travelTime;
        int minutes = (int) Math.round((travelTime - hours) * 60);
        String travelTimeFormatted = String.format("%02d:%02d", hours, minutes);
        logger.info("{} is en route to {} ({} km away). Estimated travel time: {}", id, currentAirport.getId(), route.getDistance(currentAirport), travelTimeFormatted);
        TimeUnit.MILLISECONDS.sleep((long) (travelTime * 1000));
    }

    public void changeNextDestination() {
        if (route == null || route.getAirports().isEmpty()) {
            logger.warn("Plane {} has no route or waypoints to follow.", id);
            return;
        }
        routePointIndex = (routePointIndex + 1) % route.getAirports().size();
        Airport nextAirport = route.getAirports().get(routePointIndex);
        setDestination(nextAirport);
        logger.info("{} changing destination to {} (waypoint {} of {})", id, nextAirport.getId(), routePointIndex + 1, route.getAirports().size());
    }

    public String getId() {
        return id;
    }

    public Size getSize() {
        return size;
    }

    public Airport getCurrentAirport() {
        return currentAirport;
    }
}
