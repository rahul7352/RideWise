package com.airtribe.ridewise.patterns.strategy;

import com.airtribe.ridewise.model.Ride;

import java.time.LocalTime;
import java.util.function.Supplier;

public class PeakHourFareStrategy implements FareStrategy {

    private static final double PEAK_HOUR_MULTIPLIER = 1.5;

    private final FareStrategy baseStrategy;

    private final Supplier<LocalTime> time;

    public PeakHourFareStrategy(FareStrategy baseStrategy) {
        this(baseStrategy, LocalTime::now);
    }

    public PeakHourFareStrategy(FareStrategy baseStrategy, Supplier<LocalTime> time) {
        this.baseStrategy = baseStrategy;
        this.time = time;
    }

    @Override
    public double calculateFare(Ride ride) {
        double fare = baseStrategy.calculateFare(ride);
        return isPeakHour(time.get()) ? fare * PEAK_HOUR_MULTIPLIER : fare;
    }

    private boolean isPeakHour(LocalTime time) {
        int hour = time.getHour();
        return hour >= 8 && hour < 10 || hour >= 17 && hour < 20;
    }
}
