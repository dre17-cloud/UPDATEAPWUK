package client.gui;

import server.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class ShipmentToVechicle extends JFrame {

    private static final long serialVersionUID = 1L;
    private JComboBox<String> shipmentList;
    private JComboBox<String> vehicleList;
    private JComboBox<String> driverList;
    private JLabel statusMessage;

    public ShipmentToVechicle() {
        setSize(600, 450);
        setTitle("Assign Shipment To Vehicle & Driver");
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Assign Shipment To Vehicle & Driver");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;

        // =============================
        // SHIPMENT DROPDOWN
        // =============================
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Select Shipment"), gbc);

        shipmentList = new JComboBox<>();
        gbc.gridx = 1;
        add(shipmentList, gbc);

        loadAvailableShipments();

        // =============================
        // VEHICLE DROPDOWN
        // =============================
        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Select Vehicle"), gbc);

        vehicleList = new JComboBox<>();
        gbc.gridx = 1;
        add(vehicleList, gbc);

        loadVehicles();

        // =============================
        // DRIVER DROPDOWN
        // =============================
        gbc.gridy = 3; gbc.gridx = 0;
        add(new JLabel("Select Driver"), gbc);

        driverList = new JComboBox<>();
        gbc.gridx = 1;
        add(driverList, gbc);

        loadDrivers();

        // =============================
        // STATUS MESSAGE
        // =============================
        statusMessage = new JLabel(" ");
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        add(statusMessage, gbc);

        // =============================
        // ASSIGN BUTTON
        // =============================
        JButton assignBtn = new JButton("Assign Shipment");
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(assignBtn, gbc);

        assignBtn.addActionListener(this::assignShipment);

        setVisible(true);
    }

    // LOAD SHIPMENTS NOT ASSIGNED YET
    private void loadAvailableShipments() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql =
                    "SELECT `Tracking Number` FROM baseshipment WHERE Status = 'Processed'";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                shipmentList.addItem(rs.getString("Tracking Number"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // LOAD VEHICLES
    private void loadVehicles() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT `Vehicle ID` FROM vehicle";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                vehicleList.addItem(rs.getString("Vehicle ID"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // LOAD DRIVERS
    private void loadDrivers() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT userId FROM user WHERE role = 'Driver'";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                driverList.addItem(rs.getString("userId"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ASSIGN SHIPMENT TO VEHICLE AND DRIVER
    private void assignShipment(ActionEvent e) {
        String shipment = (String) shipmentList.getSelectedItem();
        String vehicle = (String) vehicleList.getSelectedItem();
        String driver = (String) driverList.getSelectedItem();

        if (shipment == null || vehicle == null || driver == null) {
            statusMessage.setText("Please select a shipment, vehicle, and driver.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();

            // =================================================
            // 0. CHECK IF VEHICLE IS AT MAX CAPACITY
            // =================================================
            String ckSql = "SELECT `Max Packages`, `Assigned Shipments` FROM vehicle WHERE `Vehicle ID`=?";
            PreparedStatement ckStmt = conn.prepareStatement(ckSql);
            ckStmt.setString(1, vehicle);
            ResultSet ckRs = ckStmt.executeQuery();

            if (ckRs.next()) {
                int max = ckRs.getInt("Max Packages");
                String assigned = ckRs.getString("Assigned Shipments");

                int current = 0;
                if (assigned != null && !assigned.isBlank()) {
                    current = assigned.split(",").length;
                }

                if (current >= max) {
                    statusMessage.setText("Vehicle " + vehicle + " is FULL (" + max + " packages).");
                    return;
                }
            }

            // =================================================
            // 1. GET ROUTE (from Destination)
            // =================================================
            String destSql = "SELECT Destination FROM baseshipment WHERE `Tracking Number`=?";
            PreparedStatement destStmt = conn.prepareStatement(destSql);
            destStmt.setString(1, shipment);
            ResultSet destRs = destStmt.executeQuery();

            String destination = "";
            if (destRs.next()) {
                destination = destRs.getString("Destination");
            }

            // =================================================
            // 2. UPDATE VEHICLE SCHEDULE ROUTE
            // =================================================
            String updateRoute =
                    "UPDATE vehicle_schedule SET route=? WHERE vehicleId=? AND driverId=? AND scheduleDate=CURDATE()";

            PreparedStatement routePS = conn.prepareStatement(updateRoute);
            routePS.setString(1, destination);
            routePS.setString(2, vehicle);
            routePS.setString(3, driver);
            routePS.executeUpdate();

            // =================================================
            // 3. UPDATE SHIPMENT TABLE
            // =================================================
            String updateShipment =
                    "UPDATE baseshipment SET Status='In Vehicle', `Assigned Driver`=?, `Assigned Vehicle`=? WHERE `Tracking Number`=?";

            PreparedStatement sPS = conn.prepareStatement(updateShipment);
            sPS.setString(1, driver);
            sPS.setString(2, vehicle);
            sPS.setString(3, shipment);
            sPS.executeUpdate();

            // =================================================
            // 4. UPDATE VEHICLE'S ASSIGNED SHIPMENTS
            // =================================================
            String getAssigned = "SELECT `Assigned Shipments` FROM vehicle WHERE `Vehicle ID`=?";
            PreparedStatement ps2 = conn.prepareStatement(getAssigned);
            ps2.setString(1, vehicle);
            ResultSet rs = ps2.executeQuery();

            String newList = shipment;

            if (rs.next()) {
                String assigned = rs.getString("Assigned Shipments");

                if (assigned != null && !assigned.isBlank()) {
                    newList = assigned + "," + shipment;
                }
            }

            String updateVehicle =
                    "UPDATE vehicle SET `Assigned Shipments`=? WHERE `Vehicle ID`=?";

            PreparedStatement ps3 = conn.prepareStatement(updateVehicle);
            ps3.setString(1, newList);
            ps3.setString(2, vehicle);
            ps3.executeUpdate();

            statusMessage.setText("Shipment " + shipment + " assigned successfully!");

        } catch (Exception ex) {
            ex.printStackTrace();
            statusMessage.setText("Error assigning shipment.");
        }
    }

    public static void main(String[] args) {
        new ShipmentToVechicle();
    }
}
