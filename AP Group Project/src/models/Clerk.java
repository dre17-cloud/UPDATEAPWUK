package models;


public class Clerk extends User  {
    private static final long serialVersionUID = 1L;

    public Clerk(String userId, String name, String email, String password) {
        super(userId, name, email, password, "Clerk");
    }

    public void processShipment(Shipment shipment) {
        if (shipment.getStatus().equalsIgnoreCase("Pending")) {
            shipment.setStatus("Processed");
            System.out.println(" Shipment processed successfully!");
        } else {
            System.out.println(" Shipment already processed or invalid status.");
        }
    }

    public Invoice generateInvoice(Shipment shipment) {
        Invoice invoice = new Invoice(shipment);
        System.out.println("🧾 Invoice generated for shipment " + shipment.getTrackingNumber());
        return invoice;
    }

}
