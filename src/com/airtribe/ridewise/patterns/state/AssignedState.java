package com.airtribe.ridewise.patterns.state;

import com.airtribe.ridewise.model.RideStatus;
import com.airtribe.ridewise.model.FareReceipt;
import com.airtribe.ridewise.model.Ride;

import java.time.LocalDateTime;

final public class AssignedState implements RideState {

    public static final RideState INSTANCE = new AssignedState();

    private AssignedState() {}

    @Override
    public RideStatus status() {
        return RideStatus.ASSIGNED;
    }

    @Override
    public void complete(Ride ride, double fareAmount) {
        ride.getDriver().completedTrips();
        ride.setFareReceipt(new FareReceipt(ride.getId(), fareAmount, LocalDateTime.now()));
        ride.setState(CompletedState.INSTANCE);
    }

    @Override
    public void cancel(Ride ride) {
        ride.getDriver().releaseTrip();
        ride.setState(CancelledState.INSTANCE);
    }
}
