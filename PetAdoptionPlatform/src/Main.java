import javax.swing.SwingUtilities;
import model.Database;
import view.MainFrame;

public class Main {

    public static void main(String[] args) {
        System.out.println("Initializing database...");
        Database.initializeDatabase();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("Creating and showing GUI...");
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            }
        });

       
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook triggered. Closing database connection...");
            Database.closeConnection();
        }));

        System.out.println("Application startup sequence initiated.");
    }
}