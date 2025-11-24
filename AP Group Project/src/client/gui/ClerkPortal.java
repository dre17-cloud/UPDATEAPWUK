package client.gui;

import javax.swing.*;
import java.awt.*;

public class ClerkPortal extends JFrame {

    private static final long serialVersionUID = 1L;

    public ClerkPortal() {
        setTitle("SmartShip - Clerk Portal");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.BOTH;

        JLabel title = new JLabel("Clerk Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);
        gbc.gridwidth = 1;

        JButton assignBtn = new JButton("Assign Shipments");
        JButton statusBtn = new JButton("Update Status");
        JButton paymentBtn = new JButton("Manage Payments");
        JButton logoutBtn = new JButton("Logout");

        gbc.gridy = 1; gbc.gridx = 0; panel.add(assignBtn, gbc);
        gbc.gridy = 1; gbc.gridx = 1; panel.add(statusBtn, gbc);

        gbc.gridy = 2; gbc.gridx = 0; panel.add(paymentBtn, gbc);
        gbc.gridy = 2; gbc.gridx = 1; panel.add(logoutBtn, gbc);

        add(panel);

        assignBtn.addActionListener(e -> new ShipmentToVechicle().setVisible(true));
        statusBtn.addActionListener(e -> new UpdateStatusFrame(null).setVisible(true));
        paymentBtn.addActionListener(e -> new ManagePaymentsFrame().setVisible(true));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }
}
