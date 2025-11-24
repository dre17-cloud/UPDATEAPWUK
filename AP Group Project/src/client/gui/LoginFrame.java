package client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import server.DatabaseConnection;

public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JButton loginBtn, registerBtn;

    public LoginFrame() {
        setTitle("SmartShip - Login");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("SMARTSHIP LOGIN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Email
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        // Password
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        // Role
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleBox = new JComboBox<>(new String[]{"Customer", "Clerk", "Driver", "Manager"});
        panel.add(roleBox, gbc);

        // Buttons
        gbc.gridy = 4;
        gbc.gridx = 0;
        loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(0, 153, 76));
        loginBtn.setForeground(Color.WHITE);
        panel.add(loginBtn, gbc);

        gbc.gridx = 1;
        registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(204, 153, 0));
        registerBtn.setForeground(Color.WHITE);
        panel.add(registerBtn, gbc);

        add(panel);

        setupActions();
    }

    private void setupActions() {

        // Disable register button based on selected role
        roleBox.addActionListener(e -> {
            String selectedRole = roleBox.getSelectedItem().toString();

            // Allow register ONLY for customers
            if (selectedRole.equals("Customer")) {
                registerBtn.setEnabled(true);
            } else {
                registerBtn.setEnabled(false);
            }
        });

        loginBtn.addActionListener(this::handleLogin);

        registerBtn.addActionListener(e ->
                new RegisterCustomerFrame().setVisible(true)
        );
    }
    private void handleLogin(ActionEvent e) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String role = roleBox.getSelectedItem().toString();

        if (email.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
        }

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed!");
            return;
        }

        try {
            // IMPORTANT: user is a reserved SQL keyword → wrap with backticks
            String sql = "SELECT * FROM `user` WHERE email=? AND password=? AND role=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, role);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                // GET THE SENDER NAME FROM DATABASE
                String senderName = rs.getString("name");

                JOptionPane.showMessageDialog(this, "Login successful!");

                dispose(); // close login window

                // PASS SENDER NAME INTO NEXT SCREEN
                switch (role) {
                    case "Customer" -> new CustomerPortal(senderName).setVisible(true);
                    case "Clerk" -> new ClerkPortal().setVisible(true);
                    case "Driver" -> {
                        String driverId = rs.getString("userId");  // <-- get driverId from DB
                        new DriverPortal(driverId).setVisible(true);
                    }
                    case "Manager" -> new ManagerPortal().setVisible(true);
                }

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid login credentials!",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

            stmt.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }
}
