package com.airtribe.ridewise.patterns.strategy;

import com.airtribe.ridewise.model.Ride;

public interface FareStrategy {

    double calculateFare(Ride ride);
}
