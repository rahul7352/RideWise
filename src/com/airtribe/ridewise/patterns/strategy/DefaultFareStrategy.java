package com.airtribe.ridewise.patterns.strategy;

import com.airtribe.ridewise.model.Ride;

public class DefaultFareStrategy implements FareStrategy {

    private static final double BASE_FARE = 30.0;

    @Override
    public double calculateFare(Ride ride) {
        return BASE_FARE + (ride.getDistance() * ride.getDriver().getVehicleType().getRatePerKm());
    }
}
