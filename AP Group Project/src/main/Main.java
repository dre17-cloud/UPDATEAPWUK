package main;

import java.io.IOException;
import models.*;
import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    public static final ArrayList<Shipment> shipments = new ArrayList<>();
    public static final ArrayList<Invoice> invoices = new ArrayList<>();
    public static final ArrayList<Vehicle> vehicles = new ArrayList<>();
    private static final ArrayList<Customer> customers = new ArrayList<>();
    private static final ArrayList<Clerk> clerks = new ArrayList<>();
    private static final ArrayList<Driver> drivers = new ArrayList<>();

    private static final String MANAGER_USERNAME = "manager";
    private static final String MANAGER_PASSWORD = "admin123";
    private static final Driver driver = new Driver("D001", "Driver", "driver@gmail.com", "driver123");

    public static void main(String[] args) {

        vehicles.add(new Vehicle("VH001", 300, 20));
        driver.assignVehicle(vehicles.get(0));
        drivers.add(driver);

        while (true) {
            clearScreen();
            System.out.println("\n===== SMARTSHIP MAIN MENU =====");
            System.out.println("1. Customer");
            System.out.println("2. Clerk");
            System.out.println("3. Driver");
            System.out.println("4. Manager");
            System.out.println("5. Exit");
            System.out.print("Choose: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> customerMenu();
                case 2 -> clerkMenu();
                case 3 -> driverMenu();
                case 4 -> managerLogin();
                case 5 -> {
                    System.out.println("Exiting...");
                    return;
                }
            }
        }
    }

    // =====================================================
    // CUSTOMER SECTION
    // =====================================================

    private static void customerMenu() {
        clearScreen();

        System.out.println("===== CUSTOMER REGISTRATION =====");
        String name = readAny("Your name: ");
        String email = readEmail("Email: ");
        String password = readAny("Password: ");

        String regEmail = email;
        String regPass = password;

        Customer customer = new Customer("C" + (customers.size() + 1), name, email, password);
        customers.add(customer);

        System.out.println("Registration successful!");
        pause();

        while (true) {
            clearScreen();
            System.out.println("===== CUSTOMER LOGIN =====");

            String logEmail = readAny("Email: ");
            String logPass = readAny("Password: ");

            if (logEmail.equalsIgnoreCase(regEmail) && logPass.equals(regPass)) {
                System.out.println("Login successful!");
                pause();
                customerPortal(customer);
                return;
            }

            System.out.println("Invalid login. Try again.");
            pause();
        }
    }

    private static void customerPortal(Customer customer) {
        while (true) {
            clearScreen();
            System.out.println("\n===== CUSTOMER PORTAL =====");
            System.out.println("1. Create Shipment");
            System.out.println("2. Track Shipment");
            System.out.println("3. View Invoices");
            System.out.println("4. Logout");

            int choice = readInt();

            switch (choice) {

                case 1 -> {
                    String recipient = readAny("Recipient: ");
                    String dest = readAny("Destination: ");
                    int zone = readIntRange("Zone (1-4): ", 1, 4);
                    double weight = readDouble("Weight (kg): ", 0.1);
                    String type = readAny("Type (Standard/Express/Fragile): ");

                    try{
                    Shipment s = new BaseShipment(customer.getName(), recipient, dest, zone, weight, type);
                    shipments.add(s);
                    customer.createShipment(s);

                    
                    Invoice inv = new Invoice(s);
                    invoices.add(inv);

                    System.out.println("Shipment created!");
                    s.printDetails();
                    } catch (IllegalArgumentException ex) {
                        System.out.println("Error creating shipment:" +ex.getMessage());
                    }
                    pause();
                }

                case 2 -> {
                    String tn = readAny("Tracking #: ");
                    customer.trackShipment(tn);
                    pause();
                }

                case 3 -> {
                    boolean found = false;

                    for (Invoice inv : invoices) {

                        BaseShipment bs = (BaseShipment) inv.getShipment();

                        if (bs.getSenderName().equalsIgnoreCase(customer.getName())) {
                            inv.printInvoice();
                            found = true;
                        }
                    }

                    if (!found)
                        System.out.println("No invoices found.");

                    pause();
                }

                case 4 -> { return; }
            }
        }
    }

    // =====================================================
    // CLERK SECTION
    // =====================================================

    private static void clerkMenu() {
        while (true) {
            clearScreen();
            System.out.println("\n===== CLERK PORTAL =====");
            System.out.println("1. View Shipments");
            System.out.println("2. Assign Shipment to Vehicle");
            System.out.println("3. Generate Invoice");
            System.out.println("4. Record Payment");
            System.out.println("5. Logout");

            int ch = readInt();

            switch (ch) {

                case 1 -> {
                    if (shipments.isEmpty())
                        System.out.println("No shipments.");
                    else
                        shipments.forEach(Shipment::printDetails);

                    pause();
                }

                case 2 -> assignShipmentToVehicle();

                case 3 -> generateInvoiceManual();

                case 4 -> recordPayment();

                case 5 -> { return; }
            }
        }
    }

    private static void assignShipmentToVehicle() {

        String tn = readAny("Tracking #: ");

        Shipment s = shipments.stream()
                .filter(x -> x.getTrackingNumber().equalsIgnoreCase(tn))
                .findFirst().orElse(null);

        if (s == null) {
            System.out.println("Shipment not found.");
            pause();
            return;
        }

        BaseShipment bs = (BaseShipment) s;

        System.out.println("Vehicles:");
            for (Vehicle v : vehicles) {
    System.out.println(
            v.getVehicleId() +
            " | Packages: " + v.getShipments().size() + "/" + v.getMaxPackages() +
            " | MaxWeight: " + v.getMaxWeight()
    );
}
                String vid = readAny("Vehicle ID: ");

                Vehicle v = vehicles.stream()
                .filter(x -> x.getVehicleId().equalsIgnoreCase(vid))
                .findFirst().orElse(null);

        if (v == null) {
            System.out.println("Vehicle not found.");
            pause();
            return;
        }

      if (v.getShipments().size() >= v.getMaxPackages()) {
        System.out.println("Vehicle package capacity full!");
        pause();
        return;
    }

    if (bs.getWeight() > v.getMaxWeight()) {
        System.out.println("Weight exceeds vehicle limit!");
        pause();
        return;
}

    v.getShipments().add(s);
    s.setStatus("Assigned");

    System.out.println("Shipment assigned!");
    pause();
    }

    private static void generateInvoiceManual() {
        String tn = readAny("Tracking #: ");

        Shipment s = shipments.stream()
                .filter(x -> x.getTrackingNumber().equalsIgnoreCase(tn))
                .findFirst().orElse(null);

        if (s == null) {
            System.out.println("Shipment not found.");
            pause();
            return;
        }

        
        Invoice inv = new Invoice(s);
        invoices.add(inv);

        inv.printInvoice();
        pause();
    }

    private static void recordPayment() {
        String id = readAny("Invoice #: ");

        Invoice inv = invoices.stream()
                .filter(x -> x.getInvoiceNumber().equalsIgnoreCase(id))
                .findFirst().orElse(null);

        if (inv == null) {
            System.out.println("Invoice not found.");
            pause();
            return;
        }

        double amt = readDouble("Payment amount: ", 0.01);
        inv.makePayment(amt);

        pause();
    }

    // =====================================================
    // DRIVER & MANAGER
    // =====================================================

    private static void driverMenu() {
        while (true){
            clearScreen();
        System.out.println("===== DRIVER PORTAL =====");
        System.out.println("1. View Assigned Deliveries");
        System.out.println("2. Update Delivery Status");
        System.out.println("3. Logout");
        int choice = readInt();
        switch (choice) {
            case 1 -> {
                // Show all shipments assigned to this driver's vehicle
                driver.viewDeliveries();
                pause();
            }

            case 2 -> {
                String tn = readAny("Tracking #: ");

                System.out.println("Choose new status:");
                System.out.println("1. In Transit");
                System.out.println("2. Delivered");
                int opt = readInt();

                String newStatus = switch (opt) {
                    case 1 -> "In Transit";
                    case 2 -> "Delivered";
                    default -> null;
                };

                if (newStatus == null) {
                    System.out.println("Invalid option.");
                } else {
                    driver.updateStatus(tn, newStatus);
                }

                pause();
            }

            case 3 -> {
                return; // back to main menu
            }

            default -> {
                System.out.println("Invalid option.");
                pause();
            }
        }
        

        }
        
    }

    private static void managerLogin() {
        String u = readAny("Username: ");
        String p = readAny("Password: ");

        if (u.equals(MANAGER_USERNAME) && p.equals(MANAGER_PASSWORD)) {
            managerMenu();
        } else {
            System.out.println("Invalid credentials.");
            pause();
        }
    }

    private static void managerMenu() {
        while (true) {
            clearScreen();
            System.out.println("===== MANAGER PORTAL =====");
            System.out.println("1. View All Users");
            System.out.println("2. Add User");
            System.out.println("3. Remove User");
            System.out.println("4. Reset User Password");
            System.out.println("5. View All Shipments");
            System.out.println("6. View All Invoices");
            System.out.println("7. Add Vehicle");
            System.out.println("8. View Vehicles");
            System.out.println("9. Generate Operations Report");
            System.out.println("10. Logout");

            int c = readInt();

            switch (c) {
                case 1 -> {
                    viewAllUsers();
                    pause();
                }

                case 2 -> {
                    addUser();
                    pause();
                }

                case 3 -> {
                    removeUser();
                    pause();

            }
                case 4 -> {
                    resetUserPassword();
                    pause();
                }

                case 5 -> {
                    viewAllShipments();
                    pause();
                }

                case 6 -> {
                    viewAllInvoices();
                    pause();
            
                }

                case 7 -> {
                    String id = readAny("Vehicle ID: ");
                double mw = readDouble("Max Weight: ", 1);
                int mp = readIntRange("Max Packages: ", 1, 500);

                try {
                    Vehicle v = new Vehicle(id, mw, mp);
                    vehicles.add(v);
                    System.out.println("Vehicle added.");
                } catch (IllegalArgumentException ex) {
                    System.out.println("Error creating vehicle: " + ex.getMessage());
                }

                pause();
                }

                case 8 -> {
                    if (vehicles.isEmpty()) {
                    System.out.println("No vehicles added yet.");
                } else {
                    for (Vehicle v : vehicles) {
                        System.out.println(
                                v.getVehicleId()
                                + " | Packages: " + v.getAssignedShipments().size() + "/" + v.getMaxPackages()
                                + " | Load: " + v.getCurrentLoadKg() + "/" + v.getMaxWeight() + " kg"
                        );
                    }
                }
                pause();
                }

                case 9 -> {
                    generateOperationsReport();
                    pause();
                    
                }

                default -> {
                    System.out.println("Invalid option. Please select from 1 to 10.");
                    pause();
                }

        }
    }
    }

    private static void viewAllUsers() {

    System.out.println("===== ALL USERS =====");

    System.out.println("\n--- Customers ---");
    if (customers.isEmpty()) {
        System.out.println("  (none)");
    } else {
        for (Customer c : customers) {
            System.out.println("  " + c.getUserId() + " | " + c.getName() + " | " + c.getEmail());
        }
    }

    System.out.println("\n--- Clerks ---");
    if (clerks.isEmpty()) {
        System.out.println("  (none)");
    } else {
        for (Clerk c : clerks) {
            System.out.println("  " + c.getUserId() + " | " + c.getName() + " | " + c.getEmail());
        }
    }

    System.out.println("\n--- Drivers ---");
    if (drivers.isEmpty()) {
        System.out.println("  (none)");
    } else {
        for (Driver d : drivers) {
            System.out.println("  " + d.getUserId() + " | " + d.getName() + " | " + d.getEmail());
        }
    }
}

