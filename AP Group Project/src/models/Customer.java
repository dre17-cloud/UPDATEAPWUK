package models;


import java.util.ArrayList;

public class Customer extends User  {
    private static final long serialVersionUID = 1L;
    private ArrayList<Shipment> shipments;

    public Customer() {
        super();
        this.role = "Customer";
        shipments = new ArrayList<>();
    }

    public Customer(String userId, String name, String email, String password) {
        super(userId, name, email, password, "Customer");
        shipments = new ArrayList<>();
    }

    public void createShipment(Shipment shipment) {
        shipments.add(shipment);
        System.out.println(" Shipment created successfully!");
    }

    public void trackShipment(String trackingNumber) {
        for (Shipment s : shipments) {
            if (s.getTrackingNumber().equals(trackingNumber)) {
                System.out.println("Shipment Status: " + s.getStatus());
                return;
            }
        }
        System.out.println(" Tracking number not found.");
    }

    public ArrayList<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(ArrayList<Shipment> shipments) {
        this.shipments = shipments;
    }

    public Object getPassword() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
