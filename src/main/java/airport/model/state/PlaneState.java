package airport.model.state;

public interface PlaneState {
    void handle() throws InterruptedException;
} 