private static void addUser() {
    System.out.println("===== ADD USER =====");
    System.out.println("Select role:");
    System.out.println("1. Customer");
    System.out.println("2. Clerk");
    System.out.println("3. Driver");

    int r = readInt();

    String name = readAny("Name: ");
    String email = readEmail("Email: ");
    String password = readAny("Password: ");

    switch (r) {
        case 1 -> {
            String id = "C" + (customers.size() + 1);
            Customer c = new Customer(id, name, email, password);
            customers.add(c);
            System.out.println("Customer added with ID: " + id);
        }
        case 2 -> {
            String id = "CL" + (clerks.size() + 1);
            Clerk c = new Clerk(id, name, email, password);
            clerks.add(c);
            System.out.println("Clerk added with ID: " + id);
        }
        case 3 -> {
            String id = "D" + (drivers.size() + 1);
            Driver d = new Driver(id, name, email, password);
            drivers.add(d);
            System.out.println("Driver added with ID: " + id);
        }
        default -> System.out.println("Invalid role selected.");
    }
}

private static void removeUser() {
    System.out.println("===== REMOVE USER =====");
    String id = readAny("Enter User ID (e.g. C1, CL1, D1): ");

    if (customers.removeIf(c -> c.getUserId().equalsIgnoreCase(id))) {
        System.out.println("Customer removed.");
        return;
    }

    if (clerks.removeIf(c -> c.getUserId().equalsIgnoreCase(id))) {
        System.out.println("Clerk removed.");
        return;
    }

    if (drivers.removeIf(d -> d.getUserId().equalsIgnoreCase(id))) {
        System.out.println("Driver removed.");
        return;
    }

    System.out.println("User not found.");
}

  private static void resetUserPassword() {
    System.out.println("===== RESET USER PASSWORD =====");
    String id = readAny("Enter User ID: ");
    String newPass = readAny("New password: ");

    for (Customer c : customers) {
        if (c.getUserId().equalsIgnoreCase(id)) {
            c.setPassword(newPass);
            System.out.println("Password updated for customer.");
            return;
        }
    }

    for (Clerk c : clerks) {
        if (c.getUserId().equalsIgnoreCase(id)) {
            c.setPassword(newPass);
            System.out.println("Password updated for clerk.");
            return;
        }
    }

    for (Driver d : drivers) {
        if (d.getUserId().equalsIgnoreCase(id)) {
            d.setPassword(newPass);
            System.out.println("Password updated for driver.");
            return;
        }
    }

    System.out.println("User not found.");
}

