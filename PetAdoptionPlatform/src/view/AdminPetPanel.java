package view;

import controller.PetController;
import model.Pet;
import model.Application;
import model.Adopter;
import model.Database;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.awt.image.BufferedImage;

public class AdminPetPanel extends JPanel {
    private PetController petController;

    private JTable petTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    // Consistent Colors & Fonts
    private static final Color PRIMARY_COLOR = new Color(60, 90, 160);
    private static final Color SECONDARY_COLOR = new Color(230, 126, 34);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(52, 73, 94);
    private static final Color INPUT_BG_COLOR = new Color(255, 255, 255);
    private static final Color INPUT_BORDER_COLOR = new Color(200, 200, 200);
    private static final Color TABLE_HEADER_BG = new Color(230, 235, 240);
    private static final Color TABLE_GRID_COLOR = new Color(210, 210, 210);
    private static final Color BUTTON_HOVER_COLOR = new Color(45, 75, 140);
    private static final Color DELETE_BUTTON_COLOR = new Color(210, 50, 50);
    private static final Color DELETE_BUTTON_HOVER_COLOR = new Color(180, 40, 40);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color SUCCESS_COLOR_DARKER = new Color(39, 174, 96);

    private static final Font HEADING_FONT = new Font("Segoe UI Semibold", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI Semibold", Font.BOLD, 14);

    // Form fields
    private JTextField idField;
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
    private byte[] selectedImageData;

    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton viewApplicationsButton;

    public AdminPetPanel(PetController controller) {
        this.petController = controller;
        setLayout(new BorderLayout(15, 15)); // Increased gaps
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15)); // Add padding

        // --- Pet List Table Panel ---
        JPanel tablePanel = setupTablePanel();

        // --- Pet Details Form & Buttons Panel ---
        JPanel formAndButtonPanel = setupFormAndButtonPanel();

        // --- Layout ---
        add(tablePanel, BorderLayout.CENTER);
        add(formAndButtonPanel, BorderLayout.EAST);

        // Load initial data
        refreshPetList();

