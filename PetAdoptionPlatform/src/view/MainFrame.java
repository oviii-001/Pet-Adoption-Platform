package view;

import controller.ApplicationController;
import controller.PetController;
import controller.AdopterController;
import model.Pet;
import model.Database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JButton roleToggle;
    private boolean isAdminMode = false;
    private String previousPanel = null;
    private String currentPanel = "Welcome";
    private JButton backButton;
    private JMenu adopterMenu;
    private JMenu adminMenu;

    // Panels
    protected LoginPanel loginPanel;
    protected PetBrowsePanel petBrowsePanel;
    protected ApplicationPanel applicationPanel;
    protected StatusPanel statusPanel;
    protected PreferencesPanel preferencesPanel;
    protected MatchedPetsPanel matchedPetsPanel;
    protected AdminPetPanel adminPetPanel;
    protected AdminApplicationPanel adminApplicationPanel;
    protected WelcomePanel welcomePanel;

    // Controllers
    private PetController petController;
    private ApplicationController applicationController;
    private AdopterController adopterController;

    private static final int CURRENT_ADOPTER_ID = 1;

    // Colors
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 48);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 24);
    private static final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    // List of admin panel names
    private static final List<String> ADMIN_PANELS = Arrays.asList("AdminManagePets", "AdminManageApplications");

    // Getter methods for panels
    public StatusPanel getStatusPanel() {
        return statusPanel;
    }

    public PetBrowsePanel getPetBrowsePanel() {
        return petBrowsePanel;
    }

    public MatchedPetsPanel getMatchedPetsPanel() {
        return matchedPetsPanel;
    }

    public MainFrame() {
        setTitle("Pet Adoption Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setPreferredSize(new Dimension(1000, 800));
        setLocationRelativeTo(null);

        // Set application icon
        try {
            ImageIcon icon = new ImageIcon("resources/pet-logo.png");
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.out.println("Could not load icon: " + e.getMessage());
        }

        // Initialize controllers
        petController = new PetController();
        adopterController = new AdopterController();
        applicationController = new ApplicationController();

        // Initialize main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Create a container panel to center all content
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(BACKGROUND_COLOR);

        // Create a wrapper panel for centering
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        // Initialize panels
        welcomePanel = new WelcomePanel(this);
        loginPanel = new LoginPanel(this);
        petBrowsePanel = new PetBrowsePanel(this, petController);
        applicationPanel = new ApplicationPanel(applicationController, this);
        statusPanel = new StatusPanel(applicationController, this);
        preferencesPanel = new PreferencesPanel(adopterController, petController, this);
        matchedPetsPanel = new MatchedPetsPanel(petController, this);
        adminPetPanel = new AdminPetPanel(petController);
        adminApplicationPanel = new AdminApplicationPanel(applicationController, petController);

        // Add panels to main panel
        mainPanel.add(welcomePanel, "Welcome");
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(petBrowsePanel, "PetBrowse");
        mainPanel.add(applicationPanel, "Application");
        mainPanel.add(statusPanel, "Status");
        mainPanel.add(preferencesPanel, "Preferences");
        mainPanel.add(matchedPetsPanel, "MatchedPets");
        mainPanel.add(adminPetPanel, "AdminManagePets");
        mainPanel.add(adminApplicationPanel, "AdminManageApplications");

        // Add main panel to wrapper panel
        wrapperPanel.add(mainPanel, gbc);

        // Add wrapper panel to center container
        centerContainer.add(wrapperPanel, BorderLayout.CENTER);

        // Add center container to frame
        add(centerContainer, BorderLayout.CENTER);

        // Setup menu bar
        setupMenuBar();

        // Add window listener for cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closing application...");
                Database.closeConnection();
                dispose();
                System.exit(0);
            }
        });

        // Show welcome panel
        showPanel("Welcome");

        // Pack and display
        pack();
        setVisible(true);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(PRIMARY_COLOR);
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Add back button
        backButton = new JButton("← Back");
        backButton.setFont(BUTTON_FONT);
        backButton.setBackground(PRIMARY_COLOR);
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            String currentPanel = getCurrentPanelName();
            if (!currentPanel.equals("Welcome") && !currentPanel.equals("Login")) {
                showPanel("Welcome");
            }
        });
        menuBar.add(backButton);

        // Add menu items
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(MENU_FONT);
        fileMenu.setForeground(Color.WHITE);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setFont(MENU_FONT);
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        // Initial setup for the button (will be updated by showPanel immediately)
        roleToggle = new JButton("Switch to Admin Mode");
        roleToggle.setFont(BUTTON_FONT);
        roleToggle.setForeground(Color.WHITE);
        roleToggle.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        roleToggle.setFocusPainted(false);
        // Initial background color will be set by the first call to showPanel -> updateRoleToggleButtonAppearance

        roleToggle.addActionListener(e -> {
            String currentText = roleToggle.getText();
            if ("Switch to Adopter Mode".equals(currentText)) {
                // Action is to go to Adopter mode/view, regardless of current isAdminMode
                setAdminMode(false); // Ensure flag is correct
                showPanel("Welcome");
            } else { // currentText must be "Switch to Admin Mode"
                // Action is to attempt Admin login
                showPanel("Login");
            }
        });
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(roleToggle);

        setJMenuBar(menuBar);
    }

    // Method to update button appearance based on the panel shown
    private void updateRoleToggleButtonAppearance(String panelName) {
        if (roleToggle == null) return;

        if (ADMIN_PANELS.contains(panelName) || panelName.equals("Login")) {
            // Showing an Admin panel OR the Login panel
            // In both cases, the alternative action is to go back to Adopter Mode
            roleToggle.setText("Switch to Adopter Mode");
            roleToggle.setBackground(PRIMARY_COLOR); // Color reflects target (Adopter)
        } else {
            // Showing a non-Admin, non-Login panel (e.g., Welcome, PetBrowse)
            roleToggle.setText("Switch to Admin Mode");
            roleToggle.setBackground(SECONDARY_COLOR); // Color reflects target (Admin)
        }
    }

    public void showPanel(String panelName) {
        if (currentPanel != null && !panelName.equals("Welcome") && !panelName.equals("Login")) {
            previousPanel = currentPanel;
        }
        currentPanel = panelName;
        cardLayout.show(mainPanel, panelName);
        backButton.setVisible(!panelName.equals("Welcome") && !panelName.equals("Login"));
        System.out.println("Showing panel: " + panelName);
        // Update the button's appearance whenever a panel is shown
        updateRoleToggleButtonAppearance(panelName);

        // Refresh pet list when showing PetBrowse panel
        if (panelName.equals("PetBrowse") && petBrowsePanel != null) {
            petBrowsePanel.refreshPetList();
        }
    }

    public String getCurrentPanelName() {
        return currentPanel;
    }

    public void setAdminMode(boolean isAdminMode) {
        this.isAdminMode = isAdminMode;
        updateViewForRole(); // Update menus based on the new mode
    }

    // This method now only handles UI elements directly tied to the isAdminMode flag (like menus)
    private void updateViewForRole() {
        // Enable/disable menus based on isAdminMode
        if (adopterMenu != null) {
            adopterMenu.setEnabled(!isAdminMode);
        }
        if (adminMenu != null) {
            adminMenu.setEnabled(isAdminMode);
        }

        // Button appearance is handled by updateRoleToggleButtonAppearance via showPanel

        // Refresh panel data if needed (keep this logic)
        if (adminPetPanel != null) adminPetPanel.refreshPetList();
        if (adminApplicationPanel != null) adminApplicationPanel.refreshApplicationList();
        if (statusPanel != null) statusPanel.loadApplications(CURRENT_ADOPTER_ID);
        if (preferencesPanel != null) preferencesPanel.loadPreferences(CURRENT_ADOPTER_ID);
        if (matchedPetsPanel != null) matchedPetsPanel.clearMatchedPets();
        if (petBrowsePanel != null) petBrowsePanel.refreshPetList();
    }

    public void navigateToApplicationForm(int petId) {
        Pet pet = petController.getPetById(petId);
        if (pet != null && "available".equalsIgnoreCase(pet.getStatus())) {
            applicationPanel.setPetToApplyFor(petId);
            showPanel("Application");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Selected pet is no longer available.",
                    "Pet Unavailable",
                    JOptionPane.WARNING_MESSAGE);
            petBrowsePanel.refreshPetList();
        }
    }

    public int getCurrentAdopterId() {
        return CURRENT_ADOPTER_ID;
    }

    public PetController getPetController() {
        return petController;
    }

    public AdopterController getAdopterController() {
        return adopterController;
    }

    public ApplicationController getApplicationController() {
        return applicationController;
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            Database.initializeDatabase();
            SwingUtilities.invokeLater(() -> new MainFrame());
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading SQLite JDBC driver: " + e.getMessage());
            e.printStackTrace();
        }
    }
}