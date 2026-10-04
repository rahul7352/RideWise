package com.airtribe.ridewise.service;

import com.airtribe.ridewise.exception.NoDriverAvailableException;
import com.airtribe.ridewise.exception.RideNotFoundException;
import com.airtribe.ridewise.model.FareReceipt;
import com.airtribe.ridewise.model.Ride;
import com.airtribe.ridewise.model.RideOption;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.patterns.strategy.FareStrategy;
import com.airtribe.ridewise.patterns.strategy.RideMatchingStrategy;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RideService {

    private final RiderService riderService;
    private final DriverService driverService;
    private final RideMatchingStrategy rideMatchingStrategy;   // composition: behaviour injected
    private final FareStrategy fareStrategy;
    private final IdGenerator idGenerator;
    private final Map<Integer, Ride> rides = new LinkedHashMap<>();

    public RideService(RiderService riderService, DriverService driverService, RideMatchingStrategy rideMatchingStrategy, FareStrategy fareStrategy, IdGenerator idGenerator) {
        this.riderService = riderService;
        this.driverService = driverService;
        this.rideMatchingStrategy = rideMatchingStrategy;
        this.fareStrategy = fareStrategy;
        this.idGenerator = idGenerator;
    }

    /**
     * Read-only preview shown before booking: every available driver with distance and estimated fare.
     * Nothing is created or changed; an empty list means no driver is free.
     */
    public List<RideOption> getRideoptions(int rideId, double distance) {
        if(distance <= 0) {
            throw new IllegalArgumentException("Distance must be greater than zero");
        }
        Rider rider = riderService.getRiderById(rideId);
        return driverService.getAllAvailableDrivers().stream()
                .map(driver -> new RideOption(
                        driver,
                        driver.distanceTo(rider),
                        fareStrategy.calculateFare(Ride.preview(rider, distance, driver))))
                .toList();
    }

    public double estimateFare(Ride ride) {
        return fareStrategy.calculateFare(ride);
    }

    /**
     * REQUESTED -> ASSIGNED when a driver is found.
     * If nobody is free the ride is recorded as CANCELLED and NoDriverAvailableException is thrown.
     */
    public Ride requestRide(int riderId, double distance) {
        if(distance <= 0) {
            throw new IllegalArgumentException("Distance must be greater than zero");
        }
        Rider rider = riderService.getRiderById(riderId);
        Ride ride = new Ride(idGenerator.nextId(), rider, distance);
        rides.putIfAbsent(riderId, ride);

        rideMatchingStrategy.findDriver(rider, driverService.getAllAvailableDrivers())
                .ifPresentOrElse(ride::assignTo, () -> {
                    ride.cancel();
                    throw new NoDriverAvailableException(
                            "No Driver available for ride " + ride.getId() + " (marked CANCELLED)");
                });
        return ride;
    }

    /** ASSIGNED -> COMPLETED; the fare comes from the injected FareStrategy. */
    public FareReceipt completeRide(int rideId) {
        Ride ride = rides.get(rideId);
        ride.complete(fareStrategy.calculateFare(ride));
        return ride.getFareReceipt().orElseThrow();
    }

    public Ride getRideById(int rideId) {
        Ride ride = rides.get(rideId);
        if(ride == null) {
            throw new RideNotFoundException("No ride with id " + rideId + " found");
        }
        return ride;
    }

    public List<Ride> getAllRides() {
        return new ArrayList<>(rides.values());
    }
}
