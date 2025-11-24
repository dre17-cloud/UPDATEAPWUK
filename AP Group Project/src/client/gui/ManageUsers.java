package client.gui;

import server.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ManageUsers extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextArea outputArea;
    private JButton loadBtn, addBtn, updateBtn, deleteBtn, resetPassBtn;

    public ManageUsers() {
        setTitle("Manage Users");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        // TEXT AREA ONLY IN CENTER
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(outputArea);
        add(scroll, BorderLayout.CENTER);

        // BUTTON PANEL ALONE
        JPanel btnPanel = new JPanel(new FlowLayout());
        loadBtn = new JButton("Load Users");
        addBtn = new JButton("Add User");
        updateBtn = new JButton("Update User");
        deleteBtn = new JButton("Delete User");
        resetPassBtn = new JButton("Reset Password");

        btnPanel.add(loadBtn);
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(resetPassBtn);

        add(btnPanel, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> loadUsers());
        addBtn.addActionListener(e -> addUser());
        updateBtn.addActionListener(e -> updateUser());
        deleteBtn.addActionListener(e -> deleteUser());
        resetPassBtn.addActionListener(e -> resetPassword());
    }

    // -----------------------------------------------------
    // LOAD USERS
    // -----------------------------------------------------
    private void loadUsers() {
        outputArea.setText("");

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM `user`";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            outputArea.append("======= USER LIST =======\n\n");

            while (rs.next()) {
                outputArea.append(
                        "User ID: " + rs.getString("userId") + "\n" +
                        "Name: " + rs.getString("name") + "\n" +
                        "Email: " + rs.getString("email") + "\n" +
                        "Password: " + rs.getString("password") + "\n" +
                        "Role: " + rs.getString("role") + "\n" +
                        "-----------------------------\n"
                );
            }
            ps.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }

    // -----------------------------------------------------
    // ADD USER
    // -----------------------------------------------------
    private void addUser() {

        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField pass = new JTextField();
        JComboBox<String> role = new JComboBox<>(new String[]{"Customer", "Clerk", "Driver", "Manager"});

        Object[] form = {
                "User ID:", id,
                "Name:", name,
                "Email:", email,
                "Password:", pass,
                "Role:", role
        };

        int result = JOptionPane.showConfirmDialog(this, form, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql = """
                    INSERT INTO `user` (userId, name, email, password, role)
                    VALUES (?, ?, ?, ?, ?)
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id.getText());
            ps.setString(2, name.getText());
            ps.setString(3, email.getText());
            ps.setString(4, pass.getText());
            ps.setString(5, role.getSelectedItem().toString());

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(this, "User added successfully!");
            loadUsers();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage());
        }
    }

    // -----------------------------------------------------
    // UPDATE USER — with ID check
    // -----------------------------------------------------
    private void updateUser() {

        String targetId = JOptionPane.showInputDialog(this, "Enter User ID to update:");
        if (targetId == null || targetId.isBlank()) return;

        // Check if ID exists first
        if (!userExists(targetId)) {
            JOptionPane.showMessageDialog(this,
                    "User ID does NOT exist!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField newName = new JTextField();
        JTextField newEmail = new JTextField();
        JTextField newPass = new JTextField();
        JComboBox<String> newRole = new JComboBox<>(new String[]{"Customer", "Clerk", "Driver", "Manager"});

        Object[] form = {
                "New Name:", newName,
                "New Email:", newEmail,
                "New Password:", newPass,
                "New Role:", newRole
        };

        int result = JOptionPane.showConfirmDialog(this, form, "Update User", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql = """
                    UPDATE `user`
                    SET name=?, email=?, password=?, role=?
                    WHERE userId=?
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newName.getText());
            ps.setString(2, newEmail.getText());
            ps.setString(3, newPass.getText());
            ps.setString(4, newRole.getSelectedItem().toString());
            ps.setString(5, targetId);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(this, "User updated successfully!");
            loadUsers();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + e.getMessage());
        }
    }

    // -----------------------------------------------------
    // DELETE USER — with ID check
    // -----------------------------------------------------
    private void deleteUser() {
        String id = JOptionPane.showInputDialog(this, "Enter User ID to delete:");
        if (id == null || id.isBlank()) return;

        // Check if user exists
        if (!userExists(id)) {
            JOptionPane.showMessageDialog(this,
                    "User ID does NOT exist!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql = "DELETE FROM `user` WHERE userId=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(this, "User deleted!");
            loadUsers();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
        }
    }

    // -----------------------------------------------------
    // CHECK IF USER EXISTS
    // -----------------------------------------------------
    private boolean userExists(String userId) {

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql = "SELECT * FROM `user` WHERE userId=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userId);

            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();

            ps.close();
            return exists;

        } catch (SQLException e) {
            return false;
        }
    }
    
 // -----------------------------------------------------
 // RESET USER PASSWORD
 // -----------------------------------------------------
 private void resetPassword() {

     String targetId = JOptionPane.showInputDialog(this, "Enter User ID to reset password:");
     if (targetId == null || targetId.isBlank()) return;

     // Check if ID exists first
     if (!userExists(targetId)) {
         JOptionPane.showMessageDialog(
                 this,
                 "User ID does NOT exist!",
                 "Error",
                 JOptionPane.ERROR_MESSAGE
         );
         return;
     }

     JPasswordField newPassField = new JPasswordField();

     Object[] form = {
             "New Password:", newPassField
     };

     int result = JOptionPane.showConfirmDialog(
             this,
             form,
             "Reset User Password",
             JOptionPane.OK_CANCEL_OPTION
     );

     if (result != JOptionPane.OK_OPTION) return;

     String newPass = new String(newPassField.getPassword());

     if (newPass.isBlank()) {
         JOptionPane.showMessageDialog(this, "Password cannot be empty.");
         return;
     }

     try {
         Connection conn = DatabaseConnection.getConnection();

         String sql = "UPDATE `user` SET password=? WHERE userId=?";
         PreparedStatement ps = conn.prepareStatement(sql);
         ps.setString(1, newPass);
         ps.setString(2, targetId);

         int rows = ps.executeUpdate();
         ps.close();

         if (rows > 0) {
             JOptionPane.showMessageDialog(this, "Password reset successfully!");
             loadUsers(); // refresh display
         } else {
             JOptionPane.showMessageDialog(this, "No rows updated. Something went wrong.");
         }

     } catch (SQLException e) {
         JOptionPane.showMessageDialog(this, "Error resetting password: " + e.getMessage());
     }
 }

    
}
