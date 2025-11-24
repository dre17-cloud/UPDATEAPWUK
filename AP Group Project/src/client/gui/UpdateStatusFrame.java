package client.gui;

import server.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class UpdateStatusFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JComboBox<String> shipmentSelect;
    private JComboBox<String> statusSelect;

    private String driverId; // RECEIVED FROM DRIVER PORTAL

    public UpdateStatusFrame(String driverId) {   // UPDATED
        this.driverId = driverId;

        setTitle("Update Delivery Status");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Load only shipments assigned to THIS DRIVER
        String[] trackingNumbers = loadTrackingNumbersForDriver(driverId);

        // Shipment selection
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Select Shipment:"), gbc);

        shipmentSelect = new JComboBox<>(trackingNumbers);
        gbc.gridx = 1;
        add(shipmentSelect, gbc);

        // Status selection (ONLY allowed driver statuses)
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("New Status:"), gbc);

        statusSelect = new JComboBox<>(new String[]{
                "In Transit",
                "Delivered"
        });

        gbc.gridx = 1;
        add(statusSelect, gbc);

        // Update button
        JButton updateBtn = new JButton("Update Status");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(updateBtn, gbc);

        updateBtn.addActionListener(e -> updateStatus());
    }

    // ===============================================================
    // LOAD SHIPMENTS ASSIGNED TO THIS DRIVER ONLY
    // ===============================================================
    private String[] loadTrackingNumbersForDriver(String driverId) {

        ArrayList<String> list = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql =
                "SELECT `Tracking Number` FROM baseshipment " +
                "WHERE `Assigned Driver` = ? " +
                "AND Status IN ('Processed', 'In Transit', 'In Vehicle', 'Assigned')";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, driverId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("Tracking Number"));
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading shipments: " + e.getMessage());
        }

        return list.toArray(new String[0]);
    }

    // ===============================================================
    // UPDATE STATUS ONLY FOR SHIPMENTS OWNED BY THIS DRIVER
    // ===============================================================
    private void updateStatus() {

        String tracking = shipmentSelect.getSelectedItem().toString();
        String newStatus = statusSelect.getSelectedItem().toString();

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql =
                "UPDATE baseshipment SET `Status`=? " +
                "WHERE `Tracking Number`=? AND `Assigned Driver`=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, newStatus);
            ps.setInt(2, Integer.parseInt(tracking));
            ps.setString(3, driverId);

            int updated = ps.executeUpdate();
            ps.close();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this,
                        "Status updated successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error: Shipment not found or not assigned to you!",
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + e.getMessage(),
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
