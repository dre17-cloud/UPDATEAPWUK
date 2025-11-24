package client.gui;

import server.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateShipment extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField recipientValue, destinationValue, weightValue, costValue;
    private JComboBox<String> typeValue, zoneValue;
    private JButton submitShipment;

    private String senderName;

    public CreateShipment(String senderName) {
        this.senderName = senderName;

        setSize(600, 450);
        setTitle("Customer - CREATE SHIPMENT");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Recipient
        gbc.gridy = 0; gbc.gridx = 0;
        add(new JLabel("Recipient Name"), gbc);

        recipientValue = new JTextField(30);
        gbc.gridx = 1;
        add(recipientValue, gbc);

        // Destination
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Destination"), gbc);

        destinationValue = new JTextField(50);
        gbc.gridx = 1;
        add(destinationValue, gbc);

        // Zone
        gbc.gridx = 2;
        add(new JLabel("Zone"), gbc);

        zoneValue = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        gbc.gridx = 3;
        add(zoneValue, gbc);

        // Weight
        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Weight (kg)"), gbc);

        weightValue = new JTextField(15);
        gbc.gridx = 1;
        add(weightValue, gbc);

        // Type
        gbc.gridy = 3; gbc.gridx = 0;
        add(new JLabel("Type"), gbc);

        typeValue = new JComboBox<>(new String[]{"Standard", "Express", "Fragile"});
        gbc.gridx = 1;
        add(typeValue, gbc);

        // Cost
        gbc.gridy = 4; gbc.gridx = 0;
        add(new JLabel("Cost (JMD)"), gbc);

        costValue = new JTextField(15);
        costValue.setEditable(false);
        gbc.gridx = 1;
        add(costValue, gbc);

        // Submit
        submitShipment = new JButton("Submit");
        gbc.gridy = 5; gbc.gridx = 1;
        add(submitShipment, gbc);

        submitShipment.addActionListener(this::saveShipment);

        setVisible(true);
    }

    private double calculateCost(int zone, int weight, String type) {
        double baseRate = 500;
        double zoneMultiplier = 1 + (zone * 0.3);
        double typeMultiplier = switch (type.toLowerCase()) {
            case "express" -> 1.5;
            case "fragile" -> 1.3;
            default -> 1.0;
        };
        return baseRate * zoneMultiplier * typeMultiplier * (weight / 2.0);
    }

    private void saveShipment(ActionEvent e) {

        String recipient = recipientValue.getText().trim();
        String destination = destinationValue.getText().trim();
        String weightText = weightValue.getText().trim();

        if (recipient.isBlank() || destination.isBlank() || weightText.isBlank()) {
            JOptionPane.showMessageDialog(this, "Complete all fields before continuing");
            return;
        }

        int zone = Integer.parseInt(zoneValue.getSelectedItem().toString());
        int weight = Integer.parseInt(weightText);
        String type = typeValue.getSelectedItem().toString();

        double cost = calculateCost(zone, weight, type);
        costValue.setText(String.format("%.2f", cost));

        try {
            Connection conn = DatabaseConnection.getConnection();

            // Insert Shipment
            String sql = """
                INSERT INTO baseshipment
                (`Sender Name`, `Recipient Name`, `Destination`,
                 `Zone`, `Weight`, `Type`, `Status`, `Cost`)
                VALUES (?, ?, ?, ?, ?, ?, 'Processed', ?)
            """;

            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, senderName);
            ps.setString(2, recipient);
            ps.setString(3, destination);
            ps.setInt(4, zone);
            ps.setInt(5, weight);
            ps.setString(6, type);
            ps.setDouble(7, cost);

            ps.executeUpdate();

            // Retrieve tracking number (AUTO_INCREMENT)
            ResultSet keys = ps.getGeneratedKeys();
            int trackingNumber = 0;
            if (keys.next()) {
                trackingNumber = keys.getInt(1);
            }

            // Create Invoice (Status = Unpaid)
            createInvoice(senderName, cost, trackingNumber);

            JOptionPane.showMessageDialog(this,
                    "Shipment created successfully!\n" +
                    "Tracking Number: " + trackingNumber + "\n" +
                    "Cost: $" + cost);

            dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createInvoice(String senderName, double amount, int trackingNumber) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            String invoiceNo = "INV-" + System.currentTimeMillis();

            String sql = """
                INSERT INTO invoice
                (`Invoice Number`, Shipment, Amount,
                 `Amount Paid`, Status, `Date Issued`, name)
                VALUES (?, ?, ?, 0, 'Unpaid', CURDATE(), ?)
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceNo);
            ps.setInt(2, trackingNumber);
            ps.setDouble(3, amount);
            ps.setString(4, senderName);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error creating invoice: " + e.getMessage());
        }
    }
}
