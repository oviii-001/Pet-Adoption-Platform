package view;

import controller.PetController;
import model.Pet;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.awt.image.BufferedImage;
import java.util.stream.Collectors;

public class PetBrowsePanel extends JPanel {
    private MainFrame mainFrame;
    private PetController petController;
    private JPanel petsPanel;

    // Colors
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(200, 200, 200);

    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font PET_NAME_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font PET_INFO_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private JComboBox<String> typeComboBox;
    private JComboBox<String> ageComboBox;
    private JComboBox<String> genderComboBox;

    public PetBrowsePanel(MainFrame mainFrame, PetController petController) {
        this.mainFrame = mainFrame;
        this.petController = petController;
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(10, 10)); // Add gaps between components

        // Create header panel with gradient background
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(52, 152, 219);
                Color color2 = new Color(41, 128, 185);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        // Add title with shadow effect
        JLabel titleLabel = new JLabel("Available Pets");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.add(titleLabel);

        // Add filter panel with modern design
        JPanel filterPanel = createFilterPanel();
        headerPanel.add(filterPanel);
        add(headerPanel, BorderLayout.NORTH);

        // Create pets panel with modern scrollbar
        petsPanel = new JPanel();
        petsPanel.setLayout(new GridLayout(0, 2, 20, 20)); // 2 columns with gaps
        petsPanel.setBackground(BACKGROUND_COLOR);
        petsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(petsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        
        // Customize scrollbar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setBackground(BACKGROUND_COLOR);
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(52, 152, 219);
                this.trackColor = BACKGROUND_COLOR;
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        // Load pets
        loadPets();
        
        // Force initial layout update
        revalidate();
        repaint();
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setOpaque(false);
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
        filterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Create filter rows
        typeComboBox = new JComboBox<>(new String[]{"All", "Dog", "Cat", "Bird"});
        ageComboBox = new JComboBox<>(new String[]{"All", "Puppy/Kitten", "Young", "Adult", "Senior"});
        genderComboBox = new JComboBox<>(new String[]{"All", "Male", "Female"});

        // Add filter rows to panel
        JPanel typeRow = createFilterRow("Type:", typeComboBox);
        JPanel ageRow = createFilterRow("Age:", ageComboBox);
        JPanel genderRow = createFilterRow("Gender:", genderComboBox);

        filterPanel.add(typeRow);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(ageRow);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(genderRow);

        // Add filter button with modern styling
        JButton filterButton = new JButton("Filter");
        filterButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterButton.setBackground(new Color(46, 204, 113));
        filterButton.setForeground(Color.WHITE);
        filterButton.setFocusPainted(false);
        filterButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 174, 96), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        filterButton.setMaximumSize(new Dimension(120, 40));
        filterButton.addActionListener(e -> {
            filterPets();
        });

