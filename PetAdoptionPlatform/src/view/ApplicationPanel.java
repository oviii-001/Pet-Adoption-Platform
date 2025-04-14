package view;

import controller.ApplicationController;
import model.Application;
import model.Pet;
import model.Adopter;
import model.Database;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.sql.SQLException;

public class ApplicationPanel extends JPanel {
    private ApplicationController applicationController;
    private MainFrame mainFrame;

    private JTextField adopterNameField;
    private JTextField contactInfoField;
    private JTextArea addressField;
    private JTextField mobileNumberField;
    private JTextArea notesField;
    private JLabel petInfoLabel;
    private JButton submitButton;
    private JButton cancelButton;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);    // Indigo
    private static final Color SECONDARY_COLOR = new Color(99, 102, 241);  // Lighter indigo
    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Cool gray
    private static final Color TEXT_COLOR = new Color(31, 41, 55);         // Dark gray
    private static final Color BORDER_COLOR = new Color(229, 231, 235);    // Light gray
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);    // Green
    private static final Color FIELD_BACKGROUND = Color.WHITE;
    private static final Color MANDATORY_BORDER_COLOR = new Color(255, 99, 71);
    private static final String REQUIRED_FIELD_INDICATOR = " *";
    
    // Modern fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private int currentPetId = -1;
    private Pet currentPet = null;

    public ApplicationPanel(ApplicationController controller, MainFrame frame) {
        this.applicationController = controller;
        this.mainFrame = frame;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Panel with Pet Info
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center Panel with Form
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel with Buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Add component listener for resize events
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                revalidate();
                repaint();
            }
        });

        // Load potential default adopter info
        loadAdopterInfo(mainFrame.getCurrentAdopterId());
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        petInfoLabel = new JLabel("Applying for Pet ID: [Select from Browse]");
        petInfoLabel.setFont(TITLE_FONT);
        petInfoLabel.setForeground(TEXT_COLOR);
        panel.add(petInfoLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Form fields
        addFormField(panel, "Name" + REQUIRED_FIELD_INDICATOR, adopterNameField = createTextField(), gbc, row++);
        addFormField(panel, "Email" + REQUIRED_FIELD_INDICATOR, contactInfoField = createTextField(), gbc, row++);
        addFormField(panel, "Mobile Number" + REQUIRED_FIELD_INDICATOR, mobileNumberField = createTextField(), gbc, row++);
        addFormField(panel, "Address" + REQUIRED_FIELD_INDICATOR, addressField = createTextArea(), gbc, row++);
        addFormField(panel, "Notes (Optional):", notesField = createTextArea(), gbc, row++);
        
        // Add empty space at bottom
        gbc.weighty = 1.0;
        gbc.gridy = row;
        panel.add(Box.createVerticalGlue(), gbc);
        
        return panel;
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        // Label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        panel.add(label, gbc);

        // Field
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        if (field instanceof JTextArea) {
            JScrollPane scrollPane = new JScrollPane(field);
            scrollPane.setPreferredSize(new Dimension(0, 100));
            panel.add(scrollPane, gbc);
        } else {
            panel.add(field, gbc);
        }
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(FIELD_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(FIELD_BACKGROUND);
        return field;
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(FIELD_FONT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        area.setBackground(FIELD_BACKGROUND);
        return area;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        submitButton = new JButton("Submit Application");
        submitButton.setFont(BUTTON_FONT);
        submitButton.setBackground(PRIMARY_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        submitButton.addActionListener(e -> submitApplication());

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(BUTTON_FONT);
        cancelButton.setBackground(BACKGROUND_COLOR);
        cancelButton.setForeground(PRIMARY_COLOR);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cancelButton.addActionListener(e -> mainFrame.showPanel("PetBrowse"));

        panel.add(submitButton);
        panel.add(cancelButton);

        return panel;
    }

    // Method to pre-fill adopter info if possible
    private void loadAdopterInfo(int adopterId) {
        Adopter adopter = Database.getAdopterById(adopterId);
        if (adopter != null) {
            adopterNameField.setText(adopter.getName());
            contactInfoField.setText(adopter.getContactInfo());
            addressField.setText(adopter.getAddress());
            mobileNumberField.setText(adopter.getMobileNumber());
            notesField.setText(adopter.getNotes());
        } else {
            // Clear fields if adopter not found (or handle error)
            adopterNameField.setText("");
            contactInfoField.setText("");
            addressField.setText("");
            mobileNumberField.setText("");
            notesField.setText("");
        }
    }

    // Called by MainFrame when navigating here
    public void setPetForApplication(Pet pet) {
        this.currentPet = pet;
        if (this.currentPet != null) {
            this.currentPetId = this.currentPet.getPetId();
            petInfoLabel.setText("Applying for: " + this.currentPet.getName() +
                               " (ID: " + this.currentPet.getPetId() +
                               ", Type: " + this.currentPet.getType() + ")");
        } else {
            this.currentPetId = -1;
            petInfoLabel.setText("Applying for Pet ID: [Error - Pet not provided]");
        }
        // Re-load adopter info when setting a pet
        loadAdopterInfo(mainFrame.getCurrentAdopterId());
    }

    private void submitApplication() {
        if (currentPet == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a pet before submitting the application.",
                "No Pet Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate required fields
        if (adopterNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Name is a required field. Please enter your full name.",
                "Required Field Missing",
                JOptionPane.WARNING_MESSAGE);
            adopterNameField.requestFocus();
            return;
        }

        if (contactInfoField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Email is a required field. Please enter a valid email address.",
                "Required Field Missing",
                JOptionPane.WARNING_MESSAGE);
            contactInfoField.requestFocus();
            return;
        }

        if (mobileNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Mobile number is a required field. Please enter a valid contact number.",
                "Required Field Missing",
                JOptionPane.WARNING_MESSAGE);
            mobileNumberField.requestFocus();
            return;
        }

        if (addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Address is a required field. Please enter your complete address.",
                "Required Field Missing",
                JOptionPane.WARNING_MESSAGE);
            addressField.requestFocus();
            return;
        }

        // --- Add/Update Adopter Logic ---
        int adopterId = mainFrame.getCurrentAdopterId();
        Adopter adopter = null;
        boolean isNewAdopter = false;

        try {
            if (adopterId > 0) {
                // Try to get existing adopter
                adopter = Database.getAdopterById(adopterId);
                if (adopter != null) {
                    // Update existing adopter's info
                    adopter.setName(adopterNameField.getText().trim());
                    adopter.setContactInfo(contactInfoField.getText().trim());
                    adopter.setAddress(addressField.getText().trim()); // Update address in Adopter table too
                    adopter.setMobileNumber(mobileNumberField.getText().trim()); // Update mobile in Adopter table too
                    adopter.setNotes(notesField.getText().trim()); // Update notes in Adopter table too
                    Database.updateAdopter(adopter);
                } else {
                    // Adopter ID exists but not found in DB? Error or create new.
                    // For simplicity, let's assume we create a new one if ID is invalid
                    adopterId = -1; // Reset ID to force creation
                }
            }

            if (adopterId <= 0) { 
                // Create new adopter
                isNewAdopter = true;
                adopter = new Adopter(0, adopterNameField.getText().trim(), contactInfoField.getText().trim(), "", addressField.getText().trim(), mobileNumberField.getText().trim(), notesField.getText().trim());
                adopterId = Database.addAdopter(adopter);
                if (adopterId > 0) {
                    // mainFrame.setCurrentAdopterId(adopterId); // Remove this line
                } else {
                    throw new SQLException("Failed to create new adopter record.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error while saving adopter details: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return; // Stop submission if adopter saving failed
        }
        // --- End Adopter Logic ---

        // Now create and submit the application with the correct adopter ID
        Application application = new Application(adopterId, currentPet.getPetId());
        application.setAddress(addressField.getText().trim()); // Keep these for the application record
        application.setMobileNumber(mobileNumberField.getText().trim());
        application.setNotes(notesField.getText().trim());

        boolean success = applicationController.submitApplication(application);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Application submitted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFormFields(); // Clear the form after successful submission
            mainFrame.showPanel("PetBrowse"); // Go back to browsing
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to submit application. Please try again later or contact support.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFormFields() {
        // Clear fields, but maybe keep name/contact if user is "logged in"?
        // For now, let's clear everything related to the application itself.
        // Keep adopterNameField and contactInfoField if mainFrame.getCurrentAdopterId() > 0?
        // Let's clear all for simplicity now.
        adopterNameField.setText("");
        contactInfoField.setText("");
        addressField.setText("");
        mobileNumberField.setText("");
        notesField.setText("");
        petInfoLabel.setText("Applying for Pet ID: [Select from Browse]");
        currentPet = null;
        currentPetId = -1;
    }

    // Custom rounded border class
    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }
    }

    // Custom shadow border class
    private static class ShadowBorder extends AbstractBorder {
        private static final int SHADOW_SIZE = 3;
        private static final Color SHADOW_COLOR = new Color(0, 0, 0, 20);

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw shadow
            for (int i = 0; i < SHADOW_SIZE; i++) {
                g2.setColor(new Color(SHADOW_COLOR.getRed(), 
                                    SHADOW_COLOR.getGreen(),
                                    SHADOW_COLOR.getBlue(),
                                    SHADOW_COLOR.getAlpha() / (i + 1)));
                g2.drawRoundRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1, 10, 10);
            }
            
            // Draw main border
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(x, y, width - 1, height - 1, 10, 10);
            
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(SHADOW_SIZE + 1, SHADOW_SIZE + 1, SHADOW_SIZE + 1, SHADOW_SIZE + 1);
        }
    }
}