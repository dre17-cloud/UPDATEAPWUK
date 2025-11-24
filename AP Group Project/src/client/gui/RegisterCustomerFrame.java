package client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import server.DatabaseConnection;

public class RegisterCustomerFrame extends JFrame {

  
	private static final long serialVersionUID = 1L;
	private JTextField userIdField, nameField, emailField;
    private JPasswordField passwordField;
    private JButton registerBtn, cancelBtn;

    public RegisterCustomerFrame() {
        setTitle("Register New Customer");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Customer Registration", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        // USER ID
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        userIdField = new JTextField(20);
        panel.add(userIdField, gbc);

        // NAME
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // EMAIL
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        // PASSWORD
        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        // BUTTONS
        gbc.gridy = 5;
        gbc.gridx = 0;
        registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(0, 153, 76));
        registerBtn.setForeground(Color.WHITE);
        panel.add(registerBtn, gbc);

        gbc.gridx = 1;
        cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(153, 0, 0));
        cancelBtn.setForeground(Color.WHITE);
        panel.add(cancelBtn, gbc);

        add(panel);

        registerBtn.addActionListener(this::registerCustomer);
        cancelBtn.addActionListener(e -> dispose());
    }

    private void registerCustomer(ActionEvent e) {
        String userId = userIdField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = "Customer";

        if (userId.isBlank() || name.isBlank() || email.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed!");
            return;
        }

        try {
            // CHECK IF USER ID ALREADY EXISTS
            PreparedStatement checkId = conn.prepareStatement(
                    "SELECT * FROM `user` WHERE userId=?");
            checkId.setString(1, userId);
            ResultSet idRS = checkId.executeQuery();

            if (idRS.next()) {
                JOptionPane.showMessageDialog(this,
                        "User ID already exists! Choose another.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // CHECK IF EMAIL ALREADY EXISTS
            PreparedStatement checkEmail = conn.prepareStatement(
                    "SELECT * FROM `user` WHERE email=?");
            checkEmail.setString(1, email);
            ResultSet emailRS = checkEmail.executeQuery();

            if (emailRS.next()) {
                JOptionPane.showMessageDialog(this,
                        "Email already registered!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // INSERT NEW CUSTOMER
            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO `user` (userId, name, email, password, role) VALUES (?, ?, ?, ?, ?)");

            insert.setString(1, userId);
            insert.setString(2, name);
            insert.setString(3, email);
            insert.setString(4, password);
            insert.setString(5, role);

            insert.executeUpdate();

            JOptionPane.showMessageDialog(this, "Customer registered successfully!");
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
