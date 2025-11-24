package client.gui;

import javax.swing.*;
import java.awt.*;

public class DriverPortal extends JFrame {

    private static final long serialVersionUID = 1L;

    private String driverId; // store driverId passed from Login

    public DriverPortal(String driverId) {
        this.driverId = driverId;

        setTitle("SmartShip - Driver Portal");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        JLabel title = new JLabel("Driver Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);
        gbc.gridwidth = 1;

        JButton viewShipmentsBtn = new JButton("View Assigned Shipments");
        JButton updateStatusBtn = new JButton("Update Delivery Status");
        JButton viewTodayBtn = new JButton("View Today's Route"); // NEW BUTTON
        JButton viewAllSchedulesBtn = new JButton("View All Schedules");
        JButton logoutBtn = new JButton("Logout");

        gbc.gridy = 1; gbc.gridx = 0; panel.add(viewShipmentsBtn, gbc);
        gbc.gridy = 1; gbc.gridx = 1; panel.add(updateStatusBtn, gbc);
        
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(viewAllSchedulesBtn, gbc);


        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2; 
        panel.add(viewTodayBtn, gbc);


        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2; 
        panel.add(logoutBtn, gbc);

        add(panel);

        // view shipments
        viewShipmentsBtn.addActionListener(e -> {
            new ViewShipmentsFrame(driverId).setVisible(true);
        });

        // update status
        updateStatusBtn.addActionListener(e -> {
            new UpdateStatusFrame(driverId).setVisible(true);
        });

        // NEW — VIEW TODAY’S ROUTE
        viewTodayBtn.addActionListener(e -> {
            new DriverTodayRouteFrame(driverId).setVisible(true);
        });
        
        viewAllSchedulesBtn.addActionListener(e -> {
            new DriverAllSchedulesFrame(driverId).setVisible(true);
        });


        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }
}
