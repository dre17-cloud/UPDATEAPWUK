
package client.gui;

import server.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Invoice extends JFrame {

    private static final long serialVersionUID = 1L;

    private JLabel invoiceValue, shipmentValue, amount, amountPaid,
            outstandingBalance, status, date;

    public Invoice(String invoiceNumber) {

        setSize(600, 400);
        setTitle("INVOICE");
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon("src/textures/box.png").getImage());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Invoice");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridy = 0; gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        add(titleLabel, gbc);

        gbc.insets = new Insets(5, 5, 5, 5);

        // ===============================
        // INVOICE NUMBER
        // ===============================
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Invoice"), gbc);

        invoiceValue = new JLabel("#" + invoiceNumber);
        gbc.gridx = 1;
        add(invoiceValue, gbc);

        // ===============================
        // SHIPMENT
        // ===============================
        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Shipment"), gbc);

        shipmentValue = new JLabel("#");
        gbc.gridx = 1;
        add(shipmentValue, gbc);

        // ===============================
        // AMOUNT
        // ===============================
        gbc.gridy = 3; gbc.gridx = 0;
        add(new JLabel("Amount"), gbc);

        amount = new JLabel("$0.00");
        gbc.gridx = 1;
        add(amount, gbc);

        // ===============================
        // AMOUNT PAID
        // ===============================
        gbc.gridy = 4; gbc.gridx = 0;
        add(new JLabel("Amount Paid"), gbc);

        amountPaid = new JLabel("$0.00");
        gbc.gridx = 1;
        add(amountPaid, gbc);

        // ===============================
        // OUTSTANDING
        // ===============================
        gbc.gridy = 5; gbc.gridx = 0;
        add(new JLabel("Outstanding Balance"), gbc);

        outstandingBalance = new JLabel("$0.00");
        gbc.gridx = 1;
        add(outstandingBalance, gbc);

        // ===============================
        // STATUS
        // ===============================
        gbc.gridy = 6; gbc.gridx = 0;
        add(new JLabel("Status"), gbc);

        status = new JLabel("Pending");
        gbc.gridx = 1;
        add(status, gbc);

        // ===============================
        // DATE ISSUED
        // ===============================
        gbc.gridy = 7; gbc.gridx = 0;
        add(new JLabel("Date Issued"), gbc);

        date = new JLabel("--/--/----");
        gbc.gridx = 1;
        add(date, gbc);

        // ===============================
        // BUTTONS
        // ===============================
        Image backImg = new ImageIcon("src/textures/return.png").getImage();
        JButton backButton = new JButton(new ImageIcon(backImg));
        gbc.gridy = 10; gbc.gridx = 0;
        gbc.insets = new Insets(20, 0, 0, 0);
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);

        backButton.addActionListener(e -> {
            new CreateShipment("Customer").setVisible(true);
            dispose();
        });
        add(backButton, gbc);

        JButton payButton = new JButton("$ Pay");
        gbc.gridx = 1;
        add(payButton, gbc);

        payButton.addActionListener(e -> {
            new Payment(invoiceNumber).setVisible(true);
            dispose();
        });

        // =========================================
        // LOAD INVOICE FROM DATABASE
        // =========================================
        loadInvoice(invoiceNumber);

        setVisible(true);
    }

    private void loadInvoice(String invoiceNo) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql = """
                SELECT `Invoice Number`, Shipment, Amount, `Amount Paid`, 
                       Status, `Date Issued`
                FROM invoice
                WHERE `Invoice Number` = ?
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceNo);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this,
                        "Invoice not found!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // POPULATE UI
            shipmentValue.setText("#" + rs.getString("Shipment"));
            amount.setText("$" + rs.getDouble("Amount"));
            amountPaid.setText("$" + rs.getDouble("Amount Paid"));

            double balance = rs.getDouble("Amount") - rs.getDouble("Amount Paid");
            outstandingBalance.setText("$" + balance);

            status.setText(rs.getString("Status"));
            date.setText(rs.getString("Date Issued"));

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    
}
