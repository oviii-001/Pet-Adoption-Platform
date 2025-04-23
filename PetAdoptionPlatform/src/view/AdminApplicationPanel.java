package view;

import controller.ApplicationController;
import controller.PetController; 
import model.Application;
import model.Database; 
import model.Pet;
import model.Adopter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class AdminApplicationPanel extends JPanel {
    private ApplicationController applicationController;
    private PetController petController; 
    private MainFrame mainFrame; 

    private JTable applicationTable;
    private DefaultTableModel tableModel;
    private JButton approveButton;
    private JButton rejectButton;
    private JButton refreshButton; 

    public AdminApplicationPanel(ApplicationController appController, PetController petCtrl) {
        this.applicationController = appController;
        this.petController = petCtrl;
       
        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"App ID", "Adopter Name", "Pet Name", "Pet Type", "Current Status", "Address", "Mobile", "Notes"};
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        applicationTable = new JTable(tableModel);
        applicationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        applicationTable.getTableHeader().setReorderingAllowed(false);
        applicationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Set column widths
        applicationTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // App ID
        applicationTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Adopter Name
        applicationTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Pet Name
        applicationTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Pet Type
        applicationTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Status
        applicationTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Address
        applicationTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Mobile
        applicationTable.getColumnModel().getColumn(7).setPreferredWidth(200); // Notes

        // Add listener to enable/disable buttons based on selection and status
        applicationTable.getSelectionModel().addListSelectionListener(e -> updateButtonStates());


        JScrollPane scrollPane = new JScrollPane(applicationTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        approveButton = new JButton("Approve Selected");
        rejectButton = new JButton("Reject Selected");
        refreshButton = new JButton("Refresh List");


        approveButton.addActionListener(e -> updateSelectedApplicationStatus("approved"));
        rejectButton.addActionListener(e -> updateSelectedApplicationStatus("rejected"));
        refreshButton.addActionListener(e -> refreshApplicationList());

        buttonPanel.add(refreshButton);
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initial load
        refreshApplicationList();
        updateButtonStates(); 
    }

    public void refreshApplicationList() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get all applications from controller
        List<Application> applications = applicationController.getAllApplications();

        // Populate table
        for (Application app : applications) {
            // Fetch related details for better display
            Adopter adopter = Database.getAdopterById(app.getAdopterId());
            Pet pet = Database.getPetById(app.getPetId());

            String adopterName = (adopter != null) ? adopter.getName() : "Adopter ID: " + app.getAdopterId();
            String petName = (pet != null) ? pet.getName() : "Pet ID: " + app.getPetId();
            String petType = (pet != null) ? pet.getType() : "N/A";


            Vector<Object> row = new Vector<>();
            row.add(app.getApplicationId());
            row.add(adopterName);
            row.add(petName);
            row.add(petType);
            row.add(app.getStatus());
            row.add(app.getAddress());
            row.add(app.getMobileNumber());
            row.add(app.getNotes());
            tableModel.addRow(row);
        }
        System.out.println("Admin Application list refreshed.");
        updateButtonStates(); // Re-evaluate button states after refresh
    }

    private void updateButtonStates() {
        int selectedRow = applicationTable.getSelectedRow();
        boolean enabled = false;
        if (selectedRow >= 0) {
            // Only enable if the application is currently 'pending'
            String currentStatus = (String) tableModel.getValueAt(selectedRow, 4); // Status column
            if ("pending".equalsIgnoreCase(currentStatus)) {
                enabled = true;
            }
        }
        approveButton.setEnabled(enabled);
        rejectButton.setEnabled(enabled);
    }

    private void updateSelectedApplicationStatus(String newStatus) {
        int selectedRow = applicationTable.getSelectedRow();
        if (selectedRow >= 0) {
            int applicationId = (int) tableModel.getValueAt(selectedRow, 0); // Get App ID
            String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);

            // Double-check if it's still pending before proceeding
            if (!"pending".equalsIgnoreCase(currentStatus)) {
                JOptionPane.showMessageDialog(this, "This application has already been processed.", "Status Changed", JOptionPane.WARNING_MESSAGE);
                refreshApplicationList(); // Refresh list as status might have changed elsewhere
                return;
            }


            boolean success = applicationController.updateApplicationStatus(applicationId, newStatus);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Application #" + applicationId + " status updated to '" + newStatus + "'.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshApplicationList(); 
                
                System.out.println("Triggering potential refresh of Admin Pet Panel");
              

            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update application status. Database error occurred.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select an application from the list.",
                    "No Application Selected", JOptionPane.WARNING_MESSAGE);
        }
    }
}