package com.airtribe.ridewise.patterns.state;

import com.airtribe.ridewise.model.RideStatus;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Ride;

final public class RequestedState implements RideState{

    public static final RequestedState INSTANCE = new RequestedState();

    private RequestedState() {
    }

    @Override
    public RideStatus status() {
        return RideStatus.REQUESTED;
    }

    @Override
    public void assign(Ride ride, Driver driver) {
        ride.setDriver(driver);
        driver.startTrip();
        ride.setState(AssignedState.INSTANCE);
    }

    @Override
    public void cancel(Ride ride) {
        ride.setState(CancelledState.INSTANCE);
    }
}