        // Add hover effect
        filterButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                filterButton.setBackground(new Color(39, 174, 96));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                filterButton.setBackground(new Color(46, 204, 113));
            }
        });

        // Add button next to gender dropdown
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(filterButton);

        return filterPanel;
    }

    private JPanel createFilterRow(String labelText, JComboBox<String> comboBox) {
        JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(false);
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.Y_AXIS));
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowPanel.setMaximumSize(new Dimension(200, 80));

        // Create label with improved styling
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(255, 255, 255));
        label.setBackground(new Color(52, 152, 219, 150));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowPanel.add(label);
        rowPanel.add(Box.createVerticalStrut(5));

        // Style combo box
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(new Color(255, 255, 255));
        comboBox.setForeground(new Color(44, 62, 80));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        comboBox.setMaximumSize(new Dimension(200, 35));
        comboBox.setFocusable(false);
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add hover effect
        comboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                comboBox.setBackground(new Color(245, 245, 245));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                comboBox.setBackground(new Color(255, 255, 255));
            }
        });
        
        rowPanel.add(comboBox);

        return rowPanel;
    }

    private void loadPets() {
        System.out.println("\nDEBUG: ===== Starting loadPets() =====");
        petsPanel.removeAll();
        List<Pet> pets = petController.getAllPets();

        if (pets.isEmpty()) {
            System.out.println("DEBUG: No pets found");
            JLabel noPetsLabel = new JLabel("No pets available at the moment.");
            noPetsLabel.setFont(PET_INFO_FONT);
            noPetsLabel.setForeground(TEXT_COLOR);
            noPetsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            petsPanel.add(noPetsLabel);
        } else {
            System.out.println("DEBUG: Found " + pets.size() + " pets");
            for (Pet pet : pets) {
                System.out.println("DEBUG: Creating card for pet: " + pet.getName());
                JPanel card = createPetCard(pet);
                card.setPreferredSize(new Dimension(450, 350));
                petsPanel.add(card);
            }
        }

        System.out.println("DEBUG: Calling revalidate() and repaint()");
        petsPanel.revalidate();
        petsPanel.repaint();
        
        // Also revalidate and repaint the parent containers
        Container parent = petsPanel.getParent();
        while (parent != null) {
            System.out.println("DEBUG: Revalidating parent: " + parent.getClass().getName());
            parent.revalidate();
            parent.repaint();
            parent = parent.getParent();
        }
        System.out.println("DEBUG: ===== loadPets() complete =====\n");
    }

    private void filterPets() {
        String selectedType = (String) typeComboBox.getSelectedItem();
        String selectedAge = (String) ageComboBox.getSelectedItem();
        String selectedGender = (String) genderComboBox.getSelectedItem();
        
        // Clear current pets
        petsPanel.removeAll();
        
        // Filter pets based on selected criteria
        List<Pet> filteredPets = petController.getAllPets().stream()
            .filter(pet -> selectedType.equals("All") || pet.getType().equals(selectedType))
            .filter(pet -> selectedAge.equals("All") || matchesAgeCategory(pet.getAge(), selectedAge))
            .filter(pet -> selectedGender.equals("All") || pet.getGender().equals(selectedGender))
            .collect(Collectors.toList());
        
        // Add filtered pets to panel
        for (Pet pet : filteredPets) {
            petsPanel.add(createPetCard(pet));
        }
        
        // Update UI
        petsPanel.revalidate();
        petsPanel.repaint();
        
        // Show message
        JOptionPane.showMessageDialog(this, 
            "Showing " + filteredPets.size() + " pets matching your criteria",
            "Filter Applied",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean matchesAgeCategory(int age, String category) {
        switch (category) {
            case "Puppy/Kitten":
                return age <= 1;
            case "Young":
                return age > 1 && age <= 3;
            case "Adult":
                return age > 3 && age <= 7;
            case "Senior":
                return age > 7;
            default:
                return true;
        }
    }

    private JPanel createPetCard(Pet pet) {
        System.out.println("\nDEBUG: ===== Creating card for pet: " + pet.getName() + " =====");
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Image panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(CARD_COLOR);
        imagePanel.setPreferredSize(new Dimension(200, 200));
        imagePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 200));
        
        if (pet.getImageData() != null && pet.getImageData().length > 0) {
            ByteArrayInputStream bis = null;
            try {
                System.out.println("DEBUG: Starting image load for pet: " + pet.getName());
                System.out.println("DEBUG: Image data size: " + pet.getImageData().length + " bytes");
                
                // Check if the image data starts with valid image magic numbers
                byte[] header = new byte[4];
                System.arraycopy(pet.getImageData(), 0, header, 0, Math.min(4, pet.getImageData().length));
                System.out.println("DEBUG: First few bytes: " + 
                    String.format("%02X %02X %02X %02X", header[0], header[1], header[2], header[3]));
                
                // Common image format magic numbers
                System.out.println("DEBUG: Checking image format...");
                if (header[0] == (byte)0xFF && header[1] == (byte)0xD8) {
                    System.out.println("DEBUG: Image appears to be JPEG");
                } else if (header[0] == (byte)0x89 && header[1] == (byte)0x50) {
                    System.out.println("DEBUG: Image appears to be PNG");
                } else if (header[0] == (byte)0x47 && header[1] == (byte)0x49) {
                    System.out.println("DEBUG: Image appears to be GIF");
                } else {
                    System.out.println("DEBUG: Unknown image format");
                }
                
                bis = new ByteArrayInputStream(pet.getImageData());
                System.out.println("DEBUG: Created ByteArrayInputStream");
                
                BufferedImage originalImage = ImageIO.read(bis);
                System.out.println("DEBUG: Called ImageIO.read");
                
                if (originalImage != null) {
                    System.out.println("DEBUG: Image loaded successfully");
                    System.out.println("DEBUG: Original dimensions - Width: " + originalImage.getWidth() + 
                                     ", Height: " + originalImage.getHeight() +
                                     ", Type: " + originalImage.getType());
                    
                    // Calculate dimensions maintaining aspect ratio
                    int targetWidth = 200;
                    int targetHeight = 200;
                    
                    double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
                    System.out.println("DEBUG: Aspect ratio: " + aspectRatio);
                    
                    if (aspectRatio > 1) {
                        // Image is wider than tall
                        targetHeight = (int) (targetWidth / aspectRatio);
                    } else {
                        // Image is taller than wide
                        targetWidth = (int) (targetHeight * aspectRatio);
                    }
                    
                    System.out.println("DEBUG: Target dimensions - Width: " + targetWidth + 
                                     ", Height: " + targetHeight);
                    
                    // Create a new BufferedImage with the correct type and dimensions
                    BufferedImage scaledImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = scaledImage.createGraphics();
                    try {
                        // Set background to white
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(0, 0, 200, 200);
                        
                        // Calculate position to center the image
                        int x = (200 - targetWidth) / 2;
                        int y = (200 - targetHeight) / 2;
                        
                        System.out.println("DEBUG: Drawing position - X: " + x + ", Y: " + y);
                        
                        // Set rendering hints for better quality
                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Draw the scaled image
                        g2d.drawImage(originalImage, x, y, targetWidth, targetHeight, null);
                        System.out.println("DEBUG: Image drawn successfully");
                        
                        // Create ImageIcon and verify it's not null
                        ImageIcon icon = new ImageIcon(scaledImage);
                        System.out.println("DEBUG: Created ImageIcon - Width: " + icon.getIconWidth() + 
                                         ", Height: " + icon.getIconHeight());
                        
                        if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
                            throw new IllegalStateException("Created ImageIcon has invalid dimensions");
                        }
                        
                        imageLabel.setIcon(icon);
                        System.out.println("DEBUG: Icon set on label successfully");
                        
                        // Force the image label to update
                        imageLabel.validate();
                        imageLabel.repaint();
                        System.out.println("DEBUG: Image label updated");
                        
                        System.out.println("DEBUG: ===== Image loading complete =====\n");
                    } finally {
                        g2d.dispose(); // Ensure graphics context is disposed
                        System.out.println("DEBUG: Graphics context disposed");
                    }
                } else {
                    System.err.println("ERROR: ImageIO.read returned null for pet: " + pet.getName());
                    imageLabel.setText("Image Error");
                    imageLabel.setForeground(TEXT_COLOR);
                }
            } catch (Exception ex) {
                System.err.println("\nERROR: Failed to load image for pet: " + pet.getName());
                System.err.println("ERROR: Exception type: " + ex.getClass().getName());
                System.err.println("ERROR: Message: " + ex.getMessage());
                ex.printStackTrace();
                imageLabel.setText("Image Error");
                imageLabel.setForeground(TEXT_COLOR);
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                        System.out.println("DEBUG: ByteArrayInputStream closed");
                    } catch (IOException e) {
                        System.err.println("ERROR: Failed to close ByteArrayInputStream: " + e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("\nDEBUG: No image data for pet: " + pet.getName() + 
                             ", imageData is " + (pet.getImageData() == null ? "null" : "empty"));
            imageLabel.setText("No Image");
            imageLabel.setForeground(TEXT_COLOR);
        }

        imagePanel.add(imageLabel, BorderLayout.CENTER);
        card.add(imagePanel, BorderLayout.CENTER);

        // Details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel(pet.getName());
        nameLabel.setFont(PET_NAME_FONT);
        nameLabel.setForeground(PRIMARY_COLOR);
        detailsPanel.add(nameLabel, gbc);

        // Type and Age
        gbc.gridy++;
        JLabel typeAgeLabel = new JLabel(pet.getType() + " • " + pet.getAge() + " years");
        typeAgeLabel.setFont(PET_INFO_FONT);
        typeAgeLabel.setForeground(TEXT_COLOR);
        detailsPanel.add(typeAgeLabel, gbc);

        // Breed
        gbc.gridy++;
        JLabel breedLabel = new JLabel("Breed: " + pet.getBreed());
        breedLabel.setFont(PET_INFO_FONT);
        breedLabel.setForeground(TEXT_COLOR);
        detailsPanel.add(breedLabel, gbc);

        // Gender
        gbc.gridy++;
        JLabel genderLabel = new JLabel("Gender: " + pet.getGender());
        genderLabel.setFont(PET_INFO_FONT);
        genderLabel.setForeground(TEXT_COLOR);
        detailsPanel.add(genderLabel, gbc);

        // Health Status
        gbc.gridy++;
        JLabel healthLabel = new JLabel("Health: " + pet.getHealthStatus());
        healthLabel.setFont(PET_INFO_FONT);
        healthLabel.setForeground(TEXT_COLOR);
        detailsPanel.add(healthLabel, gbc);

        // Temperament
        gbc.gridy++;
        JLabel tempLabel = new JLabel("Temperament: " + pet.getTemperament());
        tempLabel.setFont(PET_INFO_FONT);
        tempLabel.setForeground(TEXT_COLOR);
        detailsPanel.add(tempLabel, gbc);

        // Description
        gbc.gridy++;
        JTextArea descArea = new JTextArea(pet.getDescription());
        descArea.setFont(PET_INFO_FONT);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(CARD_COLOR);
        descArea.setForeground(TEXT_COLOR);
        descArea.setPreferredSize(new Dimension(300, 60));
        detailsPanel.add(descArea, gbc);

        // Apply button
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        JButton applyButton = new JButton("Apply to Adopt");
        applyButton.setFont(BUTTON_FONT);
        applyButton.setBackground(PRIMARY_COLOR);
        applyButton.setForeground(Color.WHITE);
        applyButton.setFocusPainted(false);
        applyButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        applyButton.addActionListener(e -> mainFrame.navigateToApplicationForm(pet.getPetId()));

        // Add hover effect
        applyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                applyButton.setBackground(new Color(41, 128, 185));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                applyButton.setBackground(PRIMARY_COLOR);
            }
        });

        detailsPanel.add(applyButton, gbc);

        card.add(detailsPanel, BorderLayout.SOUTH);
        
        // Force the card to update
        card.validate();
        card.repaint();
        System.out.println("DEBUG: Card created and updated for pet: " + pet.getName());

        return card;
    }

    public void refreshPetList() {
        loadPets();
    }
}