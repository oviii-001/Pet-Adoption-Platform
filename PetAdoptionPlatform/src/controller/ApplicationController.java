package controller;

import model.Application;
import model.Database;
import model.Pet; // Needed for status check

import java.sql.SQLException;
import java.util.List;

public class ApplicationController {

    public List<Application> getAllApplications() {
        return Database.getAllApplications();
    }

    public List<Application> getApplicationsByAdopter(int adopterId) {
        return Database.getApplicationsByAdopter(adopterId);
    }

    public boolean submitApplication(Application application) {
        try {
            System.out.println("Starting application submission for Pet ID: " + application.getPetId() + 
                             ", Adopter ID: " + application.getAdopterId());
            
            // Controller logic: double-check pet availability before submitting to DB
            Pet pet = Database.getPetById(application.getPetId());
            System.out.println("Retrieved pet from database. Pet ID: " + application.getPetId() + 
                             ", Status: " + (pet != null ? pet.getStatus() : "null"));
            
            if (pet == null || !"available".equalsIgnoreCase(pet.getStatus())) {
                System.err.println("Controller check failed: Pet " + application.getPetId() + 
                                 " is not available for application. Current status: " + 
                                 (pet != null ? pet.getStatus() : "null"));
                return false; // Prevent submission if pet isn't available
            }

            // Check if this adopter already has a pending application for this pet
            List<Application> existingApps = Database.getApplicationsByAdopter(application.getAdopterId());
            System.out.println("Found " + existingApps.size() + " existing applications for adopter " + 
                             application.getAdopterId());
            
            for(Application existing : existingApps) {
                System.out.println("Checking existing application - Pet ID: " + existing.getPetId() + 
                                 ", Status: " + existing.getStatus());
                if (existing.getPetId() == application.getPetId() &&
                        "pending".equalsIgnoreCase(existing.getStatus())) {
                    System.err.println("Controller check failed: Adopter " + application.getAdopterId() +
                            " already has a pending application for Pet " + application.getPetId());
                    return false;
                }
            }

            System.out.println("All checks passed, proceeding with application submission");
            boolean result = Database.addApplication(application);
            System.out.println("Application submission result: " + result);
            return result;
        } catch (SQLException e) {
            System.err.println("Error submitting application via controller: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateApplicationStatus(int applicationId, String newStatus) {
        try {
            // Add potential business logic here, e.g., notifications?
            // Ensure valid status transition? (e.g., can't go from approved to rejected?)
            // For now, rely on the Database layer's logic (which updates pet status on approval)
            return Database.updateApplicationStatus(applicationId, newStatus);
        } catch (SQLException e) {
            System.err.println("Error updating application status via controller: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}