private static void viewAllShipments() {
    System.out.println("===== ALL SHIPMENTS =====");

    if (shipments.isEmpty()) {
        System.out.println("No shipments in the system.");
        return;
    }

    for (Shipment s : shipments) {
        s.printDetails();
        System.out.println("---------------------------------");
    }
}

private static void viewAllInvoices() {
    System.out.println("===== ALL INVOICES =====");

    if (invoices.isEmpty()) {
        System.out.println("No invoices in the system.");
        return;
    }

    for (Invoice inv : invoices) {
        inv.printInvoice();
        System.out.println("---------------------------------");
    }
}
private static void generateOperationsReport() {
    System.out.println("===== SMARTSHIP OPERATIONS REPORT =====");

    // Users
    System.out.println("\n--- Users ---");
    System.out.println("Total Customers: " + customers.size());
    System.out.println("Total Clerks: " + clerks.size());
    System.out.println("Total Drivers: " + drivers.size());

    // Shipments
    System.out.println("\n--- Shipments ---");
    int totalShipments = shipments.size();
    long pending = shipments.stream().filter(s -> s.getStatus().equalsIgnoreCase("Pending")).count();
    long processed = shipments.stream().filter(s -> s.getStatus().equalsIgnoreCase("Processed")).count();
    long assigned = shipments.stream().filter(s -> s.getStatus().equalsIgnoreCase("Assigned")).count();
    long inTransit = shipments.stream().filter(s -> s.getStatus().equalsIgnoreCase("In Transit")).count();
    long delivered = shipments.stream().filter(s -> s.getStatus().equalsIgnoreCase("Delivered")).count();

    System.out.println("Total Shipments: " + totalShipments);
    System.out.println("Pending: " + pending);
    System.out.println("Processed: " + processed);
    System.out.println("Assigned: " + assigned);
    System.out.println("In Transit: " + inTransit);
    System.out.println("Delivered: " + delivered);

    // Invoices
    System.out.println("\n--- Invoices ---");
    int totalInvoices = invoices.size();
    long paid = invoices.stream().filter(i -> i.getStatus().equalsIgnoreCase("Paid")).count();
    long partiallyPaid = invoices.stream().filter(i -> i.getStatus().equalsIgnoreCase("Partially Paid")).count();
    long unpaid = invoices.stream().filter(i -> i.getStatus().equalsIgnoreCase("Unpaid")).count();

    double totalRevenue = invoices.stream()
            .filter(i -> i.getStatus().equalsIgnoreCase("Paid"))
            .mapToDouble(Invoice::getAmount)
            .sum();

    System.out.println("Total Invoices: " + totalInvoices);
    System.out.println("Paid: " + paid);
    System.out.println("Partially Paid: " + partiallyPaid);
    System.out.println("Unpaid: " + unpaid);
    System.out.printf("Total Revenue (Paid Invoices): $%.2f%n", totalRevenue);

    // Vehicles
    System.out.println("\n--- Vehicles ---");
    System.out.println("Total Vehicles: " + vehicles.size());
    for (Vehicle v : vehicles) {
        System.out.println(
                v.getVehicleId()
                + " | Packages: " + v.getAssignedShipments().size() + "/" + v.getMaxPackages()
                + " | Load: " + v.getCurrentLoadKg() + "/" + v.getMaxWeight() + " kg"
        );
    }

    System.out.println("\n===== END OF REPORT =====");
}
    
    // INPUT HELPERS

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter valid number: ");
            }
        }
    }

    private static int readIntRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = Integer.parseInt(scanner.nextLine().trim());
                if (v >= min && v <= max) return v;
                System.out.println("Out of range.");
            } catch (NumberFormatException ignored) {}
        }
    }

    private static double readDouble(String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            try {
                double v = Double.parseDouble(scanner.nextLine().trim());
                if (v >= min) return v;
                System.out.println("Too small.");
            } catch (NumberFormatException ignored) {}
        }
    }

    private static String readAny(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Cannot be empty.");
        }
    }

    private static String readEmail(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (s.contains("@") && s.contains(".")) return s;
            System.out.println("Invalid email. Please try again.");
        }
    }

    private static void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException ignored) {}
    }
}
