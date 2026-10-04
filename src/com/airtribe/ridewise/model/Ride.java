package com.airtribe.ridewise.model;

import com.airtribe.ridewise.patterns.state.RequestedState;
import com.airtribe.ridewise.patterns.state.RideState;

import java.util.Optional;

public class Ride {

    private final int id;
    private final Rider rider;
    private final double distance;
    private Driver driver;
    private FareReceipt fareReceipt;
    private RideState rideState = RequestedState.INSTANCE;

    public Ride(int id, Rider rider, double distance) {
        this.id = id;
        this.rider = rider;
        this.distance = distance;
    }

    public static Ride preview(Rider rider, double distance, Driver driver) {
        Ride preview = new Ride(0, rider, distance);
        preview.setDriver(driver);
        return preview;
    }

    public void assignTo(Driver driver) {
        rideState.assign(this, driver);
    }

    public void complete(double fareAmount) {
        rideState.complete(this, fareAmount);
    }

    public void cancel() {
        rideState.cancel(this);
    }
    public RideStatus getStatus() { return rideState.status(); }

    public void setState(RideState rideState) {
        this.rideState = rideState;
    }

    public int getId() {
        return id;
    }

    public Rider getRider() {
        return rider;
    }

    public double getDistance() {
        return distance;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Optional<FareReceipt> getFareReceipt() {
        return Optional.ofNullable(fareReceipt);
    }

    public void setFareReceipt(FareReceipt fareReceipt) {
        this.fareReceipt = fareReceipt;
    }

    public RideState getRideState() {
        return rideState;
    }

    public void setRideState(RideState rideState) {
        this.rideState = rideState;
    }

    @Override public String toString() {
        return "Ride[" + id + ", rider=" + rider.getName()
                + ", driver=" + (driver == null ? "none" : driver.getName())
                + ", distance=" + distance + "km, status=" + getStatus()
                + getFareReceipt().map(r -> String.format(", fare=%.2f", r.amount())).orElse("") + "]";
    }
}
