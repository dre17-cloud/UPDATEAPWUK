package models;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int invoiceCounter = 1;

    private String invoiceNumber;
    private Shipment shipment;
    private double amount; //total invoice amount
    private double amountPaid; //total paid so far
    private String status;
    private Date dateIssued;

    public Invoice(Shipment shipment) {
        //Validations for shipment and amount
        if (shipment == null) {
            throw new IllegalArgumentException("Shipment cannot be null when creating an invoice.");
        }
        double cost = shipment.getCost();
        if (cost <= 0) {
            throw new IllegalArgumentException("Invoice amount must be greater than 0. Shipment cost was: " + cost);
        }

        this.invoiceNumber = "INV" + String.format("%04d", invoiceCounter++);
        this.shipment = shipment;
        this.amount = cost;
        this.amountPaid = 0.0;
        this.status = "Unpaid";
        this.dateIssued = new Date();
    }


    public void makePayment(double amountPaidNow) {
        DecimalFormat df = new DecimalFormat("#,###.00");

        if (amountPaidNow <= 0) {
            System.out.println(" No payment made (amount must be > 0).");
            return;
        }
        double newTotalPaid = this.amountPaid + amountPaidNow; 

        if (newTotalPaid >= amount) {
            this.amountPaid = amount;
            this.status = "Paid";
            System.out.println(" Full payment of $" + df.format(amountPaidNow) +
          " received. Invoice is now PAID in full.");
        } else {
            this.amountPaid = newTotalPaid;
            this.status = "Partially Paid";
            System.out.println(" Partial payment received: $" + df.format(amountPaidNow) +
           ". Total paid so far: $" + df.format(this.amountPaid));
        }
    }
        
    public double getOutstanding() {
        return amount - amountPaid;
    }
            
    

    public void printInvoice() {
        DecimalFormat df = new DecimalFormat("#,###.00");
        System.out.println("\n===== INVOICE =====");
        System.out.println("Invoice #: " + invoiceNumber);
        System.out.println("Shipment #: " + shipment.getTrackingNumber());
        System.out.println("Amount: $" + df.format(amount));
        System.out.println("Amount Paid: $" + df.format(amountPaid));
        System.out.println("Outstanding Balance: $" + df.format(getOutstanding()));
        System.out.println("Status: " + status);
        System.out.println("Date: " + dateIssued);
    }

    public String getInvoiceNumber() { 
        return invoiceNumber; 
    }
    public String getStatus() { 
        return status; 
    }
    public double getAmount() { 
        return amount; 
    }
    public double getAmountPaid() { 
        return amountPaid; 
    }

     public double getOutstandingAmount() { 
        return getOutstanding(); 
    }
    public Shipment getShipment() { 
        return shipment; 
    }
}
