package com.airtribe.ridewise.model;

import java.time.LocalDateTime;

public record FareReceipt(int rideId, double amount, LocalDateTime generatedAt) {
}
