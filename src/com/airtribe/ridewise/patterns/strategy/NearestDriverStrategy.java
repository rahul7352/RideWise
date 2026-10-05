package com.airtribe.ridewise.patterns.strategy;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class NearestDriverStrategy implements RideMatchingStrategy {

    @Override
    public Optional<Driver> findDriver(Rider rider, List<Driver> drivers) {
        return drivers.stream()
                .min(Comparator.comparingDouble(d -> d.distanceTo(rider)));
    }
}
