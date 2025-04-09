package view;

import controller.PetController;
import model.Adopter;
import model.Database;
import model.Pet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class MatchedPetsPanel extends JPanel {
    private PetController petController;
    private MainFrame mainFrame;

    private JTable matchedPetsTable;
    private DefaultTableModel tableModel;
    private JButton applyButton; // Allow applying from matches
    private JButton backButton;  // Go back to preferences or browse


    public MatchedPetsPanel(PetController controller, MainFrame frame) {
        this.petController = controller;
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Pets Matching Your Preferences", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Table Setup
        String[] columnNames = {"ID", "Name", "Type", "Size", "Age", "Description"}; // Status is implied 'available'
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable
            }
        };
        matchedPetsTable = new JTable(tableModel);
        matchedPetsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        matchedPetsTable.getTableHeader().setReorderingAllowed(false);


        JScrollPane scrollPane = new JScrollPane(matchedPetsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        applyButton = new JButton("Apply for Selected Pet");
        backButton = new JButton("Back to Preferences");

        applyButton.addActionListener(e -> applyForSelectedPet());
        backButton.addActionListener(e -> mainFrame.showPanel("SetPreferences")); // Go back to prefs screen

        buttonPanel.add(backButton);
        buttonPanel.add(applyButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadMatchedPets(int adopterId) {
        tableModel.setRowCount(0); // Clear previous matches

        Adopter adopter = Database.getAdopterById(adopterId);
        if (adopter == null || adopter.getPreferences() == null || adopter.getPreferences().isEmpty()) {
            System.out.println("No preferences set for adopter " + adopterId + ". Cannot find matches.");
            // Optionally show a message in the panel itself
            // tableModel.addRow(new Vector<>(List.of("N/A", "Please set preferences first.", "", "", "", "")));
            return;
        }

        String preferences = adopter.getPreferences();
        System.out.println("Finding matches for adopter " + adopterId + " with preferences: " + preferences);
        List<Pet> matchedPets = petController.findMatchingPets(preferences);

        if (matchedPets.isEmpty()) {
            System.out.println("No pets found matching the criteria.");
            // Optionally show a message in the table
            // tableModel.addRow(new Vector<>(List.of("N/A", "No matching pets found.", "", "", "", "")));
        } else {
            for (Pet pet : matchedPets) {
                if("available".equalsIgnoreCase(pet.getStatus())) { // Only show available pets
                    Vector<Object> row = new Vector<>();
                    row.add(pet.getPetId());
                    row.add(pet.getName());
                    row.add(pet.getType());
                    row.add(pet.getSize());
                    row.add(pet.getAge());
                    row.add(pet.getDescription());
                    tableModel.addRow(row);
                }
            }
            System.out.println("Found " + tableModel.getRowCount() + " matching available pets.");
        }
    }

    public void clearMatchedPets() {
        tableModel.setRowCount(0);
    }

    private void applyForSelectedPet() {
        int selectedRow = matchedPetsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int petId = (int) tableModel.getValueAt(selectedRow, 0); // Get ID from first column
            System.out.println("Navigating to apply for matched Pet ID: " + petId);
            mainFrame.navigateToApplicationForm(petId);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select a pet from the list to apply for.",
                    "No Pet Selected", JOptionPane.WARNING_MESSAGE);
        }
    }
}