        // Add listener for table row selection
        petTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && petTable.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
            } else if (petTable.getSelectedRow() == -1) {
                updateButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        });
    }

    private JPanel setupTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new RoundedBorder(INPUT_BORDER_COLOR, 1, 15));

        // Create header panel with title and button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(15, 15, 10, 15));

        JLabel tableTitle = new JLabel("Manage Pets");
        tableTitle.setFont(HEADING_FONT);
        tableTitle.setForeground(PRIMARY_COLOR);

        viewApplicationsButton = createStyledButton("View Applications (0)", PRIMARY_COLOR, BUTTON_HOVER_COLOR);
        viewApplicationsButton.addActionListener(e -> showApplications());
        updateApplicationCount(); // Initial count update

        headerPanel.add(tableTitle, BorderLayout.WEST);
        headerPanel.add(viewApplicationsButton, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        setupTable(); // Initializes petTable, tableModel, scrollPane
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void setupTable() {
        String[] columnNames = {"ID", "Name", "Type", "Status", "Age"}; // Added Age
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        petTable = new JTable(tableModel);
        petTable.setFont(TABLE_FONT);
        petTable.setRowHeight(28);
        petTable.setGridColor(TABLE_GRID_COLOR);
        petTable.setShowGrid(true);
        petTable.setIntercellSpacing(new Dimension(0, 0)); // No internal spacing, use grid
        petTable.setSelectionBackground(PRIMARY_COLOR.brighter());
        petTable.setSelectionForeground(Color.WHITE);
        petTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = petTable.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_COLOR);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 35)); // Header height
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, INPUT_BORDER_COLOR));

        scrollPane = new JScrollPane(petTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15)); // Padding around table
        scrollPane.getViewport().setBackground(Color.WHITE); // Background for empty table area
    }

    private JPanel setupFormAndButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false); // Transparent wrapper

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(INPUT_BORDER_COLOR, 1, 15),
            new EmptyBorder(20, 20, 20, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;
        int y = 0;

        // Title
        JLabel formTitle = new JLabel("Pet Details");
        formTitle.setFont(HEADING_FONT);
        formTitle.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(0, 0, 15, 0);
        formPanel.add(formTitle, gbc);
        gbc.anchor = GridBagConstraints.WEST; gbc.insets = new Insets(4, 5, 4, 5); // Reset

        // Fields (Label on left, Field on right)
        idField = createReadOnlyTextField(5);
        addFormRow(formPanel, gbc, y++, "ID:", idField);

        nameField = createStyledTextField(15);
        addFormRow(formPanel, gbc, y++, "Name:", nameField);

        typeComboBox = createStyledComboBox(new String[]{"Dog", "Cat", "Other"});
        addFormRow(formPanel, gbc, y++, "Type:", typeComboBox);

        sizeComboBox = createStyledComboBox(new String[]{"Small", "Medium", "Large"});
        addFormRow(formPanel, gbc, y++, "Size:", sizeComboBox);

        ageField = createStyledTextField(5);
        addFormRow(formPanel, gbc, y++, "Age:", ageField);

        genderComboBox = createStyledComboBox(new String[]{"Male", "Female", "Unknown"});
        addFormRow(formPanel, gbc, y++, "Gender:", genderComboBox);

        breedField = createStyledTextField(15);
        addFormRow(formPanel, gbc, y++, "Breed:", breedField);

        healthStatusField = createStyledTextField(15);
        addFormRow(formPanel, gbc, y++, "Health:", healthStatusField);

        temperamentField = createStyledTextField(15);
        addFormRow(formPanel, gbc, y++, "Temper:", temperamentField);

        statusComboBox = createStyledComboBox(new String[]{"available", "adopted", "pending"});
        addFormRow(formPanel, gbc, y++, "Status:", statusComboBox);

        // Description Area
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(createLabel("Desc:"), gbc);
        gbc.gridx = 1; gbc.gridy = y++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        descriptionArea = new JTextArea(3, 15);
        descriptionArea.setFont(INPUT_FONT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setBorder(new RoundedBorder(INPUT_BORDER_COLOR, 1, 10));
        formPanel.add(descScrollPane, gbc);
        gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;

        // Image Selection Section
        gbc.gridx = 0; gbc.gridy = y; gbc.anchor = GridBagConstraints.NORTHEAST; gbc.insets = new Insets(15, 5, 4, 5); // Add top margin
        formPanel.add(createLabel("Image:"), gbc);

        gbc.gridx = 1; gbc.gridy = y++; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(15, 5, 4, 5);
        JPanel imageSelectionPanel = new JPanel(new BorderLayout(0, 5)); // Vertical gap
        imageSelectionPanel.setOpaque(false);

        imageButton = createStyledButton("Choose Image...", PRIMARY_COLOR, BUTTON_HOVER_COLOR); // Use styled button
        imageButton.setFont(BUTTON_FONT.deriveFont(13f)); // Slightly smaller font
        imageButton.setBorder(new EmptyBorder(6, 12, 6, 12)); // Adjust padding
        imageButton.addActionListener(e -> selectImage());

        imagePreview = new JLabel("Click to select image", SwingConstants.CENTER);
        imagePreview.setFont(INPUT_FONT.deriveFont(Font.ITALIC, 12f));
        imagePreview.setForeground(TEXT_COLOR.darker());
        imagePreview.setPreferredSize(new Dimension(120, 120)); // Larger preview area
        imagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreview.setVerticalAlignment(SwingConstants.CENTER);
        imagePreview.setBorder(BorderFactory.createDashedBorder(INPUT_BORDER_COLOR, 5, 2)); // Dashed border
        imagePreview.setOpaque(true);
        imagePreview.setBackground(INPUT_BG_COLOR);

        imageSelectionPanel.add(imageButton, BorderLayout.NORTH);
        imageSelectionPanel.add(imagePreview, BorderLayout.CENTER);
        formPanel.add(imageSelectionPanel, gbc);

        // --- Button Panel ---
        JPanel buttonPanel = setupButtonPanel();

        // --- Layout Form and Buttons ---
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int y, String labelText, JComponent component) {
        gbc.gridx = 0; gbc.gridy = y;
        gbc.gridwidth = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel(labelText), gbc);

        gbc.gridx = 1; gbc.gridy = y;
        gbc.gridwidth = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(INPUT_FONT);
        textField.setBackground(INPUT_BG_COLOR);
        textField.setForeground(TEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(INPUT_BORDER_COLOR, 1, 10),
            new EmptyBorder(5, 8, 5, 8) // Padding
        ));
        return textField;
    }

    private JTextField createReadOnlyTextField(int columns) {
        JTextField textField = createStyledTextField(columns);
        textField.setEditable(false);
        textField.setBackground(BACKGROUND_COLOR); // Different background for read-only
        return textField;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(INPUT_FONT);
        comboBox.setBackground(INPUT_BG_COLOR);
        comboBox.setForeground(TEXT_COLOR);
        // Basic border for consistency, might need custom renderer for full styling
        comboBox.setBorder(new RoundedBorder(INPUT_BORDER_COLOR, 1, 10));
        return comboBox;
    }

    private JPanel setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE); // Match form background
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, INPUT_BORDER_COLOR)); // Top border

        addButton = createStyledButton("Add New", PRIMARY_COLOR, BUTTON_HOVER_COLOR);
        updateButton = createStyledButton("Update", PRIMARY_COLOR, BUTTON_HOVER_COLOR);
        deleteButton = createStyledButton("Delete", DELETE_BUTTON_COLOR, DELETE_BUTTON_HOVER_COLOR);
        clearButton = createStyledButton("Clear", SECONDARY_COLOR, SECONDARY_COLOR.darker());

        addButton.addActionListener(e -> addPet());
        updateButton.addActionListener(e -> updatePet());
        deleteButton.addActionListener(e -> deletePet());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color background, Color hover) {
         JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bgColor = getBackground();
                if (getModel().isRollover()) {
                    bgColor = hover;
                }
                if (getModel().isArmed()) {
                    bgColor = hover.darker();
                }
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25); // More rounded
                super.paintComponent(g);
                g2.dispose();
            }
        };
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 18, 8, 18));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { button.repaint(); }
        });
        return button;
    }
    
    private void styleSmallButton(JButton button) {
        button.setFont(BUTTON_FONT.deriveFont(12f));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(4, 8, 4, 8));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Add hover later if needed
    }

    public void refreshPetList() {
        tableModel.setRowCount(0);
        List<Pet> pets = petController.getAllPets();
        for (Pet pet : pets) {
            Vector<Object> row = new Vector<>();
            row.add(pet.getPetId());
            row.add(pet.getName());
            row.add(pet.getType());
            row.add(pet.getStatus());
            row.add(pet.getAge()); // Added Age
            tableModel.addRow(row);
        }
         // Clear selection and form after refresh
        petTable.clearSelection();
        clearForm();
        
        // Update application count
        updateApplicationCount();
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = petTable.getSelectedRow();
        if (selectedRow == -1) return;

        int petId = (int) tableModel.getValueAt(selectedRow, 0);
        Pet pet = petController.getPetById(petId);

        if (pet != null) {
            idField.setText(String.valueOf(pet.getPetId()));
            nameField.setText(pet.getName());
            typeComboBox.setSelectedItem(pet.getType());
            sizeComboBox.setSelectedItem(pet.getSize());
            ageField.setText(String.valueOf(pet.getAge()));
            genderComboBox.setSelectedItem(pet.getGender());
            breedField.setText(pet.getBreed());
            healthStatusField.setText(pet.getHealthStatus());
            temperamentField.setText(pet.getTemperament());
            statusComboBox.setSelectedItem(pet.getStatus());
            descriptionArea.setText(pet.getDescription());

            // Set image data from Pet object and update preview
            selectedImageData = pet.getImageData(); // Store the byte array from DB
            updateImagePreviewFromData(selectedImageData); // Use method that takes byte[]

        } else {
            clearForm();
            JOptionPane.showMessageDialog(this, "Could not load details for the selected pet.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateImagePreviewFromData(byte[] imageData) {
        if (imageData != null && imageData.length > 0) {
            try {
                // Load image from byte array using ImageIO
                ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
                BufferedImage originalImage = ImageIO.read(bis);
                
                if (originalImage != null) {
                    // Scale the image
                    Image scaledImage = originalImage.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaledImage);
                    imagePreview.setIcon(icon);
                    imagePreview.setText("");
                    imagePreview.setBorder(new RoundedBorder(INPUT_BORDER_COLOR, 1, 10));
                } else {
                    throw new IOException("Failed to read image data");
                }
            } catch (Exception ex) {
                imagePreview.setIcon(null);
                imagePreview.setText("Preview Error");
                imagePreview.setBorder(BorderFactory.createDashedBorder(INPUT_BORDER_COLOR, 5, 2));
                System.err.println("Error loading image preview from data: " + ex.getMessage());
            }
        } else {
            imagePreview.setIcon(null);
            imagePreview.setText("No Image Set");
            imagePreview.setBorder(BorderFactory.createDashedBorder(INPUT_BORDER_COLOR, 5, 2));
        }
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Pet Image");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files (JPG, PNG, GIF)", "jpg", "png", "gif");
        fileChooser.addChoosableFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(selectedFile); 
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                 
                byte[] buf = new byte[1024];
                int readNum;
                while ((readNum = fis.read(buf)) != -1) {
                    bos.write(buf, 0, readNum);
                }
                selectedImageData = bos.toByteArray(); // Store file content as byte array
                updateImagePreviewFromData(selectedImageData); // Update preview from the new data
            } catch (IOException e) {
                selectedImageData = null;
                updateImagePreviewFromData(null);
                JOptionPane.showMessageDialog(this, "Error reading image file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        typeComboBox.setSelectedIndex(0);
        sizeComboBox.setSelectedIndex(0);
        ageField.setText("");
        genderComboBox.setSelectedIndex(0);
        breedField.setText("");
        healthStatusField.setText("");
        temperamentField.setText("");
        statusComboBox.setSelectedIndex(0);
        descriptionArea.setText("");
        selectedImageData = null; // Clear byte array
        updateImagePreviewFromData(null); // Update preview to show default state
        petTable.clearSelection(); 
    }

    private void addPet() {
        try {
            String name = nameField.getText();
            String type = (String) typeComboBox.getSelectedItem();
            String size = (String) sizeComboBox.getSelectedItem();
            int age = Integer.parseInt(ageField.getText());
            String description = descriptionArea.getText();
            String status = (String) statusComboBox.getSelectedItem();
            String gender = (String) genderComboBox.getSelectedItem();
            String breed = breedField.getText();
            String health = healthStatusField.getText();
            String temper = temperamentField.getText();

            if (name.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Pet name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                 return;
            }

            // Use selectedImageData directly
            Pet newPet = new Pet(0, name, type, size, age, description, status, gender, breed, health, temper, selectedImageData);
            boolean success = petController.addPet(newPet);

            if (success) {
                refreshPetList();
                JOptionPane.showMessageDialog(this, "Pet added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add pet. Check database connection or logs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid age format. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePet() {
        int selectedRow = petTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a pet from the list to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int petId = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            String type = (String) typeComboBox.getSelectedItem();
            String size = (String) sizeComboBox.getSelectedItem();
            int age = Integer.parseInt(ageField.getText());
            String description = descriptionArea.getText();
            String status = (String) statusComboBox.getSelectedItem();
            String gender = (String) genderComboBox.getSelectedItem();
            String breed = breedField.getText();
            String health = healthStatusField.getText();
            String temper = temperamentField.getText();

            // Get existing pet to compare changes
            Pet existingPet = petController.getPetById(petId);
            if (existingPet == null) {
                JOptionPane.showMessageDialog(this, "Could not find the selected pet in the database.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("Comparing changes for pet ID: " + petId);
            System.out.println("Current status: " + existingPet.getStatus() + ", New status: " + status);

            // If only status is changed, use updatePetStatus
            if (name.equals(existingPet.getName()) &&
                type.equals(existingPet.getType()) &&
                size.equals(existingPet.getSize()) &&
                age == existingPet.getAge() &&
                description.equals(existingPet.getDescription()) &&
                gender.equals(existingPet.getGender()) &&
                breed.equals(existingPet.getBreed()) &&
                health.equals(existingPet.getHealthStatus()) &&
                temper.equals(existingPet.getTemperament()) &&
                !status.equals(existingPet.getStatus())) {
                
                System.out.println("Only status changed, using updatePetStatus");
                boolean success = petController.updatePetStatus(petId, status);
                if (success) {
                    refreshPetList();
                    JOptionPane.showMessageDialog(this, "Pet status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update pet status. Check database connection or logs.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }

            System.out.println("Multiple fields changed, using full update");
            // For other changes, use the full update
            byte[] imageDataToSave = null;
            if (selectedImageData != null) {
                imageDataToSave = selectedImageData;
            } else if (existingPet != null) {
                imageDataToSave = existingPet.getImageData();
            }

            Pet updatedPet = new Pet(petId, name, type, size, age, description, status, gender, breed, health, temper, imageDataToSave);
            boolean success = petController.updatePet(updatedPet);

            if (success) {
                refreshPetList();
                JOptionPane.showMessageDialog(this, "Pet updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update pet. Check database connection or logs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid age format. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePet() {
        int selectedRow = petTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a pet from the list to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int petId = (int) tableModel.getValueAt(selectedRow, 0);
        String petName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete pet: " + petName + " (ID: " + petId + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
             boolean success = petController.deletePet(petId);
             if (success) {
                 refreshPetList(); // Refresh list and clear form
                 JOptionPane.showMessageDialog(this, "Pet deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
             } else {
                 JOptionPane.showMessageDialog(this, "Failed to delete pet. Check database connection or logs.", "Error", JOptionPane.ERROR_MESSAGE);
             }
        }
    }

    private int getPendingApplicationCount() {
        List<Application> applications = Database.getAllApplications();
        int pendingCount = 0;
        for (Application app : applications) {
            if ("pending".equalsIgnoreCase(app.getStatus())) {
                pendingCount++;
            }
        }
        return pendingCount;
    }

    private void updateApplicationCount() {
        int count = getPendingApplicationCount();
        viewApplicationsButton.setText("View Applications (" + count + ")");
    }

    private void showApplications() {
        int pendingCount = getPendingApplicationCount();
        if (pendingCount == 0) {
            JOptionPane.showMessageDialog(this, "No pending applications found.", "Applications", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create a dialog to show applications
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Pending Applications", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(this);

        // Create main panel with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(3);
        splitPane.setBackground(Color.WHITE);

        // Left panel - Application list
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create table model for application list
        String[] columnNames = {"App ID", "Pet Name", "Adopter Name"};
        DefaultTableModel listModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table for application list
        JTable listTable = new JTable(listModel);
        listTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listTable.getTableHeader().setReorderingAllowed(false);
        listTable.setRowHeight(30);
        listTable.setFont(TABLE_FONT);
        listTable.getTableHeader().setFont(TABLE_HEADER_FONT);
        listTable.setGridColor(TABLE_GRID_COLOR);
        listTable.setSelectionBackground(PRIMARY_COLOR.brighter());
        listTable.setSelectionForeground(Color.WHITE);

        // Set column widths
        listTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // App ID
        listTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Pet Name
        listTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Adopter Name

        JScrollPane listScrollPane = new JScrollPane(listTable);
        listPanel.add(listScrollPane, BorderLayout.CENTER);

        // Right panel - Application details
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create details form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        int y = 0;

        // Application Details Section
        JLabel appDetailsLabel = new JLabel("Application Details");
        appDetailsLabel.setFont(HEADING_FONT);
        appDetailsLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2; gbc.insets = new Insets(10, 0, 15, 0);
        formPanel.add(appDetailsLabel, gbc);
        gbc.gridwidth = 1; gbc.insets = new Insets(5, 5, 5, 5);

        // Application ID
        JLabel appIdLabel = createLabel("Application ID:");
        JTextField appIdField = createReadOnlyTextField(10);
        addFormRow(formPanel, gbc, y++, "Application ID:", appIdField);

        // Pet Details Section
        JLabel petDetailsLabel = new JLabel("Pet Details");
        petDetailsLabel.setFont(HEADING_FONT);
        petDetailsLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2; gbc.insets = new Insets(15, 0, 10, 0);
        formPanel.add(petDetailsLabel, gbc);
        gbc.gridwidth = 1; gbc.insets = new Insets(5, 5, 5, 5);

        // Pet fields
        JTextField petNameField = createReadOnlyTextField(20);
        JTextField petTypeField = createReadOnlyTextField(15);
        JTextField petBreedField = createReadOnlyTextField(15);
        JTextField petAgeField = createReadOnlyTextField(5);
        JTextArea petDescArea = new JTextArea(3, 20);
        petDescArea.setEditable(false);
        petDescArea.setFont(INPUT_FONT);
        petDescArea.setLineWrap(true);
        petDescArea.setWrapStyleWord(true);
        JScrollPane petDescScroll = new JScrollPane(petDescArea);

        addFormRow(formPanel, gbc, y++, "Pet Name:", petNameField);
        addFormRow(formPanel, gbc, y++, "Type:", petTypeField);
        addFormRow(formPanel, gbc, y++, "Breed:", petBreedField);
        addFormRow(formPanel, gbc, y++, "Age:", petAgeField);
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = y++; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(petDescScroll, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // Adopter Details Section
        JLabel adopterDetailsLabel = new JLabel("Adopter Details");
        adopterDetailsLabel.setFont(HEADING_FONT);
        adopterDetailsLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2; gbc.insets = new Insets(15, 0, 10, 0);
        formPanel.add(adopterDetailsLabel, gbc);
        gbc.gridwidth = 1; gbc.insets = new Insets(5, 5, 5, 5);

        // Adopter fields
        JTextField adopterNameField = createReadOnlyTextField(20);
        JTextField contactField = createReadOnlyTextField(20);
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setEditable(false);
        addressArea.setFont(INPUT_FONT);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        JTextField mobileField = createReadOnlyTextField(15);
        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setEditable(false);
        notesArea.setFont(INPUT_FONT);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);

        addFormRow(formPanel, gbc, y++, "Name:", adopterNameField);
        addFormRow(formPanel, gbc, y++, "Contact:", contactField);
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(createLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridy = y++; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(addressScroll, gbc);
        gbc.fill = GridBagConstraints.NONE;
        addFormRow(formPanel, gbc, y++, "Mobile:", mobileField);
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(createLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.gridy = y++; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(notesScroll, gbc);
        gbc.fill = GridBagConstraints.NONE;

        JScrollPane formScrollPane = new JScrollPane(formPanel);
        detailsPanel.add(formScrollPane, BorderLayout.CENTER);

        // Add buttons panel at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton approveButton = createStyledButton("Approve", SUCCESS_COLOR, SUCCESS_COLOR_DARKER);
        JButton rejectButton = createStyledButton("Reject", DELETE_BUTTON_COLOR, DELETE_BUTTON_HOVER_COLOR);
        JButton closeButton = createStyledButton("Close", SECONDARY_COLOR, SECONDARY_COLOR.darker());

        approveButton.addActionListener(e -> {
            int selectedRow = listTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select an application to approve.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int appId = (int) listModel.getValueAt(selectedRow, 0);
            try {
                if (Database.updateApplicationStatus(appId, "approved")) {
                    listModel.removeRow(selectedRow);
                    updateApplicationCount();
                    JOptionPane.showMessageDialog(dialog, "Application approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to approve application.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        rejectButton.addActionListener(e -> {
            int selectedRow = listTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select an application to reject.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int appId = (int) listModel.getValueAt(selectedRow, 0);
            try {
                if (Database.updateApplicationStatus(appId, "rejected")) {
                    listModel.removeRow(selectedRow);
                    updateApplicationCount();
                    JOptionPane.showMessageDialog(dialog, "Application rejected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to reject application.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(closeButton);
        detailsPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add panels to split pane
        splitPane.setLeftComponent(listPanel);
        splitPane.setRightComponent(detailsPanel);

        // Add split pane to dialog
        dialog.add(splitPane, BorderLayout.CENTER);

        // Add applications to list table
        List<Application> applications = Database.getAllApplications();
        for (Application app : applications) {
            if ("pending".equalsIgnoreCase(app.getStatus())) {
                Pet pet = Database.getPetById(app.getPetId());
                Adopter adopter = Database.getAdopterById(app.getAdopterId());
                
                Vector<Object> row = new Vector<>();
                row.add(app.getApplicationId());
                row.add(pet != null ? pet.getName() : "N/A");
                row.add(adopter != null ? adopter.getName() : "N/A");
                listModel.addRow(row);
            }
        }

        // Add selection listener to update details
        listTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listTable.getSelectedRow() != -1) {
                int selectedRow = listTable.getSelectedRow();
                int appId = (int) listModel.getValueAt(selectedRow, 0);
                Application app = Database.getApplicationById(appId);
                if (app != null) {
                    Pet pet = Database.getPetById(app.getPetId());
                    Adopter adopter = Database.getAdopterById(app.getAdopterId());

                    // Update application details
                    appIdField.setText(String.valueOf(app.getApplicationId()));

                    // Update pet details
                    if (pet != null) {
                        petNameField.setText(pet.getName());
                        petTypeField.setText(pet.getType());
                        petBreedField.setText(pet.getBreed());
                        petAgeField.setText(String.valueOf(pet.getAge()));
                        petDescArea.setText(pet.getDescription());
                    } else {
                        petNameField.setText("N/A");
                        petTypeField.setText("N/A");
                        petBreedField.setText("N/A");
                        petAgeField.setText("N/A");
                        petDescArea.setText("N/A");
                    }

                    // Update adopter details
                    if (adopter != null) {
                        adopterNameField.setText(adopter.getName());
                        contactField.setText(adopter.getContactInfo());
                        // Fetch address, mobile, notes from Application object
                        addressArea.setText(app.getAddress());
                        mobileField.setText(app.getMobileNumber());
                        notesArea.setText(app.getNotes());
                    } else {
                        adopterNameField.setText("N/A");
                        contactField.setText("N/A");
                        addressArea.setText("N/A"); // Clear if adopter not found
                        mobileField.setText("N/A"); // Clear if adopter not found
                        notesArea.setText("N/A");   // Clear if adopter not found
                    }
                }
            }
        });

        dialog.setVisible(true);
    }

    // Helper class for rounded borders (ensure it's defined or accessible)
    private static class RoundedBorder implements Border {
        private Color color;
        private int thickness;
        private int radius;

        public RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.draw(new RoundRectangle2D.Double(x + thickness / 2.0, y + thickness / 2.0,
                                                width - thickness, height - thickness, radius, radius));
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }
    }
}