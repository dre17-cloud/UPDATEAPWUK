package models;

import java.util.ArrayList;

public class Driver extends User  {
    private static final long serialVersionUID = 1L;
    private ArrayList<Shipment> deliveries;
    private Vehicle assignedVehicle;

    public Driver(String userId, String name, String email, String password) {
        super(userId, name, email, password, "Driver");
        deliveries = new ArrayList<>();
    }

    public void assignVehicle(Vehicle v) {
        this.assignedVehicle = v;
        System.out.println("Vehicle " + v.getVehicleId() + " assigned to driver " + name);
    }

    public void viewDeliveries() {
        if (assignedVehicle == null || assignedVehicle.getAssignedShipments().isEmpty()) {
            System.out.println("No deliveries assigned.");
            return;
        }
        assignedVehicle.listAssignedShipments();
    }

    public void updateStatus(String trackingNumber, String newStatus) {
    if (assignedVehicle == null) {
        System.out.println(" No vehicle assigned to this driver.");
        return;
    }

    for (Shipment s : assignedVehicle.getAssignedShipments()) {
        if (s.getTrackingNumber().equalsIgnoreCase(trackingNumber)) {
            try {
                s.setStatus(newStatus);
                System.out.println("Shipment " + trackingNumber + " marked as " + newStatus);
            } catch (IllegalArgumentException ex) {
                System.out.println("Status update failed: " + ex.getMessage());
            }
            return;
        }
    }

    System.out.println(" Shipment not found for this driver.");
}


    public ArrayList<Shipment> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(ArrayList<Shipment> deliveries) {
        this.deliveries = deliveries;
    }

}

