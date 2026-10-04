package com.airtribe.ridewise.model;

public enum VehicleType {

    BIKE(8.0), AUTO(12.0), CAR(18.0);

    private final double ratePerKm;

    VehicleType(double ratePerKm) {
        this.ratePerKm = ratePerKm;
    }

    public double getRatePerKm() {
        return ratePerKm;
    }
}
