package airport.model.state;

import airport.model.Plane;

public class LandedState implements PlaneState {
    private final Plane plane;
    public LandedState(Plane plane) {
        this.plane = plane;
    }
    @Override
    public void handle() throws InterruptedException {
        plane.handleGroundOperations();
        plane.setState(new DepartingState(plane));
    }
} 