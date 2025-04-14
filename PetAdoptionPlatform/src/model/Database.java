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
        // Check if tables exist before creating them
        String checkTableSql = "SELECT name FROM sqlite_master WHERE type='table' AND name='Pet'";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkTableSql)) {
            
            // If Pet table doesn't exist, create all tables
            if (!rs.next()) {
                System.out.println("Creating database tables for the first time...");
                String createTablesSql = """
                    CREATE TABLE IF NOT EXISTS Pet (
                        pet_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        type TEXT,
                        size TEXT,
                        age INTEGER,
                        description TEXT,
                        status TEXT DEFAULT 'available',
                        gender TEXT,
                        breed TEXT,
                        health_status TEXT,
                        temperament TEXT,
                        image_data BLOB
                    );

                    CREATE TABLE IF NOT EXISTS Adopter (
                        adopter_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        contact_info TEXT,
                        preferences TEXT,
                        address TEXT,
                        mobile_number TEXT,
                        notes TEXT
                    );

                    CREATE TABLE IF NOT EXISTS Application (
                        application_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        adopter_id INTEGER NOT NULL,
                        pet_id INTEGER NOT NULL,
                        status TEXT DEFAULT 'pending',
                        address TEXT,
                        mobile_number TEXT,
                        notes TEXT,
                        FOREIGN KEY (adopter_id) REFERENCES Adopter(adopter_id) ON DELETE CASCADE,
                        FOREIGN KEY (pet_id) REFERENCES Pet(pet_id) ON DELETE CASCADE
                    );
                    """;

                try (Statement createStmt = conn.createStatement()) {
                    createStmt.executeUpdate(createTablesSql);
                    System.out.println("Database tables created successfully.");
                    insertSampleData(); // Add some initial data
                }
            } else {
                System.out.println("Database tables already exist.");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertSampleData() {
        // Check if there are any pets in the database
        String checkPetsSql = "SELECT COUNT(*) FROM Pet";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkPetsSql)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Adding sample data...");
                // Sample pets
                String insertPetsSql = """
                    INSERT INTO Pet (name, type, size, age, description, status, gender, breed, health_status, temperament)
                    VALUES 
                        ('Max', 'Dog', 'Medium', 2, 'Friendly and playful', 'available', 'Male', 'Labrador', 'Healthy', 'Friendly'),
                        ('Luna', 'Cat', 'Small', 1, 'Calm and affectionate', 'available', 'Female', 'Siamese', 'Healthy', 'Calm'),
                        ('Rocky', 'Dog', 'Large', 3, 'Energetic and loyal', 'available', 'Male', 'German Shepherd', 'Healthy', 'Energetic');
                    """;

                try (Statement insertStmt = conn.createStatement()) {
                    insertStmt.executeUpdate(insertPetsSql);
                    System.out.println("Sample data added successfully.");
                }
            } else {
                System.out.println("Sample data already exists.");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
            e.printStackTrace();
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
                byte[] imageData = rs.getBytes("image_data");
                System.out.println("Loading pet: " + rs.getString("name") + 
                                 ", Image data size: " + (imageData != null ? imageData.length + " bytes" : "null"));
                
                pets.add(new Pet(
                        rs.getInt("pet_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("size"),
                        rs.getInt("age"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("gender"),
                        rs.getString("breed"),
                        rs.getString("health_status"),
                        rs.getString("temperament"),
                        imageData
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pets: " + e.getMessage());
            e.printStackTrace();
        }
        return pets;
    }

    public static List<Pet> getAvailablePets() {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT * FROM Pet WHERE status = 'available'";
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
                        rs.getString("status"),
                        rs.getString("gender"),
                        rs.getString("breed"),
                        rs.getString("health_status"),
                        rs.getString("temperament"),
                        rs.getBytes("image_data")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available pets: " + e.getMessage());
        }
        return pets;
    }


    public static Pet getPetById(int petId) {
        System.out.println("Getting pet by ID: " + petId);
        String sql = "SELECT * FROM Pet WHERE pet_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, petId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Pet pet = new Pet(
                        rs.getInt("pet_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("size"),
                        rs.getInt("age"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("gender"),
                        rs.getString("breed"),
                        rs.getString("health_status"),
                        rs.getString("temperament"),
                        rs.getBytes("image_data")
                );
                System.out.println("Retrieved pet - ID: " + pet.getPetId() + 
                                 ", Name: " + pet.getName() + 
                                 ", Status: " + pet.getStatus());
                return pet;
            }
            System.out.println("No pet found with ID: " + petId);
        } catch (SQLException e) {
            System.err.println("Error fetching pet by ID: " + e.getMessage());
        }
        return null; // Not found
    }

    public static boolean addPet(Pet pet) throws SQLException {
        String sql = "INSERT INTO Pet (name, type, size, age, description, status, gender, breed, health_status, temperament, image_data) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pet.getName());
            pstmt.setString(2, pet.getType());
            pstmt.setString(3, pet.getSize());
            pstmt.setInt(4, pet.getAge());
            pstmt.setString(5, pet.getDescription());
            pstmt.setString(6, pet.getStatus());
            pstmt.setString(7, pet.getGender());
            pstmt.setString(8, pet.getBreed());
            pstmt.setString(9, pet.getHealthStatus());
            pstmt.setString(10, pet.getTemperament());
            pstmt.setBytes(11, pet.getImageData());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public static boolean updatePet(Pet pet) throws SQLException {
        String sql = "UPDATE Pet SET name = ?, type = ?, size = ?, age = ?, description = ?, status = ?, " +
                     "gender = ?, breed = ?, health_status = ?, temperament = ?, image_data = ? " +
                     "WHERE pet_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pet.getName());
            pstmt.setString(2, pet.getType());
            pstmt.setString(3, pet.getSize());
            pstmt.setInt(4, pet.getAge());
            pstmt.setString(5, pet.getDescription());
            pstmt.setString(6, pet.getStatus());
            pstmt.setString(7, pet.getGender());
            pstmt.setString(8, pet.getBreed());
            pstmt.setString(9, pet.getHealthStatus());
            pstmt.setString(10, pet.getTemperament());
            pstmt.setBytes(11, pet.getImageData());
            pstmt.setInt(12, pet.getPetId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public static boolean updatePetStatus(int petId, String status) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            System.out.println("Updating pet status - Pet ID: " + petId + ", New Status: " + status);
            
            // If status is being changed to available, reject all pending applications for this pet
            if ("available".equalsIgnoreCase(status)) {
                System.out.println("Status is 'available', checking for pending applications");
                String rejectAppsSql = "UPDATE Application SET status = 'rejected' WHERE pet_id = ? AND status = 'pending'";
                try (PreparedStatement pstmt = conn.prepareStatement(rejectAppsSql)) {
                    pstmt.setInt(1, petId);
                    int updatedRows = pstmt.executeUpdate();
                    System.out.println("Rejected " + updatedRows + " pending applications for pet " + petId);
                }
            }
            
            String sql = "UPDATE Pet SET status = ? WHERE pet_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, status);
                pstmt.setInt(2, petId);
                int affectedRows = pstmt.executeUpdate();
                System.out.println("Updated pet status - Affected rows: " + affectedRows);
                conn.commit(); // Commit transaction
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback on error
                System.err.println("Error in updatePetStatus transaction, rolling back: " + e.getMessage());
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Reset auto-commit
                conn.close(); // Close connection
            }
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
                        rs.getString("preferences"),
                        rs.getString("address"),
                        rs.getString("mobile_number"),
                        rs.getString("notes")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching adopter by ID: " + e.getMessage());
        }
        return null;
    }

    public static int addAdopter(Adopter adopter) throws SQLException {
        String sql = "INSERT INTO Adopter (name, contact_info, preferences, address, mobile_number, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, adopter.getName());
            pstmt.setString(2, adopter.getContactInfo());
            pstmt.setString(3, adopter.getPreferences());
            pstmt.setString(4, adopter.getAddress());
            pstmt.setString(5, adopter.getMobileNumber());
            pstmt.setString(6, adopter.getNotes());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        }
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
                        rs.getString("status"),
                        rs.getString("address"),
                        rs.getString("mobile_number"),
                        rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching applications: " + e.getMessage());
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
                        rs.getString("status"),
                        rs.getString("address"),
                        rs.getString("mobile_number"),
                        rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching applications by adopter: " + e.getMessage());
        }
        return applications;
    }

    public static boolean addApplication(Application application) throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false); // Start transaction
        
        try {
            // Insert the application
            String appSql = "INSERT INTO Application (adopter_id, pet_id, status, address, mobile_number, notes) " +
                          "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(appSql)) {
                pstmt.setInt(1, application.getAdopterId());
                pstmt.setInt(2, application.getPetId());
                pstmt.setString(3, application.getStatus());
                pstmt.setString(4, application.getAddress());
                pstmt.setString(5, application.getMobileNumber());
                pstmt.setString(6, application.getNotes());
                pstmt.executeUpdate();
            }

            // Update the pet's status to pending
            String petSql = "UPDATE Pet SET status = 'pending' WHERE pet_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(petSql)) {
                pstmt.setInt(1, application.getPetId());
                pstmt.executeUpdate();
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            conn.rollback(); // Rollback on error
            System.err.println("Error in addApplication transaction: " + e.getMessage());
            throw e;
        } finally {
            conn.setAutoCommit(true); // Reset auto-commit
        }
    }

    public static boolean updateApplicationStatus(int applicationId, String status) throws SQLException {
        String sql = "UPDATE Application SET status = ? WHERE application_id = ?";
        Connection conn = getConnection(); // Get the connection outside try-with-resources
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, applicationId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                Application app = getApplicationById(applicationId); // This needs connection too
                if (app != null) {
                    if ("approved".equalsIgnoreCase(status)) {
                        updatePetStatus(app.getPetId(), "adopted");
                    } else if ("pending".equalsIgnoreCase(status)) {
                        updatePetStatus(app.getPetId(), "pending");
                    } else if ("rejected".equalsIgnoreCase(status)) {
                        // Check if there are any other pending applications for this pet
                        String checkPendingSql = "SELECT COUNT(*) FROM Application WHERE pet_id = ? AND status = 'pending' AND application_id != ?";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkPendingSql)) { // Use the same connection
                            checkStmt.setInt(1, app.getPetId());
                            checkStmt.setInt(2, applicationId);
                            ResultSet rs = checkStmt.executeQuery();
                            if (rs.next() && rs.getInt(1) == 0) {
                                // No other pending applications, set pet back to available
                                updatePetStatus(app.getPetId(), "available");
                            }
                        } // checkStmt and rs are closed here
                    }
                }
            }

            return affectedRows > 0;
        }
        // Do NOT close the shared connection here
    }

    // Helper to get a single application (needed for status update logic)
    public static Application getApplicationById(int applicationId) {
        String sql = "SELECT * FROM Application WHERE application_id = ?";
        Application application = null;
        try { // Removed Connection from try-with-resources
            Connection conn = getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, applicationId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        application = new Application(
                                rs.getInt("application_id"),
                                rs.getInt("adopter_id"),
                                rs.getInt("pet_id"),
                                rs.getString("status"),
                                rs.getString("address"),
                                rs.getString("mobile_number"),
                                rs.getString("notes")
                        );
                    }
                } // rs is closed here
            } // pstmt is closed here
        } catch (SQLException e) {
            System.err.println("Error fetching application by ID: " + e.getMessage());
            e.printStackTrace(); // Also print stack trace here for better debugging
        }
        // Do NOT close the shared connection here
        return application;
    }
}