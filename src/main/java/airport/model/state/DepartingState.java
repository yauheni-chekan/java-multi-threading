package airport.model.state;

import airport.model.Plane;

public class DepartingState implements PlaneState {
    private final Plane plane;
    public DepartingState(Plane plane) {
        this.plane = plane;
    }
    @Override
    public void handle() throws InterruptedException {
        plane.handleTakeoff();
        plane.setState(new EnRouteState(plane));
    }
} 