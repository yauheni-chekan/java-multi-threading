package airport.model;

import airport.control.ControlTower;
import airport.util.Logger;

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
            Logger.log(String.format("Plane %s operation interrupted", id));
            Thread.currentThread().interrupt();
        }
    }

    private void handleArrival() throws InterruptedException {
        // Request landing
        Logger.log(String.format("Plane %s requesting landing clearance", id));
        controlTower.requestLanding();
        Logger.log(String.format("Plane %s cleared for landing", id));
        
        // Landing
        Thread.sleep(size.getServiceTime() / 2);
        Logger.log(String.format("Plane %s has landed", id));
        controlTower.finishLanding();

        // Request parking
        int parkingSpot = controlTower.requestParking();
        Logger.log(String.format("Plane %s assigned to parking spot %d", id, parkingSpot));
        
        // Ground service
        controlTower.requestGroundService();
        Logger.log(String.format("Plane %s starting ground service", id));
        Thread.sleep(size.getServiceTime());
        controlTower.finishGroundService();
        
        // Release parking
        controlTower.releaseParking(parkingSpot);
        Logger.log(String.format("Plane %s completed all operations", id));
    }

    private void handleDeparture() throws InterruptedException {
        // Request parking
        int parkingSpot = controlTower.requestParking();
        Logger.log(String.format("Plane %s preparing for departure at spot %d", id, parkingSpot));
        
        // Ground service
        controlTower.requestGroundService();
        Logger.log(String.format("Plane %s starting pre-flight service", id));
        Thread.sleep(size.getServiceTime());
        controlTower.finishGroundService();
        
        // Request takeoff
        controlTower.requestLanding(); // Using same runway semaphore
        Logger.log(String.format("Plane %s cleared for takeoff", id));
        Thread.sleep(size.getServiceTime() / 2);
        controlTower.finishLanding();
        
        // Release parking
        controlTower.releaseParking(parkingSpot);
        Logger.log(String.format("Plane %s has departed", id));
    }
}
