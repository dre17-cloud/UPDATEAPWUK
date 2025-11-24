package models;

import java.io.Serializable;

/**
 * Interface defining behavior for shipment objects.
 */
public interface Shipment extends Serializable {
    String getTrackingNumber();
    String getStatus();
    void setStatus(String status);
    double getCost();
    void printDetails();
}
