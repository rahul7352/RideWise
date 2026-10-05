package com.airtribe.ridewise.patterns.state;

import com.airtribe.ridewise.model.RideStatus;

final public class CompletedState implements RideState {

    public static final RideState INSTANCE = new CompletedState();

    private CompletedState() {}

    @Override
    public RideStatus status() {
        return RideStatus.COMPLETED;
    }
}
