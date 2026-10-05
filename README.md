# RideWise

A console-based ride-sharing system (Uber/Ola style) built in plain Java to demonstrate low-level design: the Strategy and State patterns, SOLID, composition over inheritance, and low coupling. Storage is in-memory; there is no framework and no external dependency.

## Features
- Register riders and drivers
- List available drivers
- Request a ride: see each available driver with distance and estimated fare, confirm, then get matched automatically
- Complete a ride and receive a fare receipt
- Track the ride lifecycle: `REQUESTED -> ASSIGNED -> COMPLETED` (or `CANCELLED`)
- Swap the driver-matching or pricing algorithm without touching the core logic

## Requirements
Java 17 or newer (tested with `--release 17` on JDK 21).

## Build and run
From the project root:

```bash
# macOS / Linux
mkdir -p out && javac -d out $(find src -name '*.java')
java -cp out com.airtribe.ridewise.Main
```

```bat
:: Windows
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out com.airtribe.ridewise.Main
```

## Menu
```
1. Add Rider            5. Complete Ride
2. Add Driver           6. View Rides
3. View Available Drivers   7. Exit
4. Request Ride
```
Locations are x/y coordinates and distance is Euclidean. The ride distance (km) is entered when requesting a ride.

### Sample session (trimmed)
```
Rider id: 1
Distance (km): 10
Available drivers:
  Driver 1: Ravi (BIKE) | 0.1 km away | estimated fare 110.00
  Driver 2: Meena (CAR) | 0.7 km away | estimated fare 210.00
A driver is assigned automatically by the active matching strategy.
Book this ride? (y/n): y
Ride booked: Ride[1, rider=Asha, driver=Ravi, distance=10.0km, status=ASSIGNED]
...
Ride 1 completed. Fare: 110.00
```
Invalid input (unknown ids, non-numeric values, bad menu choices) prints an error message and returns to the menu.

## Project structure
```
src/com/airtribe/ridewise/
├── Main.java                  console menu and composition root (wires everything)
├── model/                     Rider, Driver, Ride, RideOption, FareReceipt, Location, RideStatus, VehicleType
├── pattern/
│   ├── strategy/              RideMatchingStrategy, NearestDriverStrategy, LeastActiveDriverStrategy,
│   │                          FareStrategy, DefaultFareStrategy, PeakHourFareStrategy
│   └── state/                 RideState, RequestedState, AssignedState, CompletedState, CancelledState
├── service/                   RiderService, DriverService, RideService
├── exception/                 NoDriverAvailableException, RiderNotFoundException,
│                              DriverNotFoundException, RideNotFoundException
└── util/                      IdGenerator
docs/                          Requirements, Class_Model, Object_Relationships, SOLID_Reflection, class diagram
```

## Design at a glance
- **Strategy pattern:** `RideService` is constructed with a `RideMatchingStrategy` and a `FareStrategy` (dependency injection through interfaces).
- **State pattern:** `Ride` delegates `assignTo`, `complete` and `cancel` to its current `RideState`; illegal moves throw `IllegalStateException`.
- **Decorator-style composition:** `PeakHourFareStrategy` wraps another `FareStrategy` instead of extending it.
- **Law of Demeter:** services and strategies ask objects directly (`driver.distanceTo(rider)`) instead of walking object chains.
- Full reasoning and trade-offs: see [`docs/SOLID_Reflection.md`](docs/SOLID_Reflection.md).

### Built-in strategies
| Type | Class | Behaviour |
|------|-------|-----------|
| Matching | `NearestDriverStrategy` (default) | Closest available driver |
| Matching | `LeastActiveDriverStrategy` | Fewest completed rides, ties broken by distance |
| Fare | `DefaultFareStrategy` | `30 + distance x rate` (BIKE 8, AUTO 12, CAR 18 per km) |
| Fare | `PeakHourFareStrategy` (default) | Base fare x 1.5 during 08:00-10:00 and 17:00-20:00 |

### Changing the strategies
Edit the wiring in `Main.java`; nothing else changes:

```java
new RideService(riderService, driverService,
    new LeastActiveDriverStrategy(),    // or new NearestDriverStrategy()
    new DefaultFareStrategy(),          // or new PeakHourFareStrategy(new DefaultFareStrategy())
    new IdGenerator());
```
To add a new algorithm, implement `RideMatchingStrategy` or `FareStrategy` and pass it in.

## Documentation
| File | Contents |
|------|----------|
| [`docs/Requirements.md`](docs/Requirements.md) | Functional/non-functional requirements, menu, assumptions, error handling |
| [`docs/Class_Model.md`](docs/Class_Model.md) | Class diagram, packages, responsibilities, ride lifecycle, exceptions |
| [`docs/Object_Relationships.md`](docs/Object_Relationships.md) | Association/composition/dependency table and dependency direction |
| [`docs/SOLID_Reflection.md`](docs/SOLID_Reflection.md) | How each SOLID and design principle is applied, plus trade-offs |
| [`docs/Class_Diagram.png`](docs/Class_Diagram.png) | UML class diagram (SVG version alongside) |

## Assumptions and limitations
- One ride per driver at a time; a driver becomes available again when the ride is completed or cancelled.
- If no driver is available, the matching strategy throws `NoDriverAvailableException`; the ride is saved as `CANCELLED` and the error is rethrown with the ride id.
- The estimated fare uses the same strategy as the final fare, so it can differ if the trip crosses a peak-hour boundary.
- Everything is in memory and single-threaded; no persistence, payments or ratings.

## Possible next steps
JUnit tests for strategies and ride transitions, repository interfaces behind the services, a maximum matching radius, and configuration-driven strategy selection.
