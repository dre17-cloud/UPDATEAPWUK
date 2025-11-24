package client.gui;

import server.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerInvoiceList extends JFrame {

    private static final long serialVersionUID = 1L;

    private String senderName;
    private JComboBox<String> invoiceDropdown;
    private JButton openBtn;

    public CustomerInvoiceList(String senderName) {
        this.senderName = senderName;

        setTitle("Your Invoices");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Select an Invoice");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridy = 0; gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;

        // DROPDOWN LIST
        invoiceDropdown = new JComboBox<>();
        loadInvoicesForCustomer();

        gbc.gridy = 1; gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(invoiceDropdown, gbc);

        // OPEN BUTTON
        openBtn = new JButton("Open Invoice");
        gbc.gridy = 2; gbc.gridx = 0;
        gbc.gridwidth = 2;

        openBtn.addActionListener(e -> openInvoice());
        add(openBtn, gbc);

        setVisible(true);
    }

    private void loadInvoicesForCustomer() {
        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql = """
                SELECT `Invoice Number`
                FROM invoice
                WHERE name = ?
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, senderName);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {
                invoiceDropdown.addItem(rs.getString("Invoice Number"));
                found = true;
            }

            if (!found) {
                JOptionPane.showMessageDialog(this,
                        "You have no invoices.",
                        "No Invoices",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading invoices: " + e.getMessage());
        }
    }

    private void openInvoice() {
        String invoiceNo = (String) invoiceDropdown.getSelectedItem();

        if (invoiceNo == null) {
            JOptionPane.showMessageDialog(this, "No invoice selected.");
            return;
        }

        new Invoice(invoiceNo).setVisible(true);
        dispose();
    }
}
