package client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerPortal extends JFrame {

    private static final long serialVersionUID = 1L;

    public ManagerPortal() {
        setTitle("SmartShip - Manager Portal");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        JLabel title = new JLabel("Manager Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);
        gbc.gridwidth = 1;

        JButton reportBtn = new JButton("Generate Reports");
        JButton fleetBtn = new JButton("Manage Fleet");
        JButton manageVehiclesBtn = new JButton("Manage Vehicles");
        JButton userBtn = new JButton("Manage Users");
        JButton shipmentsBtn = new JButton("View Shipments");
        JButton logoutBtn = new JButton("Logout");

        // NEW BUTTONS
        JButton viewScheduleBtn = new JButton("View Vehicle Schedule");
        JButton assignScheduleBtn = new JButton("Assign Vehicle Schedule");

        gbc.gridy = 1; gbc.gridx = 0; panel.add(reportBtn, gbc);
        gbc.gridy = 1; gbc.gridx = 1; panel.add(fleetBtn, gbc);

        gbc.gridy = 2; gbc.gridx = 0; panel.add(userBtn, gbc);
        gbc.gridy = 2; gbc.gridx = 1; panel.add(manageVehiclesBtn, gbc);

        gbc.gridy = 3; gbc.gridx = 0; panel.add(shipmentsBtn, gbc);

        // ADD NEW BUTTONS
        gbc.gridy = 3; gbc.gridx = 1; panel.add(viewScheduleBtn, gbc);
        gbc.gridy = 4; gbc.gridx = 0; panel.add(assignScheduleBtn, gbc);

        // KEEP LOGOUT BTN
        gbc.gridy = 4; gbc.gridx = 1; panel.add(logoutBtn, gbc);

        add(panel);

        // EXISTING LISTENERS
        userBtn.addActionListener(e -> new ManageUsers().setVisible(true));

        manageVehiclesBtn.addActionListener(e -> new ManageVehicles().setVisible(true));

        shipmentsBtn.addActionListener(e -> new ManageShipments().setVisible(true));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });


        reportBtn.addActionListener(e -> {
            new ReportsFrame().setVisible(true);
        });
        // NEW LISTENERS
        viewScheduleBtn.addActionListener(e ->
                new ManagerScheduleViewer().setVisible(true)
        );

        assignScheduleBtn.addActionListener(e ->
                new AssignScheduleFrame().setVisible(true)
        );
    }
}
