package client.gui;
import server.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ManageVehicles extends JFrame{

	    private static final long serialVersionUID = 1L;

	    private JTextArea outputArea;
	    private JButton loadBtn, addBtn;

	    public ManageVehicles() {

	        setTitle("Manage Vehicles");
	        setSize(650, 480);
	        setLocationRelativeTo(null);
	        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	        setLayout(new BorderLayout());

	        outputArea = new JTextArea();
	        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
	        JScrollPane scroll = new JScrollPane(outputArea);

	        add(scroll, BorderLayout.CENTER);

	        JPanel btnPanel = new JPanel(new FlowLayout());

	        loadBtn = new JButton("View Vehicles");
	        addBtn = new JButton("Add Vehicle");

	        btnPanel.add(loadBtn);
	        btnPanel.add(addBtn);

	        add(btnPanel, BorderLayout.SOUTH);

	        loadBtn.addActionListener(e -> loadVehicles());
	        addBtn.addActionListener(e -> addVehicle());
	    }

	    // -----------------------------------------------------
	    // LOAD VEHICLES
	    // -----------------------------------------------------
	    private void loadVehicles() {
	        outputArea.setText("");

	        try {
	            Connection conn = DatabaseConnection.getConnection();
	            String sql = "SELECT * FROM vehicle";

	            PreparedStatement ps = conn.prepareStatement(sql);
	            ResultSet rs = ps.executeQuery();

	            outputArea.append("======= VEHICLE LIST =======\n\n");

	            while (rs.next()) {
	                outputArea.append(
	                        "Vehicle ID: " + rs.getString("Vehicle ID") + "\n" +
	                        "Max Weight: " + rs.getDouble("Max Weight") + " kg\n" +
	                        "Max Packages: " + rs.getInt("Max Packages") + "\n" +
	                        "Assigned Shipments: " + rs.getString("Assigned Shipments") + "\n" +
	                        "---------------------------------------\n"
	                );
	            }

	            ps.close();

	        } catch (SQLException e) {
	            JOptionPane.showMessageDialog(this, "Error loading vehicles: " + e.getMessage());
	        }
	    }

	    // -----------------------------------------------------
	    // ADD VEHICLE
	    // -----------------------------------------------------
	    private void addVehicle() {

	        JTextField id = new JTextField();
	        JTextField weight = new JTextField();
	        JTextField packages = new JTextField();
	        

	        Object[] form = {
	                "Vehicle ID:", id,
	                "Max Weight (kg):", weight,
	                "Max Packages:", packages
	                
	        };

	        int result = JOptionPane.showConfirmDialog(
	                this,
	                form,
	                "Add Vehicle",
	                JOptionPane.OK_CANCEL_OPTION
	        );

	        if (result != JOptionPane.OK_OPTION) return;

	        try {
	            Connection conn = DatabaseConnection.getConnection();

	            String sql = """
	                    INSERT INTO vehicle (`Vehicle ID`, `Max Weight`, `Max Packages`, `Assigned Shipments`)
	                    VALUES (?, ?, ?, ?)
	                    """;

	            PreparedStatement ps = conn.prepareStatement(sql);
	            ps.setString(1, id.getText());
	            ps.setDouble(2, Double.parseDouble(weight.getText()));
	            ps.setInt(3, Integer.parseInt(packages.getText()));

	            ps.executeUpdate();
	            ps.close();

	            JOptionPane.showMessageDialog(this, "Vehicle added successfully!");
	            loadVehicles();

	        } catch (SQLException e) {
	            JOptionPane.showMessageDialog(this, "Error adding vehicle: " + e.getMessage());
	        } catch (NumberFormatException ex) {
	            JOptionPane.showMessageDialog(this, "Invalid number format for weight or packages.");
	        }
	    }
	}



