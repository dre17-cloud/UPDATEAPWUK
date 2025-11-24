package client.gui;

import server.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class DriverAllSchedulesFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public DriverAllSchedulesFrame(String driverId) {

        setTitle("All Assigned Schedules");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("All Scheduled Routes", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        String[] columns = {
                "Vehicle ID",
                "Route (Destination)",
                "Schedule Date"
        };

        String[][] data = loadSchedules(driverId);

        JTable table = new JTable(data, columns);
        add(new JScrollPane(table), BorderLayout.CENTER);

        setVisible(true);
    }

    private String[][] loadSchedules(String driverId) {

        java.util.List<String[]> rows = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql =
                "SELECT vehicleId, route, scheduleDate " +
                "FROM vehicle_schedule WHERE driverId=? ORDER BY scheduleDate DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, driverId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rows.add(new String[]{
                        rs.getString("vehicleId"),
                        rs.getString("route"),
                        rs.getString("scheduleDate")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return rows.toArray(new String[0][0]);
    }
}
