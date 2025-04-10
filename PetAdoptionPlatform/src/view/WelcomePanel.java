package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.LineBorder;

public class WelcomePanel extends JPanel {
    private MainFrame mainFrame;

    // Colors
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color BUTTON_HOVER_COLOR = new Color(41, 128, 185);
    private static final Color FEATURE_BG_COLOR = new Color(255, 255, 255);
    private static final Color FEATURE_BORDER_COLOR = new Color(200, 200, 200);

    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 48);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 24);
    private static final Font FEATURE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FEATURE_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FEATURE_DESC_FONT = new Font("Segoe UI", Font.PLAIN, 16);

    public WelcomePanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Create main content panel with vertical BoxLayout
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Create header panel with logo and title
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add logo
        try {
            ImageIcon logoIcon = new ImageIcon("resources/pet-logo.png");
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            headerPanel.add(logoLabel);
            headerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        } catch (Exception e) {
            System.out.println("Could not load logo: " + e.getMessage());
        }

        // Add welcome message
        JLabel welcomeLabel = new JLabel("Welcome to Pet Adoption Platform");
        welcomeLabel.setFont(TITLE_FONT);
        welcomeLabel.setForeground(PRIMARY_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(welcomeLabel);

        // Add subtitle
        JLabel subtitleLabel = new JLabel("Find your perfect furry friend today!");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitleLabel);

        // Add features panel
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.X_AXIS));
        featuresPanel.setBackground(BACKGROUND_COLOR);
        featuresPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        addFeature(featuresPanel, "Browse Pets", "Find your perfect match", "resources/pet-browse.png");
        featuresPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        addFeature(featuresPanel, "Set Preferences", "Tell us what you're looking for", "resources/preferences.png");
        featuresPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        addFeature(featuresPanel, "Track Status", "Monitor your application progress", "resources/status.png");

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        // Add start button
        JButton startButton = new JButton("Get Started");
        startButton.setFont(BUTTON_FONT);
        startButton.setBackground(PRIMARY_COLOR);
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(15, 40, 15, 40)
        ));
        startButton.setPreferredSize(new Dimension(200, 50));

        // Add hover effect to start button
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startButton.setBackground(BUTTON_HOVER_COLOR);
                startButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BUTTON_HOVER_COLOR, 2),
                    BorderFactory.createEmptyBorder(15, 40, 15, 40)
                ));
                startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startButton.setBackground(PRIMARY_COLOR);
                startButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(15, 40, 15, 40)
                ));
                startButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        startButton.addActionListener(e -> mainFrame.showPanel("PetBrowse"));
        buttonPanel.add(startButton);

        // Add components to content panel
        contentPanel.add(headerPanel);
        contentPanel.add(featuresPanel);
        contentPanel.add(buttonPanel);

        // Add content panel to main panel
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addFeature(JPanel panel, String title, String description, String iconPath) {
        JPanel featurePanel = new JPanel();
        featurePanel.setLayout(new BoxLayout(featurePanel, BoxLayout.Y_AXIS));
        featurePanel.setBackground(FEATURE_BG_COLOR);
        featurePanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(FEATURE_BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        featurePanel.setMaximumSize(new Dimension(250, 300));

        // Add icon
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            featurePanel.add(iconLabel);
            featurePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        } catch (Exception e) {
            System.out.println("Could not load icon: " + e.getMessage());
        }

        // Create text panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(FEATURE_BG_COLOR);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FEATURE_TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(FEATURE_DESC_FONT);
        descLabel.setForeground(TEXT_COLOR);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(descLabel);

        featurePanel.add(textPanel, BorderLayout.CENTER);

        // Add hover effect to feature panel
        featurePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                featurePanel.setBackground(new Color(245, 245, 245));
                featurePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                featurePanel.setBackground(FEATURE_BG_COLOR);
                featurePanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        panel.add(featurePanel);
    }
} 