package com.airtribe.ridewise.model;

/** What a rider sees before booking: one available driver, how far away they are, and the estimated fare. */
public record RideOption(Driver driver, double distanceToRider, double estimatedFare) {

    @Override
    public String toString() {
        return String.format("Driver %d: %s (%s) | %.1f km away | estimated fare %.2f",
                driver.getId(), driver.getName(), driver.getVehicleType(), distanceToRider, estimatedFare);
    }
}
