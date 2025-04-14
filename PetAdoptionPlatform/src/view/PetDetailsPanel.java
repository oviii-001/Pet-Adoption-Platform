package view;

import model.Pet;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

public class PetDetailsPanel extends JPanel {

    private Pet pet;
    private MainFrame mainFrame; // For navigation

    // UI Components
    private JLabel imageLabel;
    private JLabel nameLabelValue;
    private JLabel typeLabelValue;
    private JLabel sizeLabelValue;
    private JLabel ageLabelValue;
    private JLabel genderLabelValue;
    private JLabel breedLabelValue;
    private JLabel healthStatusLabelValue;
    private JLabel temperamentLabelValue;
    private JLabel statusLabelValue;
    private JTextArea descriptionAreaValue;
    private JButton applyButton;

    // --- Modern Styling Constants ---
    private static final Color PRIMARY_COLOR = new Color(60, 90, 160); // Deeper blue
    private static final Color SECONDARY_COLOR = new Color(230, 126, 34); // Orange (for pending status)
    private static final Color ACCENT_COLOR = new Color(75, 192, 192); // Teal accent (optional)
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Very light gray
    private static final Color TEXT_COLOR = new Color(52, 73, 94); // Dark gray-blue
    private static final Color LIGHT_TEXT_COLOR = new Color(136, 136, 136); // Lighter gray for labels
    private static final Color PANEL_BG_COLOR = Color.WHITE; // White background for content panel
    private static final Color BORDER_COLOR = new Color(224, 224, 224); // Light border color
    private static final Color IMAGE_BG_COLOR = new Color(235, 235, 235); // Background for image placeholder
    private static final Color SUCCESS_COLOR_FG = new Color(0, 140, 0); // Dark green for Adopted status
    private static final Color BUTTON_HOVER_BG = new Color(45, 75, 140); // Darker blue for button hover
    private static final Color DISABLED_BUTTON_BG = new Color(189, 195, 199); // Gray for disabled button

    private static final Font HEADING_FONT = new Font("Segoe UI Semibold", Font.BOLD, 28);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font VALUE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font DESC_FONT = new Font("Segoe UI", Font.PLAIN, 14); // Slightly larger desc font
    private static final Font STATUS_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public PetDetailsPanel(Pet pet, MainFrame mainFrame) {
        this.pet = pet;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout()); // Main layout
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(30, 30, 30, 30)); // Increased padding

        setupUI();
        populateDetails();

        applyButton.addActionListener(e -> {
            if (this.pet != null && this.pet.getStatus().equalsIgnoreCase("available")) {
                mainFrame.showApplicationForm(this.pet);
            } else if (this.pet != null) {
                JOptionPane.showMessageDialog(this,
                    "This pet is currently '" + this.pet.getStatus() + "' and not available for adoption application.",
                    "Not Available", JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Pet details not loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void setupUI() {
        // Central content panel with white background and rounded border/shadow
        JPanel contentPanel = new JPanel(new BorderLayout(25, 25)); // Gaps between image/details
        contentPanel.setBackground(PANEL_BG_COLOR);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
             new ShadowBorder(5, 0.1f, 8, Color.GRAY), // Subtle shadow
             new EmptyBorder(25, 25, 25, 25) // Padding inside the white panel
        ));

        // --- Image Panel (Left) ---
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false); // Use contentPanel background
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(300, 300)); // Slightly larger image
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(new RoundedBorder(BORDER_COLOR, 1, 10)); // Rounded border for image area
        imageLabel.setOpaque(true); // Needed to show background color
        imageLabel.setBackground(IMAGE_BG_COLOR); // Placeholder background
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        contentPanel.add(imagePanel, BorderLayout.WEST);

        // --- Details Panel (Right) ---
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10)); // Layout for heading, grid, description
        detailsPanel.setOpaque(false);

        // Pet Name Heading
        nameLabelValue = new JLabel(" "); // Placeholder
        nameLabelValue.setFont(HEADING_FONT);
        nameLabelValue.setForeground(TEXT_COLOR);
        nameLabelValue.setBorder(new EmptyBorder(0, 0, 15, 0)); // Margin below name
        detailsPanel.add(nameLabelValue, BorderLayout.NORTH);

        // Grid for details
        JPanel detailsGridPanel = new JPanel(new GridBagLayout());
        detailsGridPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 15); // Increased vertical spacing, right margin
        gbc.anchor = GridBagConstraints.WEST;
        int y = 0;

        // Detail Rows (Label on left, Value on right)
        typeLabelValue = createValueLabel();
        addDetailRow(detailsGridPanel, gbc, y++, "Type:", typeLabelValue);

        sizeLabelValue = createValueLabel();
        addDetailRow(detailsGridPanel, gbc, y++, "Size:", sizeLabelValue);

        ageLabelValue = createValueLabel();
        addDetailRow(detailsGridPanel, gbc, y++, "Age:", ageLabelValue);

        genderLabelValue = createValueLabel();
        addDetailRow(detailsGridPanel, gbc, y++, "Gender:", genderLabelValue);

        breedLabelValue = createValueLabel();
        addDetailRow(detailsGridPanel, gbc, y++, "Breed:", breedLabelValue);

        healthStatusLabelValue = createValueLabel();
        addDetailRow(detailsGridPanel, gbc, y++, "Health:", healthStatusLabelValue);

        temperamentLabelValue = createValueLabel();
        addDetailRow(detailsGridPanel, gbc, y++, "Temperament:", temperamentLabelValue);

        statusLabelValue = createValueLabel(); // Create label, font/color set in populateDetails
        statusLabelValue.setFont(STATUS_FONT); // Set bold font
        addDetailRow(detailsGridPanel, gbc, y++, "Status:", statusLabelValue);

        detailsPanel.add(detailsGridPanel, BorderLayout.CENTER);

        // Description Area (Below grid)
        JPanel descriptionPanel = new JPanel(new BorderLayout(0, 5));
        descriptionPanel.setOpaque(false);
        descriptionPanel.setBorder(new EmptyBorder(15, 0, 0, 0)); // Top margin for description

        JLabel descLabel = createHeaderLabel("Description:");
        descriptionPanel.add(descLabel, BorderLayout.NORTH);

        descriptionAreaValue = new JTextArea(4, 20); // Rows, Columns
        descriptionAreaValue.setFont(DESC_FONT);
        descriptionAreaValue.setForeground(TEXT_COLOR);
        descriptionAreaValue.setLineWrap(true);
        descriptionAreaValue.setWrapStyleWord(true);
        descriptionAreaValue.setEditable(false);
        descriptionAreaValue.setOpaque(false); // Use panel background
        descriptionAreaValue.setMargin(new Insets(5, 5, 5, 5));

        JScrollPane descScrollPane = new JScrollPane(descriptionAreaValue);
        descScrollPane.setBorder(new RoundedBorder(BORDER_COLOR, 1, 5)); // Border around text area
        descScrollPane.setOpaque(false);
        descScrollPane.getViewport().setOpaque(false);
        descriptionPanel.add(descScrollPane, BorderLayout.CENTER);

        detailsPanel.add(descriptionPanel, BorderLayout.SOUTH);

        contentPanel.add(detailsPanel, BorderLayout.CENTER);

        // --- Button Panel (Bottom) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the button
        buttonPanel.setOpaque(false); // Use main background
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0)); // Add space above button

        applyButton = createStyledButton("Apply for Adoption");
        buttonPanel.add(applyButton);

        // --- Add Panels to Main Frame ---
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int y, String labelText, JLabel valueLabel) {
        JLabel headerLabel = createHeaderLabel(labelText);
        gbc.gridx = 0; gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST; // Right-align label
        gbc.weightx = 0.1; // Give some weight to label column
        panel.add(headerLabel, gbc);

        gbc.gridx = 1; gbc.gridy = y;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Left-align value
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.9; // More weight to value column
        panel.add(valueLabel, gbc);
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(LIGHT_TEXT_COLOR); // Lighter color for labels
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel(" "); // Placeholder
        label.setFont(VALUE_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private void populateDetails() {
        if (pet == null) {
             // Set placeholder text if pet is null
             nameLabelValue.setText("Pet Not Found");
             setPlaceholderImage("No Pet Data");
             // Optionally disable apply button
             if(applyButton != null) applyButton.setEnabled(false);
             return;
        }

        nameLabelValue.setText(pet.getName());
        typeLabelValue.setText(pet.getType());
        sizeLabelValue.setText(pet.getSize());
        ageLabelValue.setText(String.valueOf(pet.getAge()));
        genderLabelValue.setText(pet.getGender());
        breedLabelValue.setText(pet.getBreed());
        healthStatusLabelValue.setText(pet.getHealthStatus());
        temperamentLabelValue.setText(pet.getTemperament());
        descriptionAreaValue.setText(pet.getDescription());
        descriptionAreaValue.setCaretPosition(0); // Scroll to top

        // Set status label text and color, and button state
        String statusText = pet.getStatus().substring(0, 1).toUpperCase() + pet.getStatus().substring(1);
        statusLabelValue.setText(statusText);

        switch (pet.getStatus().toLowerCase()) {
            case "adopted":
                statusLabelValue.setForeground(SUCCESS_COLOR_FG); // Dark Green
                applyButton.setEnabled(false);
                applyButton.setText("Already Adopted");
                applyButton.setBackground(DISABLED_BUTTON_BG);
                break;
            case "pending":
                statusLabelValue.setForeground(SECONDARY_COLOR); // Orange
                applyButton.setEnabled(false);
                applyButton.setText("Application Pending");
                applyButton.setBackground(DISABLED_BUTTON_BG);
                break;
            case "available":
            default:
                statusLabelValue.setForeground(PRIMARY_COLOR); // Blue
                applyButton.setEnabled(true);
                applyButton.setText("Apply for Adoption");
                applyButton.setBackground(PRIMARY_COLOR); // Ensure it's the active color
                break;
        }

        // Display Image
        byte[] imageData = pet.getImageData();
        if (imageData != null && imageData.length > 0) {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(imageData)) {
                BufferedImage bImage = ImageIO.read(bis);
                if (bImage != null) {
                    int labelWidth = imageLabel.getPreferredSize().width;
                    int labelHeight = imageLabel.getPreferredSize().height;
                    // Maintain aspect ratio, fit within bounds
                    Image scaledImage = scaleImage(bImage, labelWidth, labelHeight);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                    imageLabel.setText("");
                    imageLabel.setBackground(PANEL_BG_COLOR); // Use panel background behind image
                     imageLabel.setBorder(new RoundedBorder(BORDER_COLOR, 1, 10)); // Keep border
                } else {
                    setPlaceholderImage("Image Format Error");
                }
            } catch (Exception e) {
                setPlaceholderImage("Image Load Error");
                System.err.println("Error loading image for pet ID " + pet.getPetId() + ": " + e.getMessage());
            }
        } else {
            setPlaceholderImage("No Image Available");
        }
    }

    private Image scaleImage(BufferedImage originalImage, int boundWidth, int boundHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        // Check if scaling is needed
        if (originalWidth > boundWidth) {
            newWidth = boundWidth;
            newHeight = (newWidth * originalHeight) / originalWidth;
        }
        if (newHeight > boundHeight) {
            newHeight = boundHeight;
            newWidth = (newHeight * originalWidth) / originalHeight;
        }

        return originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }

    private void setPlaceholderImage(String text) {
        imageLabel.setIcon(null);
        imageLabel.setText("<html><div style='text-align: center;'>" + text + "</div></html>"); // Center text
        imageLabel.setFont(VALUE_FONT);
        imageLabel.setForeground(LIGHT_TEXT_COLOR);
        imageLabel.setBackground(IMAGE_BG_COLOR); // Ensure placeholder background
        imageLabel.setBorder(new RoundedBorder(BORDER_COLOR, 1, 10)); // Keep border
    }

    private JButton createStyledButton(String text) {
         JButton button = new JButton(text) {
            private Color currentBg = PRIMARY_COLOR;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (!isEnabled()) {
                    currentBg = DISABLED_BUTTON_BG;
                } else if (getModel().isRollover()) {
                    currentBg = BUTTON_HOVER_BG;
                } else {
                    // Use the background set externally (e.g., in populateDetails)
                    currentBg = getBackground();
                }

                g2.setColor(currentBg);
                // Fill with rounded corners
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));

                // Let the default painter handle text, etc.
                // Need to make the button non-opaque for this to work correctly with custom painting
                setContentAreaFilled(false);
                super.paintComponent(g);

                g2.dispose();
            }

             @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                // Add padding horizontally
                size.width += 30;
                size.height = Math.max(size.height, 40); // Ensure minimum height
                return size;
            }

            // Prevent default border painting
             @Override
            public void setBorder(Border border) {
                // Ignore external border setting if needed, or use EmptyBorder for padding
            }
        };

        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY_COLOR); // Set initial background
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 25, 10, 25)); // Padding via border
        button.setContentAreaFilled(false); // Important for custom painting
        button.setOpaque(false); // Needs to be false for paintComponent background to show
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Repaint on hover/press/enable changes
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                 if(button.isEnabled()) button.repaint();
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                 if(button.isEnabled()) button.repaint();
            }
        });
        button.addPropertyChangeListener("enabled", evt -> button.repaint());

        return button;
    }

    // --- Helper Classes for Borders ---

    // RoundedBorder class (can be reused)
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
            // Adjust for thickness to draw border correctly inside/outside bounds
            g2d.draw(new RoundRectangle2D.Double(x + thickness / 2.0, y + thickness / 2.0,
                                                width - thickness, height - thickness, radius, radius));
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            int offs = radius / 2 > thickness ? radius / 2 : thickness;
            return new Insets(offs, offs, offs, offs);
        }

        @Override
        public boolean isBorderOpaque() {
            return false; // Allows background to show through
        }
    }

    // ShadowBorder class for subtle shadow effect (can be reused)
    private static class ShadowBorder extends AbstractBorder {
        private int shadowSize;
        private float shadowOpacity;
        private int cornerRadius;
        private Color shadowColor;

        public ShadowBorder(int size, float opacity, int radius, Color color) {
            this.shadowSize = size;
            this.shadowOpacity = opacity;
            this.cornerRadius = radius;
            this.shadowColor = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int baseAlpha = shadowColor.getAlpha();
            for (int i = 0; i < shadowSize; i++) {
                float alpha = shadowOpacity * (1.0f - (float) i / shadowSize);
                g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), (int) (baseAlpha * alpha)));
                // Draw slightly larger rounded rect for shadow effect
                 g2.draw(new RoundRectangle2D.Double(x + i, y + i, width - 1 - i * 2, height - 1 - i * 2, cornerRadius, cornerRadius));
            }
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
        }
    }
} 