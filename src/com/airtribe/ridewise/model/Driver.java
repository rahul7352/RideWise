package com.airtribe.ridewise.model;

public class Driver {

    private final int id;
    private final String name;
    private final VehicleType vehicleType;
    private Location currentLocation;
    private boolean available = true;
    private int completedRides = 0;

    public Driver(int id, String name, VehicleType vehicleType, Location currentLocation) {
        this.id = id;
        this.name = name;
        this.vehicleType = vehicleType;
        this.currentLocation = currentLocation;
    }

    /** Law of Demeter: callers ask the driver instead of digging into its location. */
    public double distanceTo(Rider rider) {
        return currentLocation.distanceTo(rider.getLocation());
    }

    public void startTrip() {
        this.available = false;
    }

    public void completedTrips() {
        this.available = true;
        completedRides++;
    }

    /*Frees the driver without crediting a completed ride (cancelled trip)*/
    public void releaseTrip() {
        this.available = true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getCompletedRides() {
        return completedRides;
    }

    public void setCompletedRides(int completedRides) {
        this.completedRides = completedRides;
    }

    @Override
    public String toString() {
        return "Driver[" + id + ", " + name + ", " + vehicleType + ", " + currentLocation
                + ", available=" + available + ", completedRides=" + completedRides + "]";
    }
}
