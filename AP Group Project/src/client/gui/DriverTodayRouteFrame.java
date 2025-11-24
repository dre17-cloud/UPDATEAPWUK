package client.gui;

import server.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class DriverTodayRouteFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public DriverTodayRouteFrame(String driverId) {

        setTitle("Today's Route & Deliveries");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ==============================
        // HEADER (Route + Title)
        // ==============================
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JLabel routeLabel = new JLabel("Today's Route: Loading...", SwingConstants.CENTER);
        routeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel header = new JLabel("Your Assigned Shipments", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));

        topPanel.add(routeLabel);
        topPanel.add(header);

        add(topPanel, BorderLayout.NORTH);

        // ==============================
        // TABLE COLUMNS
        // ==============================
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

        // Load today's route
        loadTodayRoute(driverId, routeLabel);

        setVisible(true);
    }

    // ======================================================
    // LOAD DRIVER'S SHIPMENTS (Assigned Driver)
    // ======================================================
    private String[][] loadDriverShipments(String driverId) {

        ArrayList<String[]> rows = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql =
                "SELECT `Tracking Number`, `Destination`, `Weight`, `Type`, `Status` " +
                "FROM baseshipment " +
                "WHERE `Assigned Driver` = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, driverId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String tracking = rs.getString("Tracking Number");
                String destination = rs.getString("Destination");
                String weight = rs.getString("Weight");
                String type = rs.getString("Type");
                String status = rs.getString("Status");

                rows.add(new String[]{
                        tracking,
                        destination,
                        weight,
                        type,
                        status
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return rows.toArray(new String[0][0]);
    }

    // ======================================================
    // LOAD TODAY'S ROUTE (from vehicle_schedule.route)
    // ======================================================
    private void loadTodayRoute(String driverId, JLabel routeLabel) {

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql =
                "SELECT route FROM vehicle_schedule " +
                "WHERE driverId = ? AND scheduleDate = CURDATE()";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, driverId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String route = rs.getString("route");

                if (route == null || route.isBlank()) {
                    routeLabel.setText("Today's Route: Not Assigned");
                } else {
                    routeLabel.setText("Today's Route: " + route);
                }
            } else {
                routeLabel.setText("Today's Route: No Schedule");
            }

        } catch (Exception ex) {
            routeLabel.setText("Error loading route");
            ex.printStackTrace();
        }
    }
}
