package models;

/**
 * Concrete implementation of the Shipment interface 
 */
public class BaseShipment implements Shipment {
    private static final long serialVersionUID = 1L;
    private static int trackingCounter = 1;
    private String trackingNumber;
    private String senderName;
    private String recipientName;
    private String destination;
    private int zone;
    private double weight;
    private String type;   // Standard, Express, Fragile
    private String status; // Pending, Assigned, In Transit, Delivered
    private double cost;

    //add validations in constructor so that invalid shipments cannot be created
    public BaseShipment(String senderName, String recipientName, String destination, int zone, double weight, String type) {
        if (senderName == null || senderName.isBlank()) {
            throw new IllegalArgumentException("Sender name is required.");
        }
        if (recipientName == null || recipientName.isBlank()) {
            throw new IllegalArgumentException("Recipient name is required.");
        }
        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Destination is required.");
        }
        if (zone < 1 || zone > 4) {
            throw new IllegalArgumentException("Zone must be between 1 and 4.");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0.");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Shipment type is required (Standard / Express / Fragile).");
        }
        String typeLower = type.toLowerCase();
        if (!typeLower.equals("standard") &&
            !typeLower.equals("express") &&
            !typeLower.equals("fragile")) {
            throw new IllegalArgumentException("Shipment type must be Standard, Express, or Fragile.");
        }

        this.trackingNumber = generateTrackingNumber();
        this.senderName = senderName;
        this.recipientName = recipientName;
        this.destination = destination;
        this.zone = zone;
        this.weight = weight;
        this.type = type;
        this.status = "Pending";
        this.cost = calculateCost();
    }

    
    

    private String generateTrackingNumber() {
        return "TRK" + String.format("%04d", trackingCounter++);
    }

    private double calculateCost() {
        double baseRate = 500; // JMD
        double zoneMultiplier = 1 + (zone * 0.3);
        double typeMultiplier = switch (type.toLowerCase()) {
            case "express" -> 1.5;
            case "fragile" -> 1.3;
            default -> 1.0;
        };
        return baseRate * zoneMultiplier * typeMultiplier * (weight / 2);
    }

    @Override public String getTrackingNumber() { return trackingNumber; }
    @Override public String getStatus() { return status; }
    

@Override
public void setStatus(String newStatus) {
    if (newStatus == null || newStatus.isBlank()) {
        throw new IllegalArgumentException("Status cannot be empty.");
    }

    newStatus = newStatus.trim();

    // Enforce allowed transitions based on current status
    switch (this.status) {

        case "Pending" -> {
            // After creation: can be processed OR directly assigned
            if (!newStatus.equals("Processed") && !newStatus.equals("Assigned")) {
                throw new IllegalArgumentException(
                        "Pending shipment can only move to 'Processed' or 'Assigned'."
                );
            }
        }

        case "Processed" -> {
            // After clerk processes: must be assigned to a vehicle
            if (!newStatus.equals("Assigned")) {
                throw new IllegalArgumentException(
                        "Processed shipment can only move to 'Assigned'."
                );
            }
        }

        case "Assigned" -> {
            // Driver picks it up
            if (!newStatus.equals("In Transit")) {
                throw new IllegalArgumentException(
                        "Assigned shipment can only move to 'In Transit'."
                );
            }
        }

        case "In Transit" -> {
            // Driver delivers it
            if (!newStatus.equals("Delivered")) {
                throw new IllegalArgumentException(
                        "In Transit shipment can only move to 'Delivered'."
                );
            }
        }

        case "Delivered" -> {
            // Final state, no going back
            throw new IllegalArgumentException(
                    "Delivered shipment cannot change status."
            );
        }

        default -> {
            // In case someone initialized status with some random string
            throw new IllegalStateException("Unknown current status: " + this.status);
        }
    }

    // If we reach here, transition is valid
    this.status = newStatus;
}


    @Override public double getCost() { return cost; }

    @Override
    public void printDetails() {
        System.out.println("Tracking #: " + trackingNumber);
        System.out.println("Sender: " + senderName);
        System.out.println("Recipient: " + recipientName);
        System.out.println("Destination: " + destination + " (Zone " + zone + ")");
        System.out.println("Weight: " + weight + "kg");
        System.out.println("Type: " + type);
        System.out.println("Cost: $" + cost);
        System.out.println("Status: " + status);
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getZone() {
        return zone;
    }

    public void setZone(int zone) {
        this.zone = zone;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}