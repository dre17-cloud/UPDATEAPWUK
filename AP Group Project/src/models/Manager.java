package models;

import java.util.ArrayList;

public class Manager extends User {
    private static final long serialVersionUID = 1L;
    private ArrayList<Vehicle> fleet;

    public Manager(String userId, String name, String email, String password) {
        super(userId, name, email, password, "Manager");
        fleet = new ArrayList<>();
    }

    public void addVehicle(Vehicle v) {
        fleet.add(v);
        System.out.println(" Vehicle " + v.getVehicleId() + " added to fleet.");
    }

    public void generateReport(ArrayList<Shipment> shipments) {
        System.out.println("\n SMARTSHIP REPORT");
        System.out.println("--------------------");
        System.out.println("Total shipments: " + shipments.size());
        long delivered = shipments.stream().filter(s -> s.getStatus().equalsIgnoreCase("Delivered")).count();
        System.out.println("Delivered: " + delivered);
        System.out.println("Pending: " + (shipments.size() - delivered));
    }
    public ArrayList<Vehicle> getFleet() {
        return fleet;
    }

    public void setFleet(ArrayList<Vehicle> fleet) {
        this.fleet = fleet;
    }
}
