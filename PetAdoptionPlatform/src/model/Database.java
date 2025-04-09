package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:pet_adoption.db";
    private static Connection connection;

    // --- Connection Management ---

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Ensure the JDBC driver is loaded
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Database connection established.");
            } catch (ClassNotFoundException e) {
                System.err.println("SQLite JDBC driver not found.");
                throw new SQLException("SQLite JDBC driver not found.", e);
            }
        }
        return connection;
    }

    public static void initializeDatabase() {
        // Combined table creation script
        String sql = """
            CREATE TABLE IF NOT EXISTS Pet (
                pet_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                type TEXT,
                size TEXT,
                age INTEGER,
                description TEXT,
                status TEXT DEFAULT 'available'
            );

            CREATE TABLE IF NOT EXISTS Adopter (
                adopter_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                contact_info TEXT,
                preferences TEXT
            );

            CREATE TABLE IF NOT EXISTS Application (
                application_id INTEGER PRIMARY KEY AUTOINCREMENT,
                adopter_id INTEGER NOT NULL,
                pet_id INTEGER NOT NULL,
                status TEXT DEFAULT 'pending',
                FOREIGN KEY (adopter_id) REFERENCES Adopter(adopter_id) ON DELETE CASCADE,
                FOREIGN KEY (pet_id) REFERENCES Pet(pet_id) ON DELETE CASCADE
            );
            """; // Using TEXT default values directly

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // Execute multiple statements if needed (JDBC drivers handle semicolon separation)
            stmt.executeUpdate(sql); // Use executeUpdate for DDL
            System.out.println("Database tables checked/created successfully.");
            insertSampleData(); // Add some initial data
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertSampleData() {
        // Very basic check to prevent re-inserting every time
        if (getAllPets().isEmpty()) {
            System.out.println("Inserting sample data...");
            try {
                addPet(new Pet(0, "Buddy", "Dog", "Medium", 3, "Friendly Golden Retriever", "available"));
                addPet(new Pet(0, "Whiskers", "Cat", "Small", 2, "Shy but sweet tabby", "available"));
                addPet(new Pet(0, "Rocky", "Dog", "Large", 5, "Energetic German Shepherd", "available"));
                addAdopter(new Adopter(0, "Alice Smith", "alice@email.com", "type:Dog,size:Medium"));
                System.out.println("Sample data inserted.");
            } catch (SQLException e) {
                System.err.println("Error inserting sample data: " + e.getMessage());
            }
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    // --- Pet Operations ---

    public static List<Pet> getAllPets() {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT * FROM Pet";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pets.add(new Pet(
                        rs.getInt("pet_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("size"),
                        rs.getInt("age"),
                        rs.getString("description"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pets: " + e.getMessage());
        }
        return pets;
    }

    public static List<Pet> getAvailablePets() {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT * FROM Pet WHERE status = 'available'";
        // Similar implementation to getAllPets, just add the WHERE clause
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pets.add(new Pet(
                        rs.getInt("pet_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("size"),
                        rs.getInt("age"),
                        rs.getString("description"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available pets: " + e.getMessage());
        }
        return pets;
    }


    public static Pet getPetById(int petId) {
        String sql = "SELECT * FROM Pet WHERE pet_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, petId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Pet(
                        rs.getInt("pet_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("size"),
                        rs.getInt("age"),
                        rs.getString("description"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pet by ID: " + e.getMessage());
        }
        return null; // Not found
    }

    public static boolean addPet(Pet pet) throws SQLException {
        String sql = "INSERT INTO Pet (name, type, size, age, description, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pet.getName());
            pstmt.setString(2, pet.getType());
            pstmt.setString(3, pet.getSize());
            pstmt.setInt(4, pet.getAge());
            pstmt.setString(5, pet.getDescription());
            pstmt.setString(6, pet.getStatus());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public static boolean updatePet(Pet pet) throws SQLException {
        String sql = "UPDATE Pet SET name = ?, type = ?, size = ?, age = ?, description = ?, status = ? WHERE pet_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pet.getName());
            pstmt.setString(2, pet.getType());
            pstmt.setString(3, pet.getSize());
            pstmt.setInt(4, pet.getAge());
            pstmt.setString(5, pet.getDescription());
            pstmt.setString(6, pet.getStatus());
            pstmt.setInt(7, pet.getPetId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public static boolean updatePetStatus(int petId, String status) throws SQLException {
        String sql = "UPDATE Pet SET status = ? WHERE pet_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, petId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }


    public static boolean deletePet(int petId) throws SQLException {
        // Consider handling related applications or use cascading delete
        String sql = "DELETE FROM Pet WHERE pet_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, petId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    // --- Adopter Operations ---

    public static Adopter getAdopterById(int adopterId) {
        String sql = "SELECT * FROM Adopter WHERE adopter_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, adopterId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Adopter(
                        rs.getInt("adopter_id"),
                        rs.getString("name"),
                        rs.getString("contact_info"),
                        rs.getString("preferences")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching adopter by ID: " + e.getMessage());
        }
        return null;
    }

    // Simple add adopter, returns the generated ID or -1 on failure
    public static int addAdopter(Adopter adopter) throws SQLException {
        String sql = "INSERT INTO Adopter (name, contact_info, preferences) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, adopter.getName());
            pstmt.setString(2, adopter.getContactInfo());
            pstmt.setString(3, adopter.getPreferences());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return the new ID
                    }
                }
            }
        }
        return -1; // Indicate failure
    }

    public static boolean updateAdopterPreferences(int adopterId, String preferences) throws SQLException {
        String sql = "UPDATE Adopter SET preferences = ? WHERE adopter_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, preferences);
            pstmt.setInt(2, adopterId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }


    // --- Application Operations ---

    public static List<Application> getAllApplications() {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM Application";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                applications.add(new Application(
                        rs.getInt("application_id"),
                        rs.getInt("adopter_id"),
                        rs.getInt("pet_id"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all applications: " + e.getMessage());
        }
        return applications;
    }

    public static List<Application> getApplicationsByAdopter(int adopterId) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM Application WHERE adopter_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, adopterId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                applications.add(new Application(
                        rs.getInt("application_id"),
                        rs.getInt("adopter_id"),
                        rs.getInt("pet_id"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching applications by adopter: " + e.getMessage());
        }
        return applications;
    }

    public static boolean addApplication(Application application) throws SQLException {
        // Check if pet is available before adding application
        Pet pet = getPetById(application.getPetId());
        if (pet == null || !"available".equalsIgnoreCase(pet.getStatus())) {
            System.err.println("Cannot apply for pet ID " + application.getPetId() + ". Pet not found or not available.");
            return false;
        }

        String sql = "INSERT INTO Application (adopter_id, pet_id, status) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, application.getAdopterId());
            pstmt.setInt(2, application.getPetId());
            pstmt.setString(3, application.getStatus()); // Should default to 'pending'
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public static boolean updateApplicationStatus(int applicationId, String status) throws SQLException {
        String sql = "UPDATE Application SET status = ? WHERE application_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, applicationId);
            int affectedRows = pstmt.executeUpdate();

            // If approved, update the corresponding pet's status to 'adopted'
            if (affectedRows > 0 && "approved".equalsIgnoreCase(status)) {
                Application app = getApplicationById(applicationId); // Need a helper for this
                if (app != null) {
                    updatePetStatus(app.getPetId(), "adopted");
                }
            }
            // Optional: If rejected, maybe set pet back to 'available' if it was held?
            // Current logic assumes pet becomes adopted *only* on approval.

            return affectedRows > 0;
        }
    }

    // Helper to get a single application (needed for status update logic)
    public static Application getApplicationById(int applicationId) {
        String sql = "SELECT * FROM Application WHERE application_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, applicationId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Application(
                        rs.getInt("application_id"),
                        rs.getInt("adopter_id"),
                        rs.getInt("pet_id"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching application by ID: " + e.getMessage());
        }
        return null;
    }
}