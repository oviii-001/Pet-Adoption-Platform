package view;

import controller.AdopterController;
import controller.PetController; 
import model.Adopter;
import model.Database; 

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PreferencesPanel extends JPanel {
    private AdopterController adopterController;
    private PetController petController; 
    private MainFrame mainFrame;

    private JComboBox<String> typeComboBox;
    private JComboBox<String> sizeComboBox;
    private JTextField ageTextField; 
    private JButton saveButton;
    private JButton viewMatchesButton;

    private int currentAdopterId = -1; 

    public PreferencesPanel(AdopterController adopterCtrl, PetController petCtrl, MainFrame frame) {
        this.adopterController = adopterCtrl;
        this.petController = petCtrl;
        this.mainFrame = frame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Type Preference
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Preferred Pet Type:"), gbc);
        gbc.gridx = 1;
        typeComboBox = new JComboBox<>(new String[]{"Any", "Dog", "Cat", "Other"}); 
        add(typeComboBox, gbc);

        // Size Preference
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Preferred Size:"), gbc);
        gbc.gridx = 1;
        sizeComboBox = new JComboBox<>(new String[]{"Any", "Small", "Medium", "Large"});
        add(sizeComboBox, gbc);

        // Age Preference
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Preferred Age (e.g., <5, >2, 3):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ageTextField = new JTextField(10);
        add(ageTextField, gbc);


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
        saveButton = new JButton("Save Preferences");
        viewMatchesButton = new JButton("View Matched Pets");


        saveButton.addActionListener(e -> savePreferences());
        viewMatchesButton.addActionListener(e -> viewMatches());

        buttonPanel.add(saveButton);
        buttonPanel.add(viewMatchesButton);
        gbc.gridx = 0; 
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

    }

    public void loadPreferences(int adopterId) {
        this.currentAdopterId = adopterId;
        Adopter adopter = Database.getAdopterById(adopterId);
        if (adopter != null && adopter.getPreferences() != null && !adopter.getPreferences().isEmpty()) {
            parseAndSetPreferences(adopter.getPreferences());
        } else {
            // Reset to defaults if no preferences saved
            typeComboBox.setSelectedItem("Any");
            sizeComboBox.setSelectedItem("Any");
            ageTextField.setText("");
            System.out.println("No preferences found for adopter " + adopterId + ", setting defaults.");
        }
    }

    private void parseAndSetPreferences(String prefsString) {
        
        Map<String, String> prefsMap = new HashMap<>();
        if (prefsString != null && !prefsString.trim().isEmpty()) {
            String[] pairs = prefsString.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    prefsMap.put(kv[0].trim().toLowerCase(), kv[1].trim());
                }
            }
        }

        // UI components based on parsed map
        typeComboBox.setSelectedItem(prefsMap.getOrDefault("type", "Any"));
        sizeComboBox.setSelectedItem(prefsMap.getOrDefault("size", "Any"));
        ageTextField.setText(prefsMap.getOrDefault("age", ""));
        System.out.println("Loaded preferences: " + prefsMap);
    }

    private String buildPreferencesString() {
        Map<String, String> prefsMap = new HashMap<>();
        String selectedType = (String) typeComboBox.getSelectedItem();
        String selectedSize = (String) sizeComboBox.getSelectedItem();
        String agePref = ageTextField.getText().trim();

        if (!"Any".equals(selectedType)) {
            prefsMap.put("type", selectedType);
        }
        if (!"Any".equals(selectedSize)) {
            prefsMap.put("size", selectedSize);
        }
        if (!agePref.isEmpty()) {
           
            prefsMap.put("age", agePref);
        }

       
        return prefsMap.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(","));
    }

    private void savePreferences() {
        if (currentAdopterId == -1) {
            JOptionPane.showMessageDialog(this, "Cannot save preferences. Adopter context not set.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String prefsString = buildPreferencesString();
        boolean success = adopterController.updateAdopterPreferences(currentAdopterId, prefsString);

        if (success) {
            JOptionPane.showMessageDialog(this, "Preferences saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Saved preferences for adopter " + currentAdopterId + ": " + prefsString);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save preferences.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewMatches() {
        if (currentAdopterId == -1) {
            JOptionPane.showMessageDialog(this, "Cannot view matches. Adopter context not set.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
       
        System.out.println("Navigating to matched pets view for adopter " + currentAdopterId);
        mainFrame.getMatchedPetsPanel().loadMatchedPets(currentAdopterId); 
        mainFrame.showPanel("MatchedPets"); 
    }
}