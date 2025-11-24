package client.gui;

import server.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class AssignScheduleFrame extends JFrame {

    private JComboBox<String> vehicleList, driverList;
    private JSpinner dateSpinner;
    private JLabel status;

    public AssignScheduleFrame() {
        setTitle("Assign Vehicle Schedule");
        setSize(450, 250);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0;
        add(new JLabel("Vehicle:"), gbc);

        vehicleList = new JComboBox<>();
        loadVehicles();
        gbc.gridx=1;
        add(vehicleList, gbc);

        gbc.gridx=0; gbc.gridy=1;
        add(new JLabel("Driver:"), gbc);

        driverList = new JComboBox<>();
        loadDrivers();
        gbc.gridx=1;
        add(driverList, gbc);

        gbc.gridx=0; gbc.gridy=2;
        add(new JLabel("Date (YYYY-MM-DD):"), gbc);

        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        gbc.gridx=1;
        add(dateSpinner, gbc);

        JButton assignBtn = new JButton("Assign Schedule");
        assignBtn.addActionListener(this::assignSchedule);

        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2;
        add(assignBtn, gbc);

        status = new JLabel(" ");
        gbc.gridy=4;
        add(status, gbc);

        setVisible(true);
    }

    private void loadVehicles(){
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT `Vehicle ID` FROM vehicle");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) vehicleList.addItem(rs.getString("Vehicle ID"));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadDrivers(){
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT userId FROM user WHERE role='Driver'");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) driverList.addItem(rs.getString("userId"));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void assignSchedule(ActionEvent e){
        String vehicle = (String) vehicleList.getSelectedItem();
        String driver = (String) driverList.getSelectedItem();
        java.sql.Date date = new java.sql.Date(((java.util.Date)dateSpinner.getValue()).getTime());

        try {
            Connection conn = DatabaseConnection.getConnection();

            // VEHICLE DAY CONFLICT
            String sql1 = "SELECT * FROM vehicle_schedule WHERE vehicleId=? AND scheduleDate=?";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setString(1, vehicle);
            ps1.setDate(2, date);
            ResultSet rs1 = ps1.executeQuery();
            if(rs1.next()){
                status.setText("Vehicle already assigned on this date!");
                return;
            }

            // DRIVER DAY CONFLICT
            String sql2 = "SELECT * FROM vehicle_schedule WHERE driverId=? AND scheduleDate=?";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setString(1, driver);
            ps2.setDate(2, date);
            ResultSet rs2 = ps2.executeQuery();
            if(rs2.next()){
                status.setText("Driver already has a schedule on this date!");
                return;
            }

            // INSERT EMPTY ROUTE (will fill automatically from shipment)
            String insert = "INSERT INTO vehicle_schedule(vehicleId, driverId, route, scheduleDate) VALUES(?,?,?,?)";
            PreparedStatement ps3 = conn.prepareStatement(insert);
            ps3.setString(1, vehicle);
            ps3.setString(2, driver);
            ps3.setString(3, ""); // route will be filled automatically when shipment assigned
            ps3.setDate(4, date);
            ps3.executeUpdate();

            status.setText("Schedule Assigned Successfully!");

        } catch (Exception ex){
            ex.printStackTrace();
            status.setText("Error assigning schedule.");
        }
    }
}
