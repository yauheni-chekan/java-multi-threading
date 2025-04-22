package airport.model;

import airport.config.AirportConfig;
import airport.control.ControlTower;

public class Airport {
    private final String name;
    private final ControlTower controlTower;

    public Airport(String name, AirportConfig config) throws InterruptedException {
        this.name = name;
        this.controlTower = ControlTower.getInstance(config);
    }

    public String getName() {
        return name;
    }

    public ControlTower getControlTower() {
        return controlTower;
    }

    @Override
    public String toString() {
        return "Airport{name='" + name + "'}";
    }
}
