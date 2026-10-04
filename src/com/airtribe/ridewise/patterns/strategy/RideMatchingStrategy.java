package com.airtribe.ridewise.patterns.strategy;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;

import java.util.List;
import java.util.Optional;

public interface RideMatchingStrategy {

    Optional<Driver> findDriver(Rider rider, List<Driver> drivers);
}
