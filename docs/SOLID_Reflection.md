# RideWise: SOLID and Design Principles Reflection

## SOLID
**SRP.** Each class has one reason to change. `RiderService` and `DriverService` manage registries; `RideService` orchestrates the ride flow; each strategy implements one algorithm; `Ride` holds data while each `RideState` class owns the rules of one lifecycle stage; `IdGenerator` only produces ids.

**OCP.** To add a new matching rule (for example `HighestRatedDriverStrategy`) or pricing rule (for example `SurgeFareStrategy`), add one class and change one line in `Main`. `RideService` is not modified. The same applies to the lifecycle: a new status (say `IN_PROGRESS`) is a new `RideState` class plus one change to the transition that leads into it, with no growing `if/switch` chains in `Ride`.

**LSP.** Every `RideState` honours the same contract: an allowed transition performs its effects and moves to the next state, and a disallowed one throws `IllegalStateException`. `Ride` treats them all identically. Any `RideMatchingStrategy` or `FareStrategy` can replace another with no change in how `RideService` behaves. Contract: matching receives only available drivers and returns a driver or throws `NoDriverAvailableException` (never null), so every matching strategy fails in the same way; fares are non-negative doubles. `PeakHourFareStrategy` honours the same contract while wrapping another strategy.

**ISP.** The two strategy interfaces have one method each, so implementers are never forced to provide unrelated behaviour, and `RideService` depends only on what it uses.

**DIP.** `RideService` depends on `RideMatchingStrategy` and `FareStrategy` (abstractions), and concrete classes are chosen only in `Main`. `IdGenerator` is injected instead of being a static singleton.

## Other principles
- **Package structure:** the design patterns are grouped under `pattern` (`pattern.strategy`, `pattern.state`), so a reader sees the patterns used at a glance. Domain classes stay in `model`, orchestration in `service`.
- **Composition over inheritance:** there are no class hierarchies. Behaviour comes from injected strategies, and `PeakHourFareStrategy` decorates instead of extending `DefaultFareStrategy`.
- **DRY:** surge logic reuses the base fare; ride state rules live in the `RideState` classes, not repeated in `Ride` or the services; input parsing helpers in `Main` are shared across menu options.
- **KISS:** in-memory `LinkedHashMap`s, a 2D `Location`, and Euclidean distance.
- **YAGNI:** no payments, ratings, persistence or concurrency.
- **Law of Demeter:** `driver.distanceTo(rider)` and `ride.getVehicleType()` mean strategies avoid chains like `ride.getDriver().getVehicleType()` or `rider.getLocation().distanceTo(...)`. Services talk only to their direct collaborators.

## Design decisions and trade-offs
- **Specific not-found exceptions.** `RiderNotFoundException`, `DriverNotFoundException` and `RideNotFoundException` replace a generic `IllegalArgumentException` for missing ids, so callers can catch each case precisely and the type documents the failure (and fits SRP: the exception says what went wrong). Plain validation errors (blank name, distance <= 0) stay `IllegalArgumentException`, and illegal lifecycle moves stay `IllegalStateException`. The trade-off is three more small classes; all are unchecked and `Main` catches them in one place.
- **Fare estimate before booking.** `FareStrategy` still takes a `Ride`, but a fare depends on the driver's vehicle type, so a price can't be computed before a driver exists. `Ride.preview(rider, distance, driver)` builds a throwaway, never-stored ride that is priced without calling `assignTo`, so the preview has no side effects on drivers. The alternative is a strategy signature such as `calculateFare(distance, vehicleType)`, which would remove the need for a preview ride and decouple strategies from `Ride`, at the cost of departing from the specified interface. The preview uses the same strategy as the final fare, so pricing rules are never duplicated (DRY).
- `findDriver` has the signature `Driver findDriver(Rider, List<Driver>)` from the brief. "No match" is reported with `NoDriverAvailableException` instead of returning null, so callers never need a null check and every strategy fails the same way. The trade-off is that the empty-list case uses an exception for a normal situation; an `Optional<Driver>` return would avoid that.
- **State pattern for `Ride`.** Earlier, `Ride` checked `status` with `requireStatus(...)` in each method. Now `RideState` provides default methods that throw, and each concrete state overrides only the transitions it allows. This removes status conditionals and keeps each rule next to its side effects. The trade-offs are four extra small classes for a four-state lifecycle, and, because the state classes now live in the separate `pattern.state` package, `Ride` needs public setters (`setState`, `setDriver`, `setFareReceipt`) so they can act on it. That weakens encapsulation: only convention stops a service from calling `ride.setState(...)`, and `model` and `pattern.state` depend on each other (a package cycle). Two ways to remove that: keep the state classes in `model` with package-private setters, or have each state only *return the next state* and let `Ride` apply the side effects itself. The `pattern` package was chosen to group the design patterns together, which is a readability gain at that cost.
- When a strategy throws `NoDriverAvailableException`, `RideService` catches it, marks the ride `CANCELLED` and rethrows with the ride id, so the history still shows the attempt.
- `PeakHourFareStrategy` takes a `Supplier<LocalTime>` clock so it can be tested deterministically.
- Services hold in-memory maps directly. A repository interface could sit behind them if persistence were ever needed.

## What I would improve next
1. JUnit tests for each strategy and a transition matrix test for the ride states (legal and illegal moves).
2. Repository interfaces so services do not own storage.
3. Driver location updates and a maximum matching radius.
4. Configuration-driven strategy selection (and Spring, if this became a deployable service).
