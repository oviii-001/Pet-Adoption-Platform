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
import java.util.function.BiConsumer;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.Border;

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
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 50); // Shadow color
    private static final Color HOVER_BORDER_COLOR = new Color(52, 152, 219); // Border color on hover

    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 36); // Increased title font size
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
        // Ensure header doesn't stretch vertically
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, headerPanel.getPreferredSize().height));

        // Add title with shadow effect
        JLabel titleLabel = new JLabel("Available Pets");
        titleLabel.setFont(TITLE_FONT);
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
        // Use GridLayout with 3 columns and increased gaps
        petsPanel.setLayout(new GridLayout(0, 3, 25, 25));
        petsPanel.setBackground(BACKGROUND_COLOR);
        petsPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25)); // Increased padding

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
        filterPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        filterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        ActionListener filterActionListener = e -> filterPets();

        typeComboBox = new JComboBox<>(new String[]{"All Types", "Dog", "Cat", "Bird", "Other"});
        typeComboBox.addActionListener(filterActionListener);
        ageComboBox = new JComboBox<>(new String[]{"All Ages", "Puppy/Kitten", "Young", "Adult", "Senior"});
        ageComboBox.addActionListener(filterActionListener);
        genderComboBox = new JComboBox<>(new String[]{"All Genders", "Male", "Female"});
        genderComboBox.addActionListener(filterActionListener);

        filterPanel.add(createFilterRow("Type:", typeComboBox));
        filterPanel.add(createFilterRow("Age:", ageComboBox));
        filterPanel.add(createFilterRow("Gender:", genderComboBox));

        return filterPanel;
    }

    private JPanel createFilterRow(String labelText, JComboBox<String> comboBox) {
        JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(false);
        // Use FlowLayout for simple horizontal arrangement
        rowPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        // Remove fixed size constraints to allow FlowLayout to manage size
        // rowPanel.setMaximumSize(new Dimension(200, 80));

        // Create label with improved styling
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Slightly smaller font
        label.setForeground(Color.WHITE); // Label color on gradient
        // Make label background transparent
        // label.setBackground(new Color(52, 152, 219, 150));
        // label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5)); // Adjust padding
        // label.setAlignmentX(Component.LEFT_ALIGNMENT); // Not needed with FlowLayout
        rowPanel.add(label);
        // rowPanel.add(Box.createVerticalStrut(5)); // Not needed with FlowLayout

        // Style combo box
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE); // White background
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)), // Lighter border
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        comboBox.setPreferredSize(new Dimension(150, 35)); // Set preferred size
        // comboBox.setMaximumSize(new Dimension(200, 35)); // Remove max size
        comboBox.setFocusable(false);
        // comboBox.setAlignmentX(Component.LEFT_ALIGNMENT); // Not needed with FlowLayout

        // Add hover effect (optional, can be distracting on combo boxes)
        /*
        comboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // comboBox.setBackground(new Color(245, 245, 245));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // comboBox.setBackground(Color.WHITE);
            }
        });
        */

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
                // Removed fixed PreferredSize to allow GridLayout to manage size
                // card.setPreferredSize(new Dimension(450, 350));
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
            .filter(pet -> selectedType.equals("All Types") || pet.getType().equals(selectedType))
            .filter(pet -> selectedAge.equals("All Ages") || matchesAgeCategory(pet.getAge(), selectedAge))
            .filter(pet -> selectedGender.equals("All Genders") || pet.getGender().equals(selectedGender))
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
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(250, 350));

        // Add shadow effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(220, 220, 220));
        imageLabel.setPreferredSize(new Dimension(200, 150));
        imageLabel.setMinimumSize(new Dimension(150, 120));
        imageLabel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        imageLabel.setText("Image loading...");

        if (pet.getImageData() != null && pet.getImageData().length > 0) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(pet.getImageData());
                BufferedImage originalImage = ImageIO.read(bis);
                if (originalImage != null) {
                    int labelWidth = 200;
                    int labelHeight = 150;

                    // Calculate dimensions for cropping
                    double originalRatio = (double) originalImage.getWidth() / originalImage.getHeight();
                    double targetRatio = (double) labelWidth / labelHeight;
                    
                    int cropWidth;
                    int cropHeight;
                    int x = 0;
                    int y = 0;
                    
                    if (originalRatio > targetRatio) {
                        // Original image is wider - crop width
                        cropHeight = originalImage.getHeight();
                        cropWidth = (int) (cropHeight * targetRatio);
                        x = (originalImage.getWidth() - cropWidth) / 2;
                    } else {
                        // Original image is taller - crop height
                        cropWidth = originalImage.getWidth();
                        cropHeight = (int) (cropWidth / targetRatio);
                        y = (originalImage.getHeight() - cropHeight) / 2;
                    }

                    // Crop the image
                    BufferedImage croppedImage = originalImage.getSubimage(x, y, cropWidth, cropHeight);
                    
                    // Scale the cropped image to fit the label exactly
                    Image scaledImage = croppedImage.getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);
                    
                    ImageIcon icon = new ImageIcon(scaledImage);
                    imageLabel.setIcon(icon);
                    imageLabel.setText(null);
                    imageLabel.setBackground(CARD_COLOR);
                } else {
                    imageLabel.setText("Invalid image");
                }
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
                imageLabel.setText("Image error");
            }
        } else {
            imageLabel.setText("No image");
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(imageLabel, gbc);

        // Pet name (centered below image)
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 10, 0);
        JLabel nameLabel = new JLabel(pet.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel, gbc);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        GridBagConstraints infoGBC = new GridBagConstraints();
        infoGBC.anchor = GridBagConstraints.LINE_START;
        infoGBC.insets = new Insets(1, 5, 1, 5);

        BiConsumer<String, String> addInfoRow = (labelText, valueText) -> {
            JLabel label = new JLabel(labelText);
            label.setFont(PET_INFO_FONT.deriveFont(Font.BOLD));
            label.setForeground(TEXT_COLOR.darker());
            infoGBC.gridx = 0;
            infoGBC.gridy++;
            infoGBC.weightx = 0.0;
            infoGBC.fill = GridBagConstraints.NONE;
            infoPanel.add(label, infoGBC);

            JLabel value = new JLabel(valueText);
            value.setFont(PET_INFO_FONT);
            value.setForeground(TEXT_COLOR);
            infoGBC.gridx = 1;
            infoGBC.weightx = 1.0;
            infoGBC.fill = GridBagConstraints.HORIZONTAL;
            infoPanel.add(value, infoGBC);
        };

        addInfoRow.accept("Breed:", pet.getBreed());
        addInfoRow.accept("Age:", String.valueOf(pet.getAge()) + " (" + getAgeCategory(pet.getAge()) + ")");
        addInfoRow.accept("Gender:", pet.getGender());
        addInfoRow.accept("Status:", pet.getStatus());

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        card.add(infoPanel, gbc);

        JButton applyButton = new JButton("View Details / Apply");
        applyButton.setFont(BUTTON_FONT);
        applyButton.setFocusPainted(false);
        applyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if ("available".equalsIgnoreCase(pet.getStatus())) {
            applyButton.setBackground(PRIMARY_COLOR);
            applyButton.setForeground(Color.WHITE);
            applyButton.addActionListener(e -> mainFrame.navigateToApplicationForm(pet.getPetId()));
            applyButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    applyButton.setBackground(PRIMARY_COLOR.darker());
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    applyButton.setBackground(PRIMARY_COLOR);
                }
            });
        } else {
            applyButton.setBackground(Color.LIGHT_GRAY);
            applyButton.setForeground(Color.DARK_GRAY);
            applyButton.setText(pet.getStatus().substring(0, 1).toUpperCase() + pet.getStatus().substring(1));
            applyButton.setEnabled(false);
        }
        applyButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(applyButton, gbc);

        return card;
    }

    // Helper method to get age category string
    private String getAgeCategory(int age) {
        if (age <= 1) return "Puppy/Kitten";
        if (age <= 3) return "Young";
        if (age <= 7) return "Adult";
        return "Senior";
    }

    public void refreshPetList() {
        filterPets(); // Refresh by re-filtering
        // Optional: Scroll to top after refresh
        // SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }
}