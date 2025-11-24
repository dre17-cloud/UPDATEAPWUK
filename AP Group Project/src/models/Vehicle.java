package models;

import java.io.Serializable;
import java.util.ArrayList;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    private String vehicleId;
    private double maxWeight;
    private int maxPackages;
    private ArrayList<Shipment> assignedShipments;

    public Vehicle(String vehicleId, double maxWeight, int maxPackages) {
        if (vehicleId == null || vehicleId.isBlank()) {
            throw new IllegalArgumentException("Vehicle ID is required.");
        }
        if (maxWeight <= 0) {
            throw new IllegalArgumentException("Max weight must be greater than 0.");
        }
        if (maxPackages <= 0) {
            throw new IllegalArgumentException("Max packages must be greater than 0.");
        }
        this.vehicleId = vehicleId;
        this.maxWeight = maxWeight;
        this.maxPackages = maxPackages;
        this.assignedShipments = new ArrayList<>();
    }

    public boolean assignShipment(Shipment s) {
        if (assignedShipments.size() >= maxPackages) {
            System.out.println("Cannot assign shipment — vehicle full!");
            return false;
        }
        assignedShipments.add(s);
        s.setStatus("Assigned");
        return true;
    }

    public void listAssignedShipments() {
        System.out.println("Vehicle " + vehicleId + " Assignments:");
        for (Shipment s : assignedShipments) {
            System.out.println(" - " + s.getTrackingNumber() + " (" + s.getStatus() + ")");
        }
    }

    public double getCurrentLoadKg() {
    double total = 0;
    for (Shipment s : assignedShipments) {
        // BaseShipment has getWeight(), but Shipment interface does not.
        // So we must cast it.
        if (s instanceof BaseShipment bs) {
            total += bs.getWeight();
        }
    }
    return total;
}


    public String getVehicleId() { 
        return vehicleId; 
    }

    // Main.java expects this name:
    public ArrayList<Shipment> getShipments() { 
        return assignedShipments; 
    }

    // Already exists but keeping for clarity
    public ArrayList<Shipment> getAssignedShipments() { 
        return assignedShipments; 
    }

    // Main.java expects this:
    public int getMaxPackages() { 
        return maxPackages; 
    }

    public double getMaxWeight() { 
        return maxWeight; 
    }

    public void setMaxWeight(double maxWeight) { 
        this.maxWeight = maxWeight; 
    }
    
    @Override
    public String toString() {
    return "Vehicle ID: " + vehicleId +
           " | Max Weight: " + maxWeight +
           " | Max Packages: " + maxPackages +
           " | Assigned Shipments: " + assignedShipments.size();
    }
}
