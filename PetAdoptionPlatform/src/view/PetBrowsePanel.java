package view;

import controller.PetController;
import model.Pet;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

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
        setLayout(new BorderLayout());

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
        petsPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        petsPanel.setBackground(BACKGROUND_COLOR);
        petsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(petsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
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
            String type = (String) typeComboBox.getSelectedItem();
            String age = (String) ageComboBox.getSelectedItem();
            String gender = (String) genderComboBox.getSelectedItem();
            
            List<Pet> filteredPets = petController.getAllPets().stream()
                .filter(pet -> type.equals("All") || pet.getType().equalsIgnoreCase(type))
                .filter(pet -> age.equals("All") || matchesAgeCategory(pet.getAge(), age))
                .filter(pet -> gender.equals("All") || pet.getGender().equalsIgnoreCase(gender))
                .toList();
            
            displayFilteredPets(filteredPets);
            
            JOptionPane.showMessageDialog(this,
                "Filters applied successfully!",
                "Filter Status",
                JOptionPane.INFORMATION_MESSAGE);
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
        petsPanel.removeAll();
        List<Pet> pets = petController.getAllPets();

        if (pets.isEmpty()) {
            JLabel noPetsLabel = new JLabel("No pets available at the moment.");
            noPetsLabel.setFont(PET_INFO_FONT);
            noPetsLabel.setForeground(TEXT_COLOR);
            noPetsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            petsPanel.add(noPetsLabel);
        } else {
            for (Pet pet : pets) {
                JPanel card = createPetCard(pet);
                card.setPreferredSize(new Dimension(450, 350));
                petsPanel.add(card);
            }
        }

        petsPanel.revalidate();
        petsPanel.repaint();
    }

    private void displayFilteredPets(List<Pet> pets) {
        petsPanel.removeAll();

        if (pets.isEmpty()) {
            JLabel noPetsLabel = new JLabel("No pets match the selected filters.");
            noPetsLabel.setFont(PET_INFO_FONT);
            noPetsLabel.setForeground(TEXT_COLOR);
            noPetsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            petsPanel.add(noPetsLabel);
        } else {
            for (Pet pet : pets) {
                JPanel card = createPetCard(pet);
                card.setPreferredSize(new Dimension(450, 350));
                petsPanel.add(card);
            }
        }

        petsPanel.revalidate();
        petsPanel.repaint();
    }

    private JPanel createPetCard(Pet pet) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(450, 350));

        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        });

        // Image panel with rounded corners
        JPanel imagePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        imagePanel.setPreferredSize(new Dimension(180, 180));
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Load pet image
        String imagePath = "resources/pets/" + pet.getName().toLowerCase().replaceAll("\\s+", "") + ".jpg";
        ImageIcon petIcon = new ImageIcon(imagePath);
        if (petIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            Image scaledImage = petIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePanel.add(imageLabel, BorderLayout.CENTER);
        } else {
            JLabel placeholder = new JLabel("No Image");
            placeholder.setHorizontalAlignment(JLabel.CENTER);
            placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            placeholder.setForeground(Color.GRAY);
            imagePanel.add(placeholder, BorderLayout.CENTER);
        }

        // Details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name with icon
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel(pet.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(PRIMARY_COLOR);
        detailsPanel.add(nameLabel, gbc);

        // Type and Age
        gbc.gridy++;
        JLabel typeAgeLabel = new JLabel(pet.getType() + " • " + String.valueOf(pet.getAge()) + " years");
        typeAgeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        typeAgeLabel.setForeground(TEXT_COLOR);
        detailsPanel.add(typeAgeLabel, gbc);

        // Breed
        gbc.gridy++;
        JLabel breedLabel = new JLabel("Breed: " + pet.getBreed());
        breedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        breedLabel.setForeground(TEXT_COLOR);
        detailsPanel.add(breedLabel, gbc);

        // Gender
        gbc.gridy++;
        JLabel genderLabel = new JLabel("Gender: " + pet.getGender());
        genderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        genderLabel.setForeground(TEXT_COLOR);
        detailsPanel.add(genderLabel, gbc);

        // Health Status
        gbc.gridy++;
        JLabel healthLabel = new JLabel("Health: " + pet.getHealthStatus());
        healthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        healthLabel.setForeground(TEXT_COLOR);
        detailsPanel.add(healthLabel, gbc);

        // Temperament
        gbc.gridy++;
        JLabel tempLabel = new JLabel("Temperament: " + pet.getTemperament());
        tempLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tempLabel.setForeground(TEXT_COLOR);
        detailsPanel.add(tempLabel, gbc);

        // Description
        gbc.gridy++;
        JTextArea descArea = new JTextArea(pet.getDescription());
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(Color.WHITE);
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
        applyButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
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

        // Add panels to card
        card.add(imagePanel, BorderLayout.WEST);
        card.add(detailsPanel, BorderLayout.CENTER);

        return card;
    }

    private boolean matchesAgeCategory(int age, String ageCategory) {
        switch (ageCategory) {
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

    public void refreshPetList() {
        loadPets();
    }
}