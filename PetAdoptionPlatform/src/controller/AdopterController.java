package controller;

import model.Database;
import model.Adopter; // Although we might not create full objects often here

import java.sql.SQLException;

public class AdopterController {

    // In this simple setup, we primarily interact via Adopter ID
    // We don't have a full Adopter management UI

    public boolean updateAdopterPreferences(int adopterId, String preferences) {
        try {
            // Basic validation of adopterId could be added
            if (adopterId <= 0) return false;
            return Database.updateAdopterPreferences(adopterId, preferences);
        } catch (SQLException e) {
            System.err.println("Error updating adopter preferences via controller: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Method to potentially add a new adopter if needed (e.g., if no default existed)
    // Returns the new adopter's ID or -1 on failure
    public int addAdopter(String name, String contactInfo, String preferences, String address, String mobileNumber, String notes) {
        Adopter newAdopter = new Adopter(0, name, contactInfo, preferences, address, mobileNumber, notes);
        try {
            return Database.addAdopter(newAdopter);
        } catch (SQLException e) {
            System.err.println("Error adding adopter via controller: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    // Overloaded method for backward compatibility
    public int addAdopter(String name, String contactInfo, String preferences) {
        return addAdopter(name, contactInfo, preferences, "", "", "");
    }

    public Adopter getAdopterById(int adopterId) {
        // Directly call database method, controller adds little value here for now
        return Database.getAdopterById(adopterId);
    }
}