package client.gui;

import javax.swing.*;
import java.awt.*;

public class CustomerPortal extends JFrame {

    private static final long serialVersionUID = 1L;
    private String senderName;

    public CustomerPortal(String senderName) {
        this.senderName = senderName;

        setTitle("SmartShip - Customer Portal");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        JLabel title = new JLabel("Customer Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);
        gbc.gridwidth = 1;

        JButton createShipmentBtn = new JButton("Create Shipment");
        JButton trackBtn = new JButton("Track Package");
        JButton invoiceBtn = new JButton("View / Pay Invoices");
        JButton logoutBtn = new JButton("Logout");

        gbc.gridy = 1; 
        gbc.gridx = 0; 
        panel.add(createShipmentBtn, gbc);
        
        gbc.gridy = 1; 
        gbc.gridx = 1; 
        panel.add(trackBtn, gbc);
        
        gbc.gridy = 2; 
        gbc.gridx = 0; 
        panel.add(invoiceBtn, gbc);
        gbc.gridy = 2; 
        gbc.gridx = 1; 
        panel.add(logoutBtn, gbc);

        add(panel);



        createShipmentBtn.addActionListener(e -> {
            new CreateShipment(senderName).setVisible(true);
        });
        

        trackBtn.addActionListener(e -> {
        	new TrackShipment(senderName).setVisible(true);
        });



        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });


        invoiceBtn.addActionListener(e -> {
            new CustomerInvoiceList(senderName).setVisible(true);
        });
    }
}
