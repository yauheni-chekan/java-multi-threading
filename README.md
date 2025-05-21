# Core Components
### 1. Plane (`Plane.java`)
 - Represents an aircraft, modeled as a Callable (thread).
 - Manages its own state using the State pattern (PlaneState interface and concrete states).
 - Handles flight phases: landing, ground operations, takeoff, and cruising.
 - Has size-specific service and cruising times.
 - Can be assigned a route (sequence of airports).
 - Interacts with airports and their control towers for resource management.

### 2. Airport (`Airport.java`)
 - Represents an airport with a unique ID, name, and a dedicated ControlTower.
 - ControlTower manages all resource coordination for the airport.

### 3. ControlTower (`ControlTower.java`)
 - Manages shared airport resources using semaphores:
   - Runway (1 at a time)
   - Parking spots (configurable number)
   - Ground service teams (configurable number)
 - Provides methods for planes to request/release resources for landing, takeoff, parking, and ground service.
 - Ensures thread-safe access and logs all operations.

### 4. State Pattern for Plane Phases (`model/state/`)
 - PlaneState interface defines the contract for plane states.
 - Concrete states: LandedState, DepartingState, ApproachingState, EnRouteState.
 - Each state implements specific behavior for the plane during that phase.

### 5. Route (`Route.java`)
 - Represents a sequence of airports for a plane to follow.
 - Used by planes to determine their next destination and travel distance.

### Simulation Control
 - `AirportSimulation.java`
   - Loads configuration and data (airports, routes, planes) from CSV files specified in a properties file.
   - Uses factories (AirportFactory, RouteFactory, PlaneFactory) to instantiate objects from CSV data.
   - Creates a thread pool and submits all planes to run concurrently.
   - Manages simulation lifecycle and logs progress.

### Factories
 - `AirportFactory`, `PlaneFactory`, `RouteFactory`: Load and create airports, planes, and routes from CSV files.

### Configuration
 - `AirportConfig`: Holds configuration for each airport (e.g., number of parking spots, ground service teams).

### Threading Model
 - Each plane runs in its own thread.
 - Airports (via `ControlTower`) manage shared resources using semaphores for thread safety.
 - The simulation uses an ExecutorService to manage plane threads.

### Notable Design Patterns
 - State Pattern: For plane flight phases.
 - Dependency Injection: Airports and routes are injected into planes.
 - Resource Management: Semaphores for runways, parking, and ground services.

### Logging
 - Comprehensive logging is implemented for all major operations and state transitions.
