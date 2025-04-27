package airport.model;

import airport.config.AirportConfig;
import airport.control.ControlTower;

/**
 * Represents an airport with a unique identifier, name, and control tower.
 * Instances should be managed via AirportFactory.
 */
public class Airport {
    private final String id;
    private final String name;
    private final ControlTower controlTower;

    public Airport(String id, String name, AirportConfig config) throws InterruptedException {
        this.id = id;
        this.name = name;
        this.controlTower = new ControlTower(config, this); // Dedicated tower
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ControlTower getControlTower() {
        return controlTower;
    }

    @Override
    public String toString() {
        return String.format("Airport{id='%s', name='%s'}", id, name);
    }
}
