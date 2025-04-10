package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    // Colors
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Create a container panel for centering
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        // Create login panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(BACKGROUND_COLOR);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        loginPanel.setMaximumSize(new Dimension(400, 500));

        // Add title
        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createVerticalStrut(30));

        // Add username field
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
        usernamePanel.setBackground(BACKGROUND_COLOR);
        usernamePanel.setMaximumSize(new Dimension(400, 50));
        usernamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(LABEL_FONT);
        usernameLabel.setForeground(TEXT_COLOR);
        usernameLabel.setPreferredSize(new Dimension(100, 30));
        usernamePanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(INPUT_FONT);
        usernameField.setMaximumSize(new Dimension(300, 40));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        usernamePanel.add(usernameField);

        loginPanel.add(usernamePanel);
        loginPanel.add(Box.createVerticalStrut(20));

        // Add password field
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
        passwordPanel.setBackground(BACKGROUND_COLOR);
        passwordPanel.setMaximumSize(new Dimension(400, 50));
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(LABEL_FONT);
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setPreferredSize(new Dimension(100, 30));
        passwordPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(INPUT_FONT);
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passwordPanel.add(passwordField);

        loginPanel.add(passwordPanel);
        loginPanel.add(Box.createVerticalStrut(30));

        // Add login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(BUTTON_FONT);
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            if (username.equals("admin") && password.equals("admin")) {
                mainFrame.setAdminMode(true);
                mainFrame.showPanel("AdminManagePets");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add hover effect
        loginButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(41, 128, 185));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(PRIMARY_COLOR);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseClicked(MouseEvent e) {}
        });

        loginPanel.add(loginButton);

        // Add login panel to center container
        centerContainer.add(loginPanel, gbc);

        // Add center container to main panel
        add(centerContainer, BorderLayout.CENTER);
    }
} 