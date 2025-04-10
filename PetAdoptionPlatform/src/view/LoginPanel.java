package view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    // Removed loginButton field as it's created and added locally

    // Using admin/admin for simpler testing - CHANGE THIS FOR PRODUCTION
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin"; // Changed from "admin123"

    // Consistent Colors & Fonts from WelcomePanel
    private static final Color PRIMARY_COLOR = new Color(60, 90, 160); // Deeper Blue
    private static final Color SECONDARY_COLOR = new Color(230, 126, 34); // Orange accent
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Lighter Gray
    private static final Color TEXT_COLOR = new Color(52, 73, 94); // Dark Gray/Blue
    private static final Color INPUT_BG_COLOR = new Color(255, 255, 255); // White
    private static final Color INPUT_BORDER_COLOR = new Color(200, 200, 200); // Light Gray
    private static final Color BUTTON_HOVER_COLOR = new Color(45, 75, 140); // Darker Blue for Hover

    private static final Font TITLE_FONT = new Font("Segoe UI Semibold", Font.BOLD, 28);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout()); // Use GridBagLayout for the main panel to center content
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Login Form Panel (centered)
        JPanel loginFormPanel = new JPanel();
        loginFormPanel.setLayout(new BoxLayout(loginFormPanel, BoxLayout.Y_AXIS));
        loginFormPanel.setBackground(Color.WHITE); // White background for the form area
        loginFormPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(INPUT_BORDER_COLOR, 1, 15), // Rounded border
            new EmptyBorder(40, 50, 40, 50) // Padding inside
        ));
        loginFormPanel.setMaximumSize(new Dimension(400, 400)); // Constrain size
        loginFormPanel.setPreferredSize(new Dimension(400, 350));

        // Add Title
        JLabel titleLabel = new JLabel("Administrator Login");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginFormPanel.add(titleLabel);
        loginFormPanel.add(Box.createVerticalStrut(30)); // Space after title

        // Username Input Row
        usernameField = createTextField();
        loginFormPanel.add(createInputRow("Username", usernameField));
        loginFormPanel.add(Box.createVerticalStrut(15)); // Space between rows

        // Password Input Row
        passwordField = createPasswordField();
        loginFormPanel.add(createInputRow("Password", passwordField));
        loginFormPanel.add(Box.createVerticalStrut(30)); // Space before button

        // Login Button
        JButton loginButton = createLoginButton();
        loginFormPanel.add(loginButton);

        // Add the form panel to the main panel (centered)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginFormPanel, gbc);
    }

    private JPanel createInputRow(String labelText, JComponent inputField) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0)); // 10px horizontal gap
        rowPanel.setBackground(Color.WHITE); // Match form background
        JLabel label = createLabel(labelText);
        label.setPreferredSize(new Dimension(80, 30)); // Give label fixed width
        rowPanel.add(label, BorderLayout.WEST);
        rowPanel.add(inputField, BorderLayout.CENTER);
        rowPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Align row panel itself centrally
        // Constrain the height of the row panel
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); 
        return rowPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT); // Keep label text left-aligned
        label.setBorder(new EmptyBorder(0, 5, 5, 0));
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(INPUT_FONT);
        textField.setBackground(INPUT_BG_COLOR);
        textField.setForeground(TEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(INPUT_BORDER_COLOR, 1, 10),
            new EmptyBorder(8, 12, 8, 12)
        ));
        // textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Removed, row controls size
        // textField.setAlignmentX(Component.CENTER_ALIGNMENT); // Removed, BorderLayout handles alignment
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(INPUT_FONT);
        passwordField.setBackground(INPUT_BG_COLOR);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(INPUT_BORDER_COLOR, 1, 10),
            new EmptyBorder(8, 12, 8, 12)
        ));
        // passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Removed, row controls size
        // passwordField.setAlignmentX(Component.CENTER_ALIGNMENT); // Removed, BorderLayout handles alignment
        return passwordField;
    }

    private JButton createLoginButton() {
         JButton button = new JButton("Login") {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Rounded corners
                super.paintComponent(g);
                g2.dispose();
            }
        };

        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 30, 12, 30)); // Padding
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(150, 45)); // Control button size
        button.setPreferredSize(new Dimension(150, 45));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                button.repaint();
            }
        });

        // Action Listener
        button.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
                mainFrame.setAdminMode(true); // Set admin mode
                mainFrame.showPanel("AdminManagePets"); // Show admin dashboard
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password. Please try again.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
                passwordField.setText(""); // Clear password field on failure
                usernameField.requestFocusInWindow(); // Focus username field
            }
        });

        return button;
    }

    // Reusing RoundedBorder class from WelcomePanel (ensure it's accessible or copy it here)
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