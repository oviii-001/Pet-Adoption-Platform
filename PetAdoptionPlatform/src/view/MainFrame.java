package view;

import controller.ApplicationController;
import controller.PetController;
import controller.AdopterController; // Assuming we need it later
import model.Pet; // Add import for Pet class

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import model.Database; // For shutdown hook

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JToggleButton roleToggle;
    private boolean isAdminMode = false; // Default to adopter mode

    // Panels
    PetBrowsePanel petBrowsePanel;
    private ApplicationPanel applicationPanel;
    StatusPanel statusPanel;
    private PreferencesPanel preferencesPanel;
    MatchedPetsPanel matchedPetsPanel;
    private AdminPetPanel adminPetPanel;
    private AdminApplicationPanel adminApplicationPanel;

    // Controllers
    private PetController petController;
    private ApplicationController applicationController;
    private AdopterController adopterController; // Instance for preference handling

    // Temporary Adopter ID (since no login)
    // In a real app, this would come from a login process.
    // Let's use ID 1, assuming Alice Smith was inserted as sample data.
    private static final int CURRENT_ADOPTER_ID = 1;

    public int getCurrentAdopterId() {
        return CURRENT_ADOPTER_ID;
    }

    public MainFrame() {
        setTitle("Pet Adoption Platform");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Handle close via listener
        setLocationRelativeTo(null); // Center the window

        // Initialize Controllers (pass necessary references)
        petController = new PetController();
        adopterController = new AdopterController(); // Initialize adopter controller
        applicationController = new ApplicationController();

        // Setup main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize Panels (pass controllers and MainFrame reference if needed for navigation)
        petBrowsePanel = new PetBrowsePanel(petController, this);
        applicationPanel = new ApplicationPanel(applicationController, this);
        statusPanel = new StatusPanel(applicationController, this); // Pass MainFrame ref
        preferencesPanel = new PreferencesPanel(adopterController, petController, this); // Needs adopter & pet controller
        matchedPetsPanel = new MatchedPetsPanel(petController, this); // Needs pet controller
        adminPetPanel = new AdminPetPanel(petController);
        adminApplicationPanel = new AdminApplicationPanel(applicationController, petController); // Needs both

        // Add panels to CardLayout
        mainPanel.add(petBrowsePanel, "BrowsePets");
        mainPanel.add(applicationPanel, "SubmitApplication");
        mainPanel.add(statusPanel, "ViewStatus");
        mainPanel.add(preferencesPanel, "SetPreferences");
        mainPanel.add(matchedPetsPanel, "MatchedPets");
        mainPanel.add(adminPetPanel, "AdminManagePets");
        mainPanel.add(adminApplicationPanel, "AdminManageApps");

        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);

        // Setup Menu Bar and Role Toggle
        setupMenuBar();

        // Add Window Listener for database cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closing application...");
                Database.closeConnection();
                dispose(); // Close the window
                System.exit(0); // Terminate the application
            }
        });

        // Initial setup based on default role (Adopter)
        updateViewForRole();
        showPanel("BrowsePets"); // Start with browsing pets
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // --- Adopter Menu ---
        JMenu adopterMenu = new JMenu("Adopter");
        JMenuItem browsePetsItem = new JMenuItem("Browse Pets");
        browsePetsItem.addActionListener(e -> showPanel("BrowsePets"));
        JMenuItem submitAppItem = new JMenuItem("Submit Application"); // Usually navigated to from Browse
        submitAppItem.addActionListener(e -> showPanel("SubmitApplication")); // Allow direct access maybe?
        JMenuItem viewStatusItem = new JMenuItem("View Application Status");
        viewStatusItem.addActionListener(e -> {
            statusPanel.loadApplications(CURRENT_ADOPTER_ID); // Load status for current user
            showPanel("ViewStatus");
        });
        JMenuItem setPrefsItem = new JMenuItem("Set Preferences");
        setPrefsItem.addActionListener(e -> {
            preferencesPanel.loadPreferences(CURRENT_ADOPTER_ID); // Load current prefs
            showPanel("SetPreferences");
        });
        JMenuItem viewMatchesItem = new JMenuItem("View Matched Pets");
        viewMatchesItem.addActionListener(e -> {
            matchedPetsPanel.loadMatchedPets(CURRENT_ADOPTER_ID); // Load matches for user
            showPanel("MatchedPets");
        });


        adopterMenu.add(browsePetsItem);
        // adopterMenu.add(submitAppItem); // Let's disable direct access, apply from browse view
        adopterMenu.add(viewStatusItem);
        adopterMenu.add(setPrefsItem);
        adopterMenu.add(viewMatchesItem);

        // --- Admin Menu ---
        JMenu adminMenu = new JMenu("Admin");
        JMenuItem managePetsItem = new JMenuItem("Manage Pets");
        managePetsItem.addActionListener(e -> showPanel("AdminManagePets"));
        JMenuItem manageAppsItem = new JMenuItem("Manage Applications");
        manageAppsItem.addActionListener(e -> showPanel("AdminManageApps"));

        adminMenu.add(managePetsItem);
        adminMenu.add(manageAppsItem);

        // --- Role Toggle Button ---
        roleToggle = new JToggleButton("Switch to Admin Mode");
        roleToggle.addActionListener(e -> {
            isAdminMode = roleToggle.isSelected();
            roleToggle.setText(isAdminMode ? "Switch to Adopter Mode" : "Switch to Admin Mode");
            updateViewForRole();
        });

        menuBar.add(adopterMenu);
        menuBar.add(adminMenu);
        menuBar.add(Box.createHorizontalGlue()); // Pushes toggle to the right
        menuBar.add(roleToggle);

        setJMenuBar(menuBar);
    }

    private void updateViewForRole() {
        // Enable/Disable menus based on role
        getJMenuBar().getMenu(0).setEnabled(!isAdminMode); // Adopter Menu
        getJMenuBar().getMenu(1).setEnabled(isAdminMode);  // Admin Menu

        // Switch to appropriate default panel for the role
        if (isAdminMode) {
            showPanel("AdminManagePets"); // Default admin view
        } else {
            // Reload adopter data when switching back
            petBrowsePanel.refreshPetList();
            showPanel("BrowsePets");    // Default adopter view
        }
        // Ensure panels refresh data if needed when role changes
        if (adminPetPanel != null) adminPetPanel.refreshPetList();
        if (adminApplicationPanel != null) adminApplicationPanel.refreshApplicationList();
        if (statusPanel != null) statusPanel.loadApplications(CURRENT_ADOPTER_ID);
        if (preferencesPanel != null) preferencesPanel.loadPreferences(CURRENT_ADOPTER_ID);
        if (matchedPetsPanel != null) matchedPetsPanel.clearMatchedPets(); // Clear matches until explicitly viewed

    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
        System.out.println("Showing panel: " + panelName); // Debugging
    }

    // Method to navigate to the application form with a specific pet ID
    public void navigateToApplicationForm(int petId) {
        // Check if pet is actually available before navigating
        Pet pet = petController.getPetById(petId);
        if (pet != null && "available".equalsIgnoreCase(pet.getStatus())) {
            applicationPanel.setPetToApplyFor(petId);
            showPanel("SubmitApplication");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Selected pet is no longer available.",
                    "Pet Unavailable", JOptionPane.WARNING_MESSAGE);
            petBrowsePanel.refreshPetList(); // Refresh browse view
        }
    }
}