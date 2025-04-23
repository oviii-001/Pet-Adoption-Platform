package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.geom.RoundRectangle2D;

public class WelcomePanel extends JPanel {
    private MainFrame mainFrame;

    // Enhanced Colors & Fonts
    private static final Color PRIMARY_COLOR = new Color(60, 90, 160); // Deeper Blue
    private static final Color SECONDARY_COLOR = new Color(230, 126, 34); // Orange accent
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Lighter Gray
    private static final Color TEXT_COLOR = new Color(52, 73, 94); // Dark Gray/Blue
    private static final Color BUTTON_HOVER_COLOR = new Color(45, 75, 140); // Darker Blue for Hover
    private static final Color FEATURE_BG_COLOR = new Color(255, 255, 255); // White
    private static final Color FEATURE_BORDER_COLOR = new Color(224, 224, 224); // Light Gray Border
    private static final Color FEATURE_SHADOW_COLOR = new Color(0, 0, 0, 50); // Shadow for cards

    private static final Font TITLE_FONT = new Font("Segoe UI Semibold", Font.BOLD, 42);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 20);
    private static final Font FEATURE_TITLE_FONT = new Font("Segoe UI Semibold", Font.BOLD, 18);
    private static final Font FEATURE_DESC_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public WelcomePanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(40, 40, 40, 40));

        // Central Content Panel using GridBagLayout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER; 
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.insets = new Insets(10, 0, 10, 0);

        // Header Section
        JPanel headerPanel = createHeaderPanel();
        gbc.weighty = 0.1; 
        gbc.insets = new Insets(0, 0, 30, 0); 
        contentPanel.add(headerPanel, gbc);

        // Features Section
        JPanel featuresPanel = createFeaturesPanel();
        gbc.weighty = 0.5; 
        gbc.insets = new Insets(20, 0, 30, 0); 
        contentPanel.add(featuresPanel, gbc);

        // Button Section
        JButton startButton = createStartButton();
        gbc.weighty = 0.1; 
        gbc.insets = new Insets(20, 0, 0, 0); 
        contentPanel.add(startButton, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

    
        try {
            ImageIcon logoIcon = new ImageIcon(new ImageIcon("resources/pet-logo.png").getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            headerPanel.add(logoLabel);
            headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        } catch (Exception e) {
            System.out.println("Could not load logo: " + e.getMessage());
        }

        JLabel welcomeLabel = new JLabel("Welcome to Pet Harmony");
        welcomeLabel.setFont(TITLE_FONT);
        welcomeLabel.setForeground(PRIMARY_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(welcomeLabel);

        JLabel subtitleLabel = new JLabel("Connecting loving homes with furry friends");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createFeaturesPanel() {
        JPanel featuresPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        featuresPanel.setBackground(BACKGROUND_COLOR);
        featuresPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create and add feature cards with pet images
        featuresPanel.add(createFeatureCard("Browse Pets", "Find your perfect match among our available pets.", "resources/browse-pets-image.png"));
        featuresPanel.add(createFeatureCard("Set Preferences", "Tell us what you're looking for in a companion.", "resources/preferences-image.png"));
        featuresPanel.add(createFeatureCard("Track Status", "Monitor the progress of your adoption application.", "resources/status-image.png"));

        return featuresPanel;
    }

     private JPanel createFeatureCard(String title, String description, String imagePath) {
        // Panel with shadow effect
        JPanel shadowPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Apply shadow painting
                int shadowSize = 5;
                g2d.setColor(FEATURE_SHADOW_COLOR);
                g2d.fillRoundRect(shadowSize, shadowSize, getWidth() - shadowSize * 2, getHeight() - shadowSize * 2, 20, 20);
                g2d.dispose();
            }
        };
        shadowPanel.setOpaque(false);
        shadowPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // Space for shadow

        // Main content card panel
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(FEATURE_BG_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(FEATURE_BORDER_COLOR, 1, 15),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        cardPanel.setPreferredSize(new Dimension(230, 420));
        cardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add image
        try {
             // Use imagePath and scale it larger (150x150)
             ImageIcon imageIcon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
             JLabel imageLabel = new JLabel(imageIcon);
             imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
             cardPanel.add(imageLabel);
             cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));
         } catch (Exception e) {
             System.out.println("Could not load image for feature card: " + e.getMessage());
             // Optionally add a placeholder or error text
             JLabel errorLabel = new JLabel("Image Error");
             errorLabel.setFont(FEATURE_DESC_FONT);
             errorLabel.setForeground(SECONDARY_COLOR);
             errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
             cardPanel.add(errorLabel);
             cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));
         }

        // Add Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FEATURE_TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add Description (using JTextArea for wrapping)
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(FEATURE_DESC_FONT);
        descArea.setForeground(TEXT_COLOR);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setFocusable(false);
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        descArea.setMaximumSize(new Dimension(180, 80));
        cardPanel.add(descArea);

         // Add hover effect (applied to shadow panel to lift the card)
        shadowPanel.addMouseListener(new MouseAdapter() {
             Point originalLocation = null;
             @Override
             public void mouseEntered(MouseEvent e) {
                if (originalLocation == null) {
                    originalLocation = shadowPanel.getLocation();
                }
                shadowPanel.setLocation(originalLocation.x - 2, originalLocation.y - 2);
                shadowPanel.setBorder(new EmptyBorder(7, 7, 3, 3));
                shadowPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                shadowPanel.repaint();
             }

             @Override
             public void mouseExited(MouseEvent e) {
                 if (originalLocation != null) {
                    shadowPanel.setLocation(originalLocation);
                 }
                 shadowPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                 shadowPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                 shadowPanel.repaint();
             }
         });

        shadowPanel.add(cardPanel, BorderLayout.CENTER);
        return shadowPanel;
    }

    private JButton createStartButton() {
         JButton startButton = new JButton("Get Started Finding Your Friend") {
            // Custom painting for rounded corners
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isArmed()) {
                    g2.setColor(BUTTON_HOVER_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(BUTTON_HOVER_COLOR);
                } else {
                    g2.setColor(getBackground());
                }
                // Fill rounded rectangle
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Paint text
                super.paintComponent(g);
                g2.dispose();
            }
        };

        startButton.setFont(BUTTON_FONT);
        startButton.setBackground(PRIMARY_COLOR);
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40)); 
        startButton.setContentAreaFilled(false); 
        startButton.setOpaque(false);
        startButton.setPreferredSize(new Dimension(300, 55)); 

       
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                startButton.repaint(); 
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                startButton.repaint(); 
            }
        });

        startButton.addActionListener(e -> mainFrame.showPanel("PetBrowse"));
        return startButton;
    }

   
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