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

        JLabel title = new JLabel("Your Complete Schedule History", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        String[] columns = {
                "Vehicle ID",
                "Destination (Route)",
                "Scheduled Date"
        };

        String[][] data = loadSchedules(driverId);

        JTable table = new JTable(data, columns);
        table.setRowHeight(25);

        add(new JScrollPane(table), BorderLayout.CENTER);

        setVisible(true);
    }

    private String[][] loadSchedules(String driverId) {

        ArrayList<String[]> rows = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();

            // ⭐ FIXED:
            // JOIN shipment with schedule so route = Destination ALWAYS appears
            String sql =
                "SELECT vs.vehicleId, b.Destination, vs.scheduleDate " +
                "FROM vehicle_schedule vs " +
                "JOIN baseshipment b ON b.`Assigned Vehicle` = vs.vehicleId " +
                "WHERE vs.driverId = ? " +
                "GROUP BY vs.vehicleId, vs.scheduleDate, b.Destination " +
                "ORDER BY vs.scheduleDate DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, driverId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String vehicleId = rs.getString(1);
                String destination = rs.getString(2);
                String scheduleDate = rs.getString(3);

                rows.add(new String[]{
                        vehicleId,
                        (destination == null ? "N/A" : destination),
                        scheduleDate
                });
            }

            rs.close();
            ps.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return rows.toArray(new String[0][0]);
    }
}
