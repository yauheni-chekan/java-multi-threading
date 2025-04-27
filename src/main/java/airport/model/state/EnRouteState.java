package airport.model.state;

import airport.model.Plane;

public class EnRouteState implements PlaneState {
    private final Plane plane;
    public EnRouteState(Plane plane) {
        this.plane = plane;
    }
    @Override
    public void handle() throws InterruptedException {
        plane.handleCruising();
        plane.setState(new ApproachingState(plane));
    }
} 