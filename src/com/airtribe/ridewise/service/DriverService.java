package com.airtribe.ridewise.service;

import com.airtribe.ridewise.model.VehicleType;
import com.airtribe.ridewise.exception.DriverNotFoundException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Location;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DriverService {

    private final Map<Integer, Driver> drivers = new LinkedHashMap<>();

    private final IdGenerator idGenerator;

    public DriverService(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Driver registerDriver(String name, Location location, VehicleType vehicleType) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Driver name cannot be null or empty");
        }
        if(location == null) {
            throw new IllegalArgumentException("Driver location cannot be null");
        }
        if(vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }
        Driver driver = new Driver(idGenerator.nextId(), name, vehicleType, location);
        drivers.put(driver.getId(), driver);
        return driver;
    }

    public Driver getDriverById(int id) {
        Driver driver = drivers.get(id);
        if(driver == null) {
            throw new DriverNotFoundException("Driver with id " + id + " not found");
        }
        return driver;
    }

    public void updateAvailability(int id, boolean available) {
        getDriverById(id).setAvailable(available);
    }

    public List<Driver> getAllAvailableDrivers() {
        return drivers.values().stream().filter(Driver::isAvailable).toList();
    }
}
