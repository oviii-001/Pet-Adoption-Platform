package view;

import controller.ApplicationController;
import model.Application;
import model.Database; // To get Pet/Adopter names
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
        String[] columnNames = {"App ID", "Pet Name", "Pet Type", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable
            }
        };
        statusTable = new JTable(tableModel);
        statusTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(statusTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Refresh Status");
        refreshButton.addActionListener(e -> loadApplications(mainFrame.getCurrentAdopterId()));
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initial load (can be empty until user navigates here or refreshes)
        // loadApplications(mainFrame.getCurrentAdopterId()); // Load on init? Or wait for navigation? Let's wait.
    }

    public void loadApplications(int adopterId) {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get applications for the current adopter
        List<Application> applications = applicationController.getApplicationsByAdopter(adopterId);

        // Populate table
        for (Application app : applications) {
            // Fetch Pet details for better display
            Pet pet = Database.getPetById(app.getPetId()); // Using Database directly for simplicity here
            String petName = (pet != null) ? pet.getName() : "N/A";
            String petType = (pet != null) ? pet.getType() : "N/A";

            Vector<Object> row = new Vector<>();
            row.add(app.getApplicationId());
            row.add(petName); // Display Pet Name
            row.add(petType); // Display Pet Type
            row.add(app.getStatus());
            tableModel.addRow(row);
        }
        System.out.println("Loaded " + applications.size() + " applications for adopter ID: " + adopterId);
    }
}