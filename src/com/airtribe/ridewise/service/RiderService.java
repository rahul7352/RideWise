package com.airtribe.ridewise.service;

import com.airtribe.ridewise.exception.RiderNotFoundException;
import com.airtribe.ridewise.model.Location;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.LinkedHashMap;
import java.util.Map;

public class RiderService {

    private final Map<Integer, Rider> riders = new LinkedHashMap<>();
    private final IdGenerator idGenerator;

    public RiderService(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Rider registerRider(String name, Location location) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Rider name cannot be null or empty");
        }
        if(location == null) {
            throw new IllegalArgumentException("Rider location cannot be null");
        }
        Rider rider = new Rider(idGenerator.nextId(), name.trim(), location);
        riders.putIfAbsent(rider.getId(), rider);
        return rider;
    }

    public Rider getRiderById(int id) {
        Rider rider = riders.get(id);
        if(rider == null) {
            throw new RiderNotFoundException("Rider with id " + id + " not found");
        }
        return rider;
    }
}
