# RideWise: Object Relationships

| Relationship | Type | How it appears in code                                                                                            | Why |
|--------------|------|-------------------------------------------------------------------------------------------------------------------|-----|
| Rider -> Ride | Association | `Ride` holds a `Rider` reference                                                                                  | A rider exists independently of any ride and can have many rides. |
| Driver -> Ride | Association | `Ride` holds a `Driver` reference (set on assignment)                                                             | A driver outlives a ride and serves many rides over time. |
| Ride -> FareReceipt | Composition | `Ride` creates and owns `fareReceipt` in `complete(...)`                                                          | A receipt has no meaning without its ride and is created by it. |
| Ride -> RideState | Composition (State pattern) | `Ride` holds its current `RideState` and swaps it on each transition                                              | The state objects (package `pattern.state`) belong to the ride's lifecycle; `Ride` exposes only the `RideStatus` enum, never the state object itself. |
| RideService -> RideMatchingStrategy | Composition (has-a) | Constructor-injected field of interface type                                                                      | Behaviour is plugged in rather than inherited. |
| RideService -> FareStrategy | Composition (has-a) | Constructor-injected field of interface type                                                                      | Same as above. |
| PeakHourFareStrategy -> FareStrategy | Composition (decorator) | Wraps a base `FareStrategy`                                                                                       | Reuses fare logic without inheritance. |
| RideService -> RideOption | Dependency (creates) | `getRideOptions` builds a list of immutable `RideOption` values                                                   | A preview is a value for display; it has no identity or lifecycle. |
| RiderService / DriverService / RideService -> *NotFoundException | Dependency (throws) | `getRiderById`, `getDriverById` and `getRideById` throw `RiderNotFoundException`, `DriverNotFoundException`, `RideNotFoundException` | A missing entity is reported with a precise type instead of a generic `IllegalArgumentException`. |
| RideMatchingStrategy -> NoDriverAvailableException | Dependency (throws) | Implementations throw it from `findDriver` when there is nobody to pick                                           | The contract is "return a driver or throw", never null. |
| RideService -> NoDriverAvailableException | Dependency (catches, rethrows) | `requestRide` catches it, saves the ride as `CANCELLED`, rethrows with the ride id                                | Lets callers tell "nobody is free" apart from bad input. |
| RideService -> RiderService / DriverService | Association (dependency) | Constructor-injected fields                                                                                       | RideService asks the services; it never touches their storage. |
| Rider / Driver -> Location | Association | Immutable `Location` value                                                                                        | Shared safely because it cannot change. |

## Note on "composition" for strategies
In strict UML terms, a strategy passed in from outside is closer to *aggregation* (the service does not create or destroy it). The design term used here, "composition over inheritance", is about building behaviour by holding collaborators instead of subclassing, and that is what `RideService` does.

## Lifetime and ownership
- `Main` creates every service and strategy once and wires them together.
- `RideService` stores rides; the services' maps are private, so no other class mutates them.
- `RideState` instances are stateless singletons held only by `Ride`; `Ride` exposes just `getStatus()` (a `RideStatus` enum), never the state object.
- A `FareReceipt` is only reachable through its `Ride` (`ride.getFareReceipt()`), which reflects the ownership.

## Dependency direction
```
Main -> service -> pattern.strategy (interfaces) <- pattern.strategy (implementations)
          |                      |
          v                      v
        model  <------------>  pattern.state
```
- Services depend on `model` and on the strategy *interfaces*; the concrete strategies are only referenced from `Main`.
- Strategies depend on `model` (`Ride`, `Driver`, `Rider`).
- `model` and `pattern.state` depend on each other: `Ride` holds a `RideState`, and the state classes act on the `Ride` (and use `RideStatus`). This is the one package cycle in the project and a known trade-off of putting the state classes outside `model`; see `SOLID_Reflection.md`.
