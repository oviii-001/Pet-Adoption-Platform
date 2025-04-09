import javax.swing.SwingUtilities;
import model.Database;
import view.MainFrame;

public class Main {

    public static void main(String[] args) {
        // Initialize database connection and create tables if they don't exist
        System.out.println("Initializing database...");
        Database.initializeDatabase(); // This now includes sample data insertion check

        // Schedule the GUI creation and display for the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("Creating and showing GUI...");
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            }
        });

        // Add a shutdown hook to close the database connection gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook triggered. Closing database connection...");
            Database.closeConnection();
        }));

        System.out.println("Application startup sequence initiated.");
    }
}