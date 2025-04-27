package airport.model.state;

import airport.model.Plane;

public class ApproachingState implements PlaneState {
    private final Plane plane;
    public ApproachingState(Plane plane) {
        this.plane = plane;
    }
    @Override
    public void handle() throws InterruptedException {
        plane.handleLanding();
        plane.setState(new LandedState(plane));
    }
} 