package view;

import controller.ApplicationController;
import model.Application;
import model.Database;
import model.Pet;
import model.Adopter;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class StatusPanel extends JPanel {
    private ApplicationController applicationController;
    private MainFrame mainFrame;

    private JTable statusTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public StatusPanel(ApplicationController controller, MainFrame frame) {
        this.applicationController = controller;
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"App ID", "Pet Name", "Pet Type", "Status", "Address", "Mobile", "Notes"};
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        statusTable = new JTable(tableModel);
        statusTable.getTableHeader().setReorderingAllowed(false);
        statusTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Set column widths
        statusTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // App ID
        statusTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Pet Name
        statusTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Pet Type
        statusTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Status
        statusTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Address
        statusTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Mobile
        statusTable.getColumnModel().getColumn(6).setPreferredWidth(200); // Notes

        JScrollPane scrollPane = new JScrollPane(statusTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Refresh Status");
        refreshButton.addActionListener(e -> loadApplications(mainFrame.getCurrentAdopterId()));
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

    }

    public void loadApplications(int adopterId) {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get applications for the current adopter
        List<Application> applications = applicationController.getApplicationsByAdopter(adopterId);

        // Populate table
        for (Application app : applications) {
            // Fetch Pet details for better display
            Pet pet = Database.getPetById(app.getPetId());
            String petName = (pet != null) ? pet.getName() : "N/A";
            String petType = (pet != null) ? pet.getType() : "N/A";

            Vector<Object> row = new Vector<>();
            row.add(app.getApplicationId());
            row.add(petName);
            row.add(petType);
            row.add(app.getStatus());
            row.add(app.getAddress());
            row.add(app.getMobileNumber());
            row.add(app.getNotes());
            tableModel.addRow(row);
        }
        System.out.println("Loaded " + applications.size() + " applications for adopter ID: " + adopterId);
    }
}