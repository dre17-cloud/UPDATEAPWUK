package client.gui;

import server.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ManageShipments extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextArea outputArea;
    private JButton loadBtn;

    public ManageShipments() {
        setTitle("View All Shipments");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Text area in the center
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);
        add(scroll, BorderLayout.CENTER);

        // Button panel at the bottom
        JPanel btnPanel = new JPanel(new FlowLayout());
        loadBtn = new JButton("Load Shipments");
        btnPanel.add(loadBtn);
        add(btnPanel, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> loadShipments());
    }

    // -----------------------------------------------------
    // LOAD ALL SHIPMENTS FROM baseshipment TABLE
    // -----------------------------------------------------
    private void loadShipments() {
        outputArea.setText("");

        try {
            Connection conn = DatabaseConnection.getConnection();

            // use your exact table name
            String sql = "SELECT * FROM baseshipment";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            outputArea.append("========== ALL SHIPMENTS ==========\n\n");

            while (rs.next()) {
                outputArea.append(
                        "Tracking #: " + rs.getString("Tracking Number") + "\n" +
                        "Sender:      " + rs.getString("Sender Name") + "\n" +
                        "Recipient:   " + rs.getString("Recipient Name") + "\n" +
                        "Destination: " + rs.getString("Destination") + "\n" +
                        "Zone:        " + rs.getInt("Zone") + "\n" +
                        "Weight:      " + rs.getDouble("Weight") + " kg\n" +
                        "Type:        " + rs.getString("Type") + "\n" +
                        "Status:      " + rs.getString("Status") + "\n" +
                        "Cost:        " + rs.getDouble("Cost") + "\n" +
                        "----------------------------------------\n"
                );
            }

            ps.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading shipments: " + e.getMessage());
        }
    }
}
