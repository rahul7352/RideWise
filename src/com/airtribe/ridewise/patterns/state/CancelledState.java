package com.airtribe.ridewise.patterns.state;

import com.airtribe.ridewise.model.RideStatus;

final public class CancelledState implements RideState {

    public static final RideState INSTANCE = new CancelledState();

    private CancelledState(){}

    @Override
    public RideStatus status() {
        return RideStatus.CANCELLED;
    }
}
