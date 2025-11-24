package client.gui;

import server.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class ManagerScheduleViewer extends JFrame {

    private static final long serialVersionUID = 1L;

    public ManagerScheduleViewer() {

        setTitle("Vehicle Schedule Overview");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("All Vehicle Schedules", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        String[] columns = {
                "Vehicle ID",
                "Driver ID",
                "Route (Destination)",
                "Date"
        };

        String[][] data = loadSchedules();

        JTable table = new JTable(data, columns);
        add(new JScrollPane(table), BorderLayout.CENTER);

        setVisible(true);
    }

    private String[][] loadSchedules() {

        ArrayList<String[]> rows = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql = "SELECT vehicleId, driverId, route, scheduleDate FROM vehicle_schedule";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rows.add(new String[]{
                        rs.getString("vehicleId"),
                        rs.getString("driverId"),
                        rs.getString("route"),
                        rs.getString("scheduleDate")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return rows.toArray(new String[0][0]);
    }
}
