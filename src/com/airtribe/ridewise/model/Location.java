package com.airtribe.ridewise.model;

public record Location(double x, double y) {
    public double distanceTo(Location other) {
        return Math.hypot(x - other.x, y - other.y);
    }
}
