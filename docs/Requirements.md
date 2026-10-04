# RideWise: Requirements

RideWise is a console-based ride-sharing system (Uber/Ola style). The goal is a clean low-level design, not a full app: in-memory storage, no UI framework, no persistence.

## Functional requirements
1. Register riders.
2. Register drivers.
3. Show available drivers.
4. Request a ride. Before booking, the rider sees every available driver with distance and estimated fare, then confirms (y/n).
5. Match the ride to a driver using a pluggable **RideMatchingStrategy**.
6. Calculate the fare using a pluggable **FareStrategy**.
7. Track ride status: `REQUESTED -> ASSIGNED -> COMPLETED`, or `CANCELLED`.

## Non-functional requirements
- Pricing algorithm is easy to extend (add a class, change one line in `Main`).
- Driver matching logic is easy to change.
- Low coupling between services; services depend on interfaces, not concrete strategies.
- Maintainable, readable code.

## Console menu
| # | Option | Service(s) used |
|---|--------|-----------------|
| 1 | Add Rider | RiderService |
| 2 | Add Driver | DriverService |
| 3 | View Available Drivers | DriverService |
| 4 | Request Ride (preview, confirm, book) | RideService |
| 5 | Complete Ride | RideService |
| 6 | View Rides | RideService |
| 7 | Exit | none |

Every option uses the service layer only and catches invalid input (non-numeric values, unknown ids, blank names, bad vehicle type, no driver available, invalid state transitions).

## Error handling
| Situation | Exception |
|-----------|-----------|
| Rider id does not exist | `RiderNotFoundException` |
| Driver id does not exist | `DriverNotFoundException` |
| Ride id does not exist | `RideNotFoundException` |
| No driver available when requesting a ride | `NoDriverAvailableException` |
| Invalid input (blank name, bad number, bad vehicle type, distance <= 0) | `IllegalArgumentException` |
| Illegal lifecycle transition (for example completing a cancelled ride) | `IllegalStateException` |

The console catches all of these, prints a readable message and returns to the menu. Details are in `Class_Model.md`.

## Domain entities
`Rider`, `Driver`, `Ride`, `FareReceipt`, enums `RideStatus` and `VehicleType`, plus an immutable `Location` value (x, y).

## Assumptions
- Locations are 2D coordinates; distance between points is Euclidean.
- The trip distance is entered by the user when requesting a ride (there is no destination).
- A driver serves one ride at a time; accepting a ride makes them unavailable until completion.
- If no driver is available, the ride is recorded as `CANCELLED` and `NoDriverAvailableException` is raised.
- Peak hours are 08:00-10:00 and 17:00-20:00, with a 1.5x multiplier.
- The estimated fare shown before booking uses the same `FareStrategy` as the final fare. It can differ from the final fare if pricing depends on time (for example the trip completes after peak hours begin or end).
- Declining at the confirmation step creates no ride and leaves drivers untouched.
- Fare = base 30 + distance x per-km rate of the driver's vehicle (BIKE 8, AUTO 12, CAR 18).

## Out of scope (YAGNI)
Payments, ratings, real maps, concurrency, persistence, authentication, ride cancellation by the user.
