package client.gui;

import server.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DriverTodayRouteFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public DriverTodayRouteFrame(String driverId) {

        setTitle("Today's Route & Deliveries");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JLabel routeLabel = new JLabel("Today's Route: Loading...", SwingConstants.CENTER);
        routeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel header = new JLabel("Assigned Shipments", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));

        topPanel.add(routeLabel);
        topPanel.add(header);

        add(topPanel, BorderLayout.NORTH);

        // ============================
        // TABLE FOR SHIPMENTS
        // ============================
        String[] columns = {
                "Tracking #",
                "Destination (Route)",
                "Weight",
                "Type",
                "Status"
        };

        String[][] tableData = loadDriverShipments(driverId);

        JTable table = new JTable(tableData, columns);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ============================
        // LOAD TODAY'S ROUTE
        // ============================
        loadTodayRoute(driverId, routeLabel);

        setVisible(true);
    }

    // ===============================================================
    // LOAD TODAY’S ROUTE FROM vehicle_schedule (route = Destination)
    // ===============================================================
    private void loadTodayRoute(String driverId, JLabel routeLabel) {

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql =
                    "SELECT route FROM vehicle_schedule " +
                    "WHERE driverId=? AND scheduleDate = CURDATE()";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, driverId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String route = rs.getString("route");

                if (route == null || route.isBlank()) {
                    routeLabel.setText("Today's Route: (not assigned yet)");
                } else {
                    routeLabel.setText("Today's Route: " + route);
                }

            } else {
                routeLabel.setText("No schedule assigned for today.");
            }

        } catch (Exception ex) {
            routeLabel.setText("Error loading route.");
            ex.printStackTrace();
        }
    }

    // ===============================================================
    // LOAD SHIPMENTS ASSIGNED TO THIS DRIVER FOR TODAY
    // ===============================================================
    private String[][] loadDriverShipments(String driverId) {

        java.util.List<String[]> rows = new java.util.ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql =
                    "SELECT `Tracking Number`, Destination, Weight, Type, Status " +
                    "FROM baseshipment " +
                    "WHERE `Assigned Driver` = ? " +
                    "AND Status IN ('In Vehicle', 'In Transit')";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, driverId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rows.add(new String[]{
                        rs.getString("Tracking Number"),
                        rs.getString("Destination"),  // route = destination
                        String.valueOf(rs.getInt("Weight")),
                        rs.getString("Type"),
                        rs.getString("Status")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return rows.toArray(new String[0][0]);
    }
}
