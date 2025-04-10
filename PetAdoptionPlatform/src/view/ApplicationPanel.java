package view;

import controller.ApplicationController;
import model.Application;

import javax.swing.*;
import java.awt.*;

public class ApplicationPanel extends JPanel {
    private ApplicationController applicationController;
    private MainFrame mainFrame; // For navigation and getting adopter ID

    private JTextField adopterNameField;
    private JTextField contactInfoField;
    private JLabel petInfoLabel; // To display info about the pet being applied for
    private JButton submitButton;
    private JButton cancelButton;

    private int currentPetId = -1; // Track which pet is being applied for

    public ApplicationPanel(ApplicationController controller, MainFrame frame) {
        this.applicationController = controller;
        this.mainFrame = frame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Pet Info Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        petInfoLabel = new JLabel("Applying for Pet ID: [Select from Browse]");
        add(petInfoLabel, gbc);

        // Adopter Name
        gbc.gridy++;
        gbc.gridwidth = 1;
        add(new JLabel("Your Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        adopterNameField = new JTextField(20);
        add(adopterNameField, gbc);

        // Contact Info
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Contact Info (Email/Phone):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contactInfoField = new JTextField(20);
        add(contactInfoField, gbc);

        // --- Separator ---
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(new JSeparator(), gbc);


        // Buttons
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitButton = new JButton("Submit Application");
        cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> submitApplication());
        cancelButton.addActionListener(e -> mainFrame.showPanel("BrowsePets")); // Go back

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, gbc);

        // Load potential default adopter info (if available)
        // In this simple version, fields start blank or could load from the hardcoded adopter
        loadAdopterInfo(mainFrame.getCurrentAdopterId());
    }

    // Method to pre-fill adopter info if possible
    private void loadAdopterInfo(int adopterId) {
        model.Adopter adopter = model.Database.getAdopterById(adopterId);
        if (adopter != null) {
            adopterNameField.setText(adopter.getName());
            contactInfoField.setText(adopter.getContactInfo());
            // Note: We might want to make these non-editable if confirming existing user
            // For now, allow editing as a simple way to potentially update info
        } else {
            // Clear fields if adopter not found (or handle error)
            adopterNameField.setText("");
            contactInfoField.setText("");
        }
    }


    // Called by MainFrame when navigating here
    public void setPetToApplyFor(int petId) {
        this.currentPetId = petId;
        model.Pet pet = model.Database.getPetById(petId); // Fetch pet details for display
        if (pet != null) {
            petInfoLabel.setText("Applying for: " + pet.getName() + " (ID: " + pet.getPetId() + ", Type: " + pet.getType() + ")");
        } else {
            petInfoLabel.setText("Applying for Pet ID: " + petId + " (Details not found!)");
        }
        // Re-load adopter info when setting a pet, in case user context is needed
        loadAdopterInfo(mainFrame.getCurrentAdopterId());
    }

    private void submitApplication() {
        if (currentPetId == -1) {
            JOptionPane.showMessageDialog(this,
                    "No pet selected. Please go back and select a pet to apply for.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String adopterName = adopterNameField.getText().trim();
        String contactInfo = contactInfoField.getText().trim();

        if (adopterName.isEmpty() || contactInfo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your name and contact information.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Use the hardcoded adopter ID from MainFrame
        int adopterId = mainFrame.getCurrentAdopterId();

        // In a real app, you might update the adopter's info here if it changed
        // For simplicity, we just use the ID to create the application

        Application application = new Application(adopterId, currentPetId); // Status defaults to 'pending'

        boolean success = applicationController.submitApplication(application);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Application submitted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            // Reset form and go back to browse or status view
            currentPetId = -1;
            petInfoLabel.setText("Applying for Pet ID: [Select from Browse]");
            // Optionally clear name/contact fields if not tied to a logged-in user
            // adopterNameField.setText("");
            // contactInfoField.setText("");
            mainFrame.showPanel("ViewStatus"); // Go to status view after applying
            mainFrame.getStatusPanel().loadApplications(adopterId); // Refresh status panel
            mainFrame.getPetBrowsePanel().refreshPetList(); // Refresh browse panel as pet might be pending now (or adopted if auto-approved)
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to submit application. The pet might no longer be available or a database error occurred.",
                    "Submission Failed", JOptionPane.ERROR_MESSAGE);
            mainFrame.getPetBrowsePanel().refreshPetList(); // Refresh browse list
        }
    }
}