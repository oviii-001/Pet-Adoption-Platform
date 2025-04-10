package view;

import controller.PetController;
import model.Pet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class AdminPetPanel extends JPanel {
    private PetController petController;

    private JTable petTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    // Form fields
    private JTextField idField; // Display only, non-editable
    private JTextField nameField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> sizeComboBox;
    private JTextField ageField;
    private JTextArea descriptionArea;
    private JComboBox<String> statusComboBox;
    private JComboBox<String> genderComboBox;
    private JTextField breedField;
    private JTextField healthStatusField;
    private JTextField temperamentField;
    private JButton imageButton;
    private JLabel imagePreview;
    private String selectedImagePath;

    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton; // Clear form fields


    public AdminPetPanel(PetController controller) {
        this.petController = controller;
        setLayout(new BorderLayout(10, 10)); // Add gaps

        // --- Pet List Table ---
        setupTable();

        // --- Pet Details Form ---
        JPanel formPanel = setupFormPanel();

        // --- Button Panel ---
        JPanel buttonPanel = setupButtonPanel();


        // --- Layout ---
        add(scrollPane, BorderLayout.CENTER);

        JPanel eastPanel = new JPanel(new BorderLayout(5,5)); // Panel for form and buttons
        eastPanel.add(formPanel, BorderLayout.NORTH);
        eastPanel.add(buttonPanel, BorderLayout.CENTER); // Buttons below form

        add(eastPanel, BorderLayout.EAST);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding


        // Load initial data
        refreshPetList();

        // Add listener for table row selection
        petTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && petTable.getSelectedRow() != -1) {
                    populateFormFromSelectedRow();
                }
            }
        });
    }

    private void setupTable() {
        String[] columnNames = {"ID", "Name", "Type", "Status"}; // Simpler view for admin list
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable
            }
        };
        petTable = new JTable(tableModel);
        petTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        petTable.getTableHeader().setReorderingAllowed(false);
        scrollPane = new JScrollPane(petTable);
        scrollPane.setPreferredSize(new Dimension(400, 0)); // Give table reasonable initial width
    }

    private JPanel setupFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Pet Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Image Selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Image:"), gbc);
        gbc.gridx = 1;
        JPanel imagePanel = new JPanel(new BorderLayout());
        imageButton = new JButton("Select Image");
        imageButton.addActionListener(e -> selectImage());
        imagePanel.add(imageButton, BorderLayout.WEST);
        
        imagePreview = new JLabel();
        imagePreview.setPreferredSize(new Dimension(100, 100));
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imagePanel.add(imagePreview, BorderLayout.EAST);
        formPanel.add(imagePanel, gbc);

        // ID (Read-only)
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(5);
        idField.setEditable(false);
        formPanel.add(idField, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        nameField = new JTextField(15);
        formPanel.add(nameField, gbc);
        gbc.gridwidth = 1;

        // Type
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        typeComboBox = new JComboBox<>(new String[]{"Dog", "Cat", "Other"});
        formPanel.add(typeComboBox, gbc);

        // Size
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Size:"), gbc);
        gbc.gridx = 1;
        sizeComboBox = new JComboBox<>(new String[]{"Small", "Medium", "Large"});
        formPanel.add(sizeComboBox, gbc);

        // Age
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        ageField = new JTextField(5);
        formPanel.add(ageField, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});
        formPanel.add(genderComboBox, gbc);

        // Breed
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Breed:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        breedField = new JTextField(15);
        formPanel.add(breedField, gbc);
        gbc.gridwidth = 1;

        // Health Status
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Health Status:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        healthStatusField = new JTextField(15);
        formPanel.add(healthStatusField, gbc);
        gbc.gridwidth = 1;

        // Temperament
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Temperament:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        temperamentField = new JTextField(15);
        formPanel.add(temperamentField, gbc);
        gbc.gridwidth = 1;

        // Status
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(new String[]{"available", "adopted", "pending"});
        formPanel.add(statusComboBox, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        descriptionArea = new JTextArea(4, 15);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);

        return formPanel;
    }

    private JPanel setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center buttons
        addButton = new JButton("Add New Pet");
        updateButton = new JButton("Update Selected Pet");
        deleteButton = new JButton("Delete Selected Pet");
        clearButton = new JButton("Clear Form");


        addButton.addActionListener(e -> addPet());
        updateButton.addActionListener(e -> updatePet());
        deleteButton.addActionListener(e -> deletePet());
        clearButton.addActionListener(e -> clearForm());


        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        // Initially disable update/delete until a row is selected
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);


        return buttonPanel;
    }


    public void refreshPetList() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get all pets from controller
        List<Pet> pets = petController.getAllPets();

        // Populate table
        for (Pet pet : pets) {
            Vector<Object> row = new Vector<>();
            row.add(pet.getPetId());
            row.add(pet.getName());
            row.add(pet.getType());
            row.add(pet.getStatus()); // Show status in the list
            tableModel.addRow(row);
        }
        System.out.println("Admin Pet list refreshed.");
        clearForm(); // Clear form when list refreshes
    }


    private void populateFormFromSelectedRow() {
        int selectedRow = petTable.getSelectedRow();
        if (selectedRow >= 0) {
            int petId = (int) tableModel.getValueAt(selectedRow, 0);
            Pet pet = petController.getPetById(petId);

            if (pet != null) {
                idField.setText(String.valueOf(pet.getPetId()));
                nameField.setText(pet.getName());
                typeComboBox.setSelectedItem(pet.getType());
                sizeComboBox.setSelectedItem(pet.getSize());
                ageField.setText(String.valueOf(pet.getAge()));
                descriptionArea.setText(pet.getDescription());
                statusComboBox.setSelectedItem(pet.getStatus());
                genderComboBox.setSelectedItem(pet.getGender());
                breedField.setText(pet.getBreed());
                healthStatusField.setText(pet.getHealthStatus());
                temperamentField.setText(pet.getTemperament());

                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
            } else {
                clearForm();
                JOptionPane.showMessageDialog(this, "Could not find details for selected pet.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            clearForm();
        }
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImagePath = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                // Load and scale the image
                ImageIcon originalIcon = new ImageIcon(selectedImagePath);
                Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imagePreview.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        typeComboBox.setSelectedIndex(0);
        sizeComboBox.setSelectedIndex(0);
        ageField.setText("");
        descriptionArea.setText("");
        statusComboBox.setSelectedItem("available");
        genderComboBox.setSelectedIndex(0);
        breedField.setText("");
        healthStatusField.setText("");
        temperamentField.setText("");
        imagePreview.setIcon(null);
        selectedImagePath = null;

        petTable.clearSelection();
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        nameField.requestFocus();
    }

    private void addPet() {
        try {
            String name = nameField.getText().trim();
            String type = (String) typeComboBox.getSelectedItem();
            String size = (String) sizeComboBox.getSelectedItem();
            int age = Integer.parseInt(ageField.getText().trim());
            String description = descriptionArea.getText().trim();
            String status = (String) statusComboBox.getSelectedItem();
            String gender = (String) genderComboBox.getSelectedItem();
            String breed = breedField.getText().trim();
            String healthStatus = healthStatusField.getText().trim();
            String temperament = temperamentField.getText().trim();

            if (name.isEmpty() || description.isEmpty() || breed.isEmpty() || healthStatus.isEmpty() || temperament.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedImagePath == null) {
                JOptionPane.showMessageDialog(this, "Please select an image for the pet.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Copy image to resources/pets directory
            String imageName = name.toLowerCase().replaceAll("\\s+", "") + ".jpg";
            String targetPath = "resources/pets/" + imageName;
            java.nio.file.Files.copy(
                java.nio.file.Paths.get(selectedImagePath),
                java.nio.file.Paths.get(targetPath),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );

            Pet newPet = new Pet(0, name, type, size, age, description, status, gender, breed, healthStatus, temperament);
            if (petController.addPet(newPet)) {
                JOptionPane.showMessageDialog(this, "Pet added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshPetList();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add pet.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding pet: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void updatePet() {
        try {
            int petId = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String type = (String) typeComboBox.getSelectedItem();
            String size = (String) sizeComboBox.getSelectedItem();
            int age = Integer.parseInt(ageField.getText().trim());
            String description = descriptionArea.getText().trim();
            String status = (String) statusComboBox.getSelectedItem();
            String gender = (String) genderComboBox.getSelectedItem();
            String breed = breedField.getText().trim();
            String healthStatus = healthStatusField.getText().trim();
            String temperament = temperamentField.getText().trim();

            if (name.isEmpty() || description.isEmpty() || breed.isEmpty() || healthStatus.isEmpty() || temperament.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // If new image is selected, copy it to resources/pets directory
            if (selectedImagePath != null) {
                String imageName = name.toLowerCase().replaceAll("\\s+", "") + ".jpg";
                String targetPath = "resources/pets/" + imageName;
                java.nio.file.Files.copy(
                    java.nio.file.Paths.get(selectedImagePath),
                    java.nio.file.Paths.get(targetPath),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
            }

            Pet updatedPet = new Pet(petId, name, type, size, age, description, status, gender, breed, healthStatus, temperament);
            if (petController.updatePet(updatedPet)) {
                JOptionPane.showMessageDialog(this, "Pet updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshPetList();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update pet.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating pet: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void deletePet() {
        int selectedRow = petTable.getSelectedRow();
        if (selectedRow < 0 || idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a pet from the list to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int petId;
        try {
            petId = Integer.parseInt(idField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Pet ID selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Should not happen if ID field is populated correctly
        }
        String petName = nameField.getText(); // Get name for confirmation message

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete pet '" + petName + "' (ID: " + petId + ")?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = petController.deletePet(petId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Pet deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshPetList(); // Refresh table and clear form
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete pet. Check if there are related records (applications) or a database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}