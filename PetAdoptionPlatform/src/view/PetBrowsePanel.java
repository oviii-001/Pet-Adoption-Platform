package view;

import controller.PetController;
import model.Pet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class PetBrowsePanel extends JPanel {
    private PetController petController;
    private MainFrame mainFrame; // Reference to MainFrame for navigation

    private JTable petTable;
    private DefaultTableModel tableModel;
    private JButton applyButton;
    private JButton refreshButton; // Added refresh button

    public PetBrowsePanel(PetController controller, MainFrame frame) {
        this.petController = controller;
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"ID", "Name", "Type", "Size", "Age", "Description", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        petTable = new JTable(tableModel);
        petTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only one pet at a time
        petTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering


        JScrollPane scrollPane = new JScrollPane(petTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        applyButton = new JButton("Apply for Selected Pet");
        refreshButton = new JButton("Refresh List");


        applyButton.addActionListener(e -> applyForSelectedPet());
        refreshButton.addActionListener(e -> refreshPetList());

        buttonPanel.add(refreshButton);
        buttonPanel.add(applyButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load initial data
        refreshPetList();
    }

    public void refreshPetList() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get available pets from controller
        List<Pet> pets = petController.getAvailablePets(); // Use the new method

        // Populate table
        for (Pet pet : pets) {
            Vector<Object> row = new Vector<>();
            row.add(pet.getPetId());
            row.add(pet.getName());
            row.add(pet.getType());
            row.add(pet.getSize());
            row.add(pet.getAge());
            row.add(pet.getDescription());
            row.add(pet.getStatus());
            tableModel.addRow(row);
        }
        System.out.println("Pet list refreshed. Found " + pets.size() + " available pets.");
    }

    private void applyForSelectedPet() {
        int selectedRow = petTable.getSelectedRow();
        if (selectedRow >= 0) {
            int petId = (int) tableModel.getValueAt(selectedRow, 0); // Get ID from first column
            String status = (String) tableModel.getValueAt(selectedRow, 6); // Get status

            if ("available".equalsIgnoreCase(status)) {
                System.out.println("Navigating to apply for Pet ID: " + petId);
                mainFrame.navigateToApplicationForm(petId);
            } else {
                JOptionPane.showMessageDialog(this,
                        "This pet is not available for adoption.",
                        "Pet Not Available", JOptionPane.WARNING_MESSAGE);
                refreshPetList(); // Refresh in case status changed elsewhere
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select a pet from the list to apply for.",
                    "No Pet Selected", JOptionPane.WARNING_MESSAGE);
        }
    }
}