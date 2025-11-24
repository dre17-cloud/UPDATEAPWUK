package client.gui;

import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import server.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportsFrame extends JFrame {
	private static final long serialVersionUID = 1L;

    private JTabbedPane tabs;
    private JTextArea shipmentsArea;
    private JTextArea performanceArea;
    private JTextArea revenueArea;
    private JTextArea utilizationArea;

    public ReportsFrame() {
    	setTitle("SmartShip - Manager Reports");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        tabs = new JTabbedPane();

        shipmentsArea = buildReportArea();
        performanceArea = buildReportArea();
        revenueArea = buildReportArea();
        utilizationArea = buildReportArea();

        tabs.addTab("Shipments (Daily / Weekly / Monthly)", wrapInScroll(shipmentsArea));
        tabs.addTab("Delivery Performance", wrapInScroll(performanceArea));
        tabs.addTab("Revenue", wrapInScroll(revenueArea));
        tabs.addTab("Vehicle Utilization", wrapInScroll(utilizationArea));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh Reports");
        JButton exportBtn = new JButton("Export Current to PDF");

        refreshBtn.addActionListener(this::refreshReports);
        exportBtn.addActionListener(this::exportCurrentTabToPdf);

        topPanel.add(refreshBtn);
        topPanel.add(exportBtn);

        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        // Load everything on open
        refreshReports(null);
    }

    private JTextArea buildReportArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        return area;
    }

    private JScrollPane wrapInScroll(JTextArea area) {
        JScrollPane sp = new JScrollPane(area);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        return sp;
    }

    // =====================
    // Event Handlers
    // =====================

    private void refreshReports(ActionEvent e) {
        loadShipmentReport();
        loadPerformanceReport();
        loadRevenueReport();
        loadVehicleUtilizationReport();
    }

    private void exportCurrentTabToPdf(ActionEvent e) {
        JTextArea currentArea;

        int idx = tabs.getSelectedIndex();
        if (idx == 0) {
            currentArea = shipmentsArea;
        } else if (idx == 1) {
            currentArea = performanceArea;
        } else if (idx == 2) {
            currentArea = revenueArea;
        } else {
            currentArea = utilizationArea;
        }

        String content = currentArea.getText();
        if (content == null || content.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Nothing to export on this tab.",
                    "Export",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save report as PDF");
        chooser.setSelectedFile(new File("report.pdf"));

        int choice = chooser.showSaveDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        // Ensure it ends with .pdf
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getParentFile(), file.getName() + ".pdf");
        }

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
            JOptionPane.showMessageDialog(this,
                    "Report exported to: " + file.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to export report: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =====================
    // Report Loaders
    // =====================

    /**
     * Daily / Weekly / Monthly shipment counts.
     *
     * Uses invoice "Date Issued" as the shipment date, since each shipment
     * generates a corresponding invoice in this system.
     *
     * Requires table: invoice(`Date Issued`, ...)
     */
    private void loadShipmentReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== SHIPMENTS REPORT ==========\n\n");

        try {
            Connection conn = DatabaseConnection.getConnection();

            // --- Daily Shipments ---
            sb.append(">> Daily Shipments (by invoice date)\n");
            String dailySql =
                    "SELECT DATE(`Date Issued`) AS day, COUNT(*) AS total " +
                    "FROM invoice " +
                    "GROUP BY DATE(`Date Issued`) " +
                    "ORDER BY day DESC";
            try (PreparedStatement ps = conn.prepareStatement(dailySql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    sb.append(String.format("  %s  ->  %d shipments%n",
                            rs.getString("day"),
                            rs.getInt("total")));
                }
            }

            sb.append("\n>> Weekly Shipments (YearWeek of invoice date)\n");
            String weeklySql =
                    "SELECT YEARWEEK(`Date Issued`) AS week, COUNT(*) AS total " +
                    "FROM invoice " +
                    "GROUP BY YEARWEEK(`Date Issued`) " +
                    "ORDER BY week DESC";
            try (PreparedStatement ps = conn.prepareStatement(weeklySql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    sb.append(String.format("  Week %s  ->  %d shipments%n",
                            rs.getString("week"),
                            rs.getInt("total")));
                }
            }

            sb.append("\n>> Monthly Shipments (YYYY-MM of invoice date)\n");
            String monthlySql =
                    "SELECT DATE_FORMAT(`Date Issued`, '%Y-%m') AS month, COUNT(*) AS total " +
                    "FROM invoice " +
                    "GROUP BY DATE_FORMAT(`Date Issued`, '%Y-%m') " +
                    "ORDER BY month DESC";
            try (PreparedStatement ps = conn.prepareStatement(monthlySql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    sb.append(String.format("  %s  ->  %d shipments%n",
                            rs.getString("month"),
                            rs.getInt("total")));
                }
            }

            shipmentsArea.setText(sb.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            shipmentsArea.setText("Error loading shipments report:\n" + ex.getMessage());
        }
    }

    /**
     * Delivery performance: status breakdown.
     *
     * We summarise based on baseshipment.Status.
     * Also show a simple "Delivered vs Not Delivered" view.
     *
     * Requires table: baseshipment(Status, ...)
     */
    private void loadPerformanceReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== DELIVERY PERFORMANCE ==========\n\n");

        int total = 0;
        int delivered = 0;

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql =
                    "SELECT Status, COUNT(*) AS total " +
                    "FROM baseshipment " +
                    "GROUP BY Status";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                sb.append("Status breakdown:\n");
                while (rs.next()) {
                    String status = rs.getString("Status");
                    int count = rs.getInt("total");
                    total += count;
                    if ("Delivered".equalsIgnoreCase(status)) {
                        delivered += count;
                    }
                    sb.append(String.format("  %-12s : %d%n", status, count));
                }
            }

            sb.append("\nOverall summary:\n");
            sb.append("  Total shipments: ").append(total).append("\n");
            sb.append("  Delivered:       ").append(delivered).append("\n");
            sb.append("  Not Delivered:   ").append(total - delivered).append("\n");

            performanceArea.setText(sb.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            performanceArea.setText("Error loading performance report:\n" + ex.getMessage());
        }
    }

    /**
     * Revenue report: sums of billed vs. paid amounts grouped by day.
     *
     * Requires table: invoice(Amount, `Amount Paid`, `Date Issued`, ...)
     */
    private void loadRevenueReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== REVENUE REPORT ==========\n\n");
        sb.append("By invoice date:\n\n");

        double grandBilled = 0.0;
        double grandPaid = 0.0;

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql =
                    "SELECT DATE(`Date Issued`) AS day, " +
                    "       SUM(`Amount`) AS billed, " +
                    "       SUM(`Amount Paid`) AS paid " +
                    "FROM invoice " +
                    "GROUP BY DATE(`Date Issued`) " +
                    "ORDER BY day DESC";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String day = rs.getString("day");
                    double billed = rs.getDouble("billed");
                    double paid = rs.getDouble("paid");

                    grandBilled += billed;
                    grandPaid += paid;

                    sb.append(String.format("  %s  ->  Billed: $%.2f   Paid: $%.2f%n",
                            day, billed, paid));
                }
            }

            sb.append("\nGrand totals:\n");
            sb.append(String.format("  Total Billed: $%.2f%n", grandBilled));
            sb.append(String.format("  Total Paid:   $%.2f%n", grandPaid));
            sb.append(String.format("  Outstanding:  $%.2f%n", (grandBilled - grandPaid)));

            revenueArea.setText(sb.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            revenueArea.setText("Error loading revenue report:\n" + ex.getMessage());
        }
    }

    /**
     * Vehicle utilization: how full each vehicle runs.
     *
     * Expects table `vehicle` to have:
     *  - `Vehicle ID`
     *  - `Max Packages`
     *  - `Assigned Shipments`
     */
    private void loadVehicleUtilizationReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== VEHICLE UTILIZATION ==========\n\n");
        sb.append(String.format("%-12s %-15s %-20s %-15s%n",
                "Vehicle", "Max Packages", "Assigned Shipments", "Utilization"));
        sb.append("---------------------------------------------------------------\n");

        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql =
                    "SELECT `Vehicle ID`, `Max Packages`, `Assigned Shipments` " +
                    "FROM vehicle";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String id = rs.getString("Vehicle ID");
                    int maxPackages = rs.getInt("Max Packages");
                    int assigned = rs.getInt("Assigned Shipments");

                    double utilization = 0.0;
                    if (maxPackages > 0) {
                        utilization = (assigned * 100.0) / maxPackages;
                    }

                    sb.append(String.format("%-12s %-15d %-20d %-14.2f%%%n",
                            id, maxPackages, assigned, utilization));
                }
            }

            utilizationArea.setText(sb.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            utilizationArea.setText("Error loading vehicle utilization report:\n" + ex.getMessage());
        }
    }
}



