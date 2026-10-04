package com.airtribe.ridewise;

import com.airtribe.ridewise.model.VehicleType;
import com.airtribe.ridewise.exception.DriverNotFoundException;
import com.airtribe.ridewise.exception.NoDriverAvailableException;
import com.airtribe.ridewise.exception.RideNotFoundException;
import com.airtribe.ridewise.exception.RiderNotFoundException;
import com.airtribe.ridewise.model.*;
import com.airtribe.ridewise.patterns.strategy.DefaultFareStrategy;
import com.airtribe.ridewise.patterns.strategy.NearestDriverStrategy;
import com.airtribe.ridewise.patterns.strategy.PeakHourFareStrategy;
import com.airtribe.ridewise.service.DriverService;
import com.airtribe.ridewise.service.RideService;
import com.airtribe.ridewise.service.RiderService;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.List;
import java.util.Scanner;

public class Main {

    private final Scanner in = new Scanner(System.in);
    private final RiderService riderService = new RiderService(new IdGenerator());
    private final DriverService driverService = new DriverService(new IdGenerator());
    private final RideService rideService = new RideService(
            riderService, driverService,
            new NearestDriverStrategy(),                          // swap: new LeastActiveDriverStrategy()
            new PeakHourFareStrategy(new DefaultFareStrategy()),  // swap: new DefaultFareStrategy()
            new IdGenerator());

    static void main() {
        new Main().run();
    }

    private void run() {
        banner("RideWise —> Modular Ride-Sharing System");
        while (true) {
            printMenu();
            try {
                switch (readInt("Choose option: ")) {
                    case 1 -> addRider();
                    case 2 -> addDriver();
                    case 3 -> viewAvailableDrivers();
                    case 4 -> requestRide();
                    case 5 -> completeRide();
                    case 6 -> viewRides();
                    case 7 -> { System.out.println("Bye!"); return; }
                    default -> System.out.println("Invalid option, pick 1-7.");
                }
            } catch (NoDriverAvailableException e) {
                System.out.println("Sorry: " + e.getMessage());
            } catch (RiderNotFoundException | DriverNotFoundException | RideNotFoundException
                     | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void addRider() {
        String name = readLine("Name: ");
        System.out.println("Registered " + riderService.registerRider(name, readLocation()));
    }

    private void addDriver() {
        String name = readLine("Name: ");
        Location loc = readLocation();
        VehicleType type = readVehicleType();
        System.out.println("Registered " + driverService.registerDriver(name, loc, type));
    }

    private void viewAvailableDrivers() {
        List<Driver> available = driverService.getAllAvailableDrivers();
        if (available.isEmpty()) System.out.println("No drivers available.");
        else available.forEach(System.out::println);
    }

    private void requestRide() {
        int riderId = readInt("Rider id: ");
        double distance = readDouble("Distance (km): ");

        List<RideOption> options = rideService.getRideoptions(riderId, distance);
        if (!options.isEmpty()) {
            System.out.println("Available drivers:");
            options.forEach(o -> System.out.println("  " + o));
            System.out.println("A driver is assigned automatically by the active matching strategy.");
            if (!readYesNo("Book this ride? (y/n): ")) {
                System.out.println("Ride request cancelled.");
                return;
            }
        }
        // With no available driver this records a CANCELLED ride and throws NoDriverAvailableException.
        Ride ride = rideService.requestRide(riderId, distance);
        System.out.println("Ride booked: " + ride);
        System.out.println("Matched driver: " + ride.getDriver());
        System.out.printf("Estimated fare: %.2f (final fare is calculated on completion)%n", rideService.estimateFare(ride));
    }

    private void completeRide() {
        FareReceipt receipt = rideService.completeRide(readInt("Ride id: "));
        System.out.printf("Ride %d completed. Fare: %.2f (at %s)%n",
                receipt.rideId(), receipt.amount(), receipt.generatedAt());
    }

    private void viewRides() {
        List<Ride> rides = rideService.getAllRides();
        if (rides.isEmpty()) System.out.println("No rides yet.");
        else rides.forEach(System.out::println);
    }

    // ---- input helpers: bad input becomes IllegalArgumentException, handled once in run() ----
    private String readLine(String prompt) { System.out.print(prompt); return in.nextLine(); }

    private int readInt(String prompt) {
        try { return Integer.parseInt(readLine(prompt).trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Please enter a whole number"); }
    }

    private double readDouble(String prompt) {
        try { return Double.parseDouble(readLine(prompt).trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Please enter a number"); }
    }

    private boolean readYesNo(String prompt) {
        String answer = readLine(prompt).trim().toLowerCase();
        if (answer.equals("y") || answer.equals("yes")) return true;
        if (answer.equals("n") || answer.equals("no")) return false;
        throw new IllegalArgumentException("Please answer y or n");
    }

    private VehicleType readVehicleType() {
        try { return VehicleType.valueOf(readLine("Vehicle (BIKE/AUTO/CAR): ").trim().toUpperCase()); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("Vehicle must be BIKE, AUTO or CAR"); }
    }

    private Location readLocation() { return new Location(readDouble("Location x: "), readDouble("Location y: ")); }

    private void printMenu() {
        System.out.println("\n1. Add Rider\n2. Add Driver\n3. View Available Drivers\n4. Request Ride\n"
                + "5. Complete Ride\n6. View Rides\n7. Exit");
    }

    private static void banner(String title) {
        String line = "═".repeat(title.length() + 4);
        System.out.println("╔" + line + "╗");
        System.out.println("║  " + title + "  ║");
        System.out.println("╚" + line + "╝");
    }
}
