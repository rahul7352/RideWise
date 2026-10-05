package com.airtribe.ridewise.patterns.state;

import com.airtribe.ridewise.model.RideStatus;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Ride;

public interface RideState {

    RideStatus status();

    default void assign(Ride ride, Driver driver) {
        throw illegal(ride, "assign a Driver to");
    }

    default void complete(Ride ride, double fareAmount) {
        throw illegal(ride, "complete");
    }

    default void cancel(Ride ride) {
        throw illegal(ride, "cancel");
    }

    private IllegalStateException illegal(Ride ride, String action) {
        return new IllegalStateException(
                "Cannot " + action + " ride " + ride.getId() + " while it is " + status());
    }
}
