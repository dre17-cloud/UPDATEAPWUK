
package client.gui;

import server.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TrackShipment extends JFrame {

    private JTextArea textArea;
    private String senderName;

    public TrackShipment(String senderName) {
        this.senderName = senderName;

        setSize(600, 400);
        setTitle("Track My Shipments");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("My Shipments", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(textArea);
        add(scroll, BorderLayout.CENTER);

        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            new CustomerPortal(senderName).setVisible(true);
            dispose();
        });

        JPanel bottom = new JPanel();
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);

        loadShipments();
        setVisible(true);
    }

    private void loadShipments() {
        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql = "SELECT `Tracking Number`, `Destination`, `Status`, `Cost` " +
                         "FROM baseshipment WHERE `Sender Name` = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, senderName);

            ResultSet rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder();
            sb.append("Your Shipments:\n\n");

            boolean found = false;

            while (rs.next()) {
                found = true;

                sb.append("Tracking Number: ").append(rs.getString("Tracking Number")).append("\n");
                sb.append("Destination: ").append(rs.getString("Destination")).append("\n");
                sb.append("Status: ").append(rs.getString("Status")).append("\n");
                sb.append("Cost: ").append(rs.getString("Cost")).append("\n");
                sb.append("-----------------------------------------\n");
            }

            if (!found) {
                sb.append("No shipments found for you.");
            }

            textArea.setText(sb.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            textArea.setText("Error loading shipments.");
        }
    }
}

