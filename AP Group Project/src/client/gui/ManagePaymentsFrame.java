package client.gui;

import server.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ManagePaymentsFrame extends JFrame {

    private JComboBox<String> invoiceList;

    public ManagePaymentsFrame() {

        setTitle("Clerk - Approve Payments");
        setSize(450, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Label: Invoice
        gbc.gridx = 0; 
        gbc.gridy = 0;
        add(new JLabel("Select Invoice:"), gbc);

        // Load PENDING invoices only
        invoiceList = new JComboBox<>(loadInvoices());
        gbc.gridx = 1;
        add(invoiceList, gbc);

        // Button
        JButton payBtn = new JButton("Approve Payment");
        gbc.gridx = 0; 
        gbc.gridy = 1; 
        gbc.gridwidth = 2;
        add(payBtn, gbc);

        payBtn.addActionListener(e -> recordPayment());
    }

    /** LOAD ONLY PENDING INVOICES */
    private String[] loadInvoices() {
        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql = "SELECT `Invoice Number` FROM invoice WHERE Status = 'Pending'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

            while (rs.next()) {
                model.addElement(rs.getString("Invoice Number"));
            }

            if (model.getSize() == 0)
                model.addElement("No Pending Invoices");

            return toArray(model);

        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{"Error Loading"};
        }
    }

    private String[] toArray(DefaultComboBoxModel<String> model) {
        String[] arr = new String[model.getSize()];
        for (int i = 0; i < model.getSize(); i++) {
            arr[i] = model.getElementAt(i);
        }
        return arr;
    }

    /** APPROVE PAYMENT AUTOMATICALLY */
    private void recordPayment() {
        try {
            String selected = (String) invoiceList.getSelectedItem();

            if (selected == null || selected.contains("No Pending")) {
                JOptionPane.showMessageDialog(this, "No pending invoice selected.");
                return;
            }

            String invoiceNum = selected;
            Connection conn = DatabaseConnection.getConnection();

            // Retrieve invoice totals
            String getSql = """
                SELECT Amount, `Amount Paid`
                FROM invoice
                WHERE `Invoice Number` = ?
            """;

            PreparedStatement getPs = conn.prepareStatement(getSql);
            getPs.setString(1, invoiceNum);
            ResultSet rs = getPs.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Invoice not found.");
                return;
            }

            double amount = rs.getDouble("Amount");

            // Mark full payment and approve
            String updateSql = """
                UPDATE invoice
                SET `Amount Paid` = ?, Status = 'Paid'
                WHERE `Invoice Number` = ?
            """;

            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setDouble(1, amount);      // fully paid
            updatePs.setString(2, invoiceNum);
            updatePs.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Invoice approved and marked as PAID!");

            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            ex.printStackTrace();
        }
    }
}
