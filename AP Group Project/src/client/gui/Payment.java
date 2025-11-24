package client.gui;

import server.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Payment extends JFrame {

    private static final long serialVersionUID = 1L;

    private JLabel outstandingBalanceLabel, outstandingBalanceValue;
    private JTextField amountField;
    private JButton payButton, backButton;
    private JComboBox<String> paymentMethodBox;  // <<== NEW

    private String invoiceNumber;

    public Payment(String invoiceNumber) {

        this.invoiceNumber = invoiceNumber;

        setTitle("PAYMENT");
        setSize(500, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        setIconImage(new ImageIcon("client/textures/box.png").getImage());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Make Payment");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        gbc.gridy = 0; 
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // OUTSTANDING BALANCE
        outstandingBalanceLabel = new JLabel("Outstanding Balance:");
        gbc.gridy = 1; 
        gbc.gridx = 0;
        add(outstandingBalanceLabel, gbc);

        outstandingBalanceValue = new JLabel("$0.00");
        gbc.gridx = 1;
        add(outstandingBalanceValue, gbc);

        // AMOUNT INPUT
        JLabel payLabel = new JLabel("Enter Amount:");
        gbc.gridy = 2; 
        gbc.gridx = 0;
        add(payLabel, gbc);

        amountField = new JTextField(10);
        gbc.gridx = 1;
        add(amountField, gbc);

        // PAYMENT METHOD (NEW)
        JLabel methodLabel = new JLabel("Payment Method:");
        gbc.gridy = 3;
        gbc.gridx = 0;
        add(methodLabel, gbc);

        paymentMethodBox = new JComboBox<>(new String[]{"Cash", "Card"});
        gbc.gridx = 1;
        add(paymentMethodBox, gbc);

        // BACK BUTTON
        ImageIcon backIcon = new ImageIcon("client/textures/return.png");
        backButton = new JButton(backIcon);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);

        backButton.addActionListener(e -> {
            new Invoice(invoiceNumber).setVisible(true);
            dispose();
        });

        gbc.gridy = 5;
        gbc.gridx = 0;
        add(backButton, gbc);

        // PAY BUTTON
        payButton = new JButton("Pay");
        gbc.gridx = 1;

        payButton.addActionListener(this::processPayment);
        add(payButton, gbc);

        loadOutstandingBalance();
        setVisible(true);
    }

    // LOAD BALANCE
    private void loadOutstandingBalance() {
        try {
            Connection conn = DatabaseConnection.getConnection();

            String query = """
                SELECT Amount, `Amount Paid`
                FROM invoice
                WHERE `Invoice Number` = ?
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, invoiceNumber);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double amount = rs.getDouble("Amount");
                double paid = rs.getDouble("Amount Paid");
                double balance = amount - paid;

                outstandingBalanceValue.setText("$" + String.format("%.2f", balance));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading balance: " + ex.getMessage());
        }
    }

    // PROCESS PAYMENT
    private void processPayment(ActionEvent e) {

        try {
            double payment = Double.parseDouble(amountField.getText().trim());
            double outstanding = getOutstandingBalance();

            if (payment <= 0) {
                JOptionPane.showMessageDialog(this, "Enter a valid payment amount!");
                return;
            }

            if (payment > outstanding) {
                JOptionPane.showMessageDialog(this,
                        "You cannot pay more than the outstanding balance!");
                return;
            }

            Connection conn = DatabaseConnection.getConnection();

            // Get current paid amount
            String fetchSql = """
                SELECT Amount, `Amount Paid`
                FROM invoice
                WHERE `Invoice Number` = ?
            """;

            PreparedStatement fetchPs = conn.prepareStatement(fetchSql);
            fetchPs.setString(1, invoiceNumber);
            ResultSet rs = fetchPs.executeQuery();
            rs.next();

            double totalAmount = rs.getDouble("Amount");
            double alreadyPaid = rs.getDouble("Amount Paid");
            double newTotal = alreadyPaid + payment;

            // NEW STATUS LOGIC
            String newStatus;
            if (newTotal >= totalAmount) {
                newStatus = "Pending"; // fully paid -> awaiting clerk approval
            } else {
                newStatus = "Unpaid";  // partial payment -> not ready for clerk
            }

            // NEW: Get payment method selected
            String paymentMethod = paymentMethodBox.getSelectedItem().toString();

            String sql = """
                UPDATE invoice
                SET `Amount Paid` = ?, Status = ?, `Payment Method` = ?
                WHERE `Invoice Number` = ?
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, newTotal);
            ps.setString(2, newStatus);
            ps.setString(3, paymentMethod);
            ps.setString(4, invoiceNumber);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Payment Successful!");
                new Invoice(invoiceNumber).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Payment failed!");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error processing payment: " + ex.getMessage());
        }
    }

    private double getOutstandingBalance() throws Exception {
        Connection conn = DatabaseConnection.getConnection();

        String query = """
            SELECT Amount, `Amount Paid`
            FROM invoice
            WHERE `Invoice Number` = ?
        """;

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, invoiceNumber);

        ResultSet rs = ps.executeQuery();
        rs.next();

        return rs.getDouble("Amount") - rs.getDouble("Amount Paid");
    }
}
