package controller;

import model.Database;
import model.Pet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class PetController {

    // No direct view references needed if views pull data on demand

    public List<Pet> getAllPets() {
        return Database.getAllPets();
    }

    public List<Pet> getAvailablePets() {
        return Database.getAvailablePets();
    }


    public Pet getPetById(int petId) {
        return Database.getPetById(petId);
    }

    public boolean addPet(Pet pet) {
        try {
            return Database.addPet(pet);
        } catch (SQLException e) {
            System.err.println("Error adding pet via controller: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePet(Pet pet) {
        try {
            return Database.updatePet(pet);
        } catch (SQLException e) {
            System.err.println("Error updating pet via controller: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePet(int petId) {
        try {
            // Add checks here? E.g., prevent deleting adopted pets?
            // Or rely on FOREIGN KEY constraints / DB logic
            return Database.deletePet(petId);
        } catch (SQLException e) {
            System.err.println("Error deleting pet via controller: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePetStatus(int petId, String status) {
        try {
            System.out.println("Updating pet status - Pet ID: " + petId + ", New Status: " + status);
            boolean success = Database.updatePetStatus(petId, status);
            System.out.println("Update result: " + (success ? "Success" : "Failed"));
            return success;
        } catch (SQLException e) {
            System.err.println("Error updating pet status via controller: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- Pet Matching Logic ---
    public List<Pet> findMatchingPets(String preferencesString) {
        if (preferencesString == null || preferencesString.trim().isEmpty()) {
            return new ArrayList<>(); // No preferences, no matches
        }

        // 1. Parse Preferences String "key:value,key:value"
        Map<String, String> prefsMap = new HashMap<>();
        try {
            String[] pairs = preferencesString.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2 && !kv[1].trim().isEmpty() && !"Any".equalsIgnoreCase(kv[1].trim())) {
                    prefsMap.put(kv[0].trim().toLowerCase(), kv[1].trim());
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing preferences string: " + preferencesString + " - " + e.getMessage());
            return new ArrayList<>(); // Parsing error, return empty
        }


        if (prefsMap.isEmpty()) {
            return new ArrayList<>(); // Only "Any" or empty preferences selected
        }


        // 2. Fetch all *available* pets
        List<Pet> availablePets = Database.getAvailablePets();

        // 3. Filter pets based on preferences
        List<Pet> matchedPets = availablePets.stream()
                .filter(pet -> matches(pet, prefsMap))
                .collect(Collectors.toList());

        return matchedPets;
    }


    // Helper method for matching a single pet against the preferences map
    private boolean matches(Pet pet, Map<String, String> prefsMap) {
        boolean match = true; // Assume match until proven otherwise

        // Check Type
        if (prefsMap.containsKey("type")) {
            if (!prefsMap.get("type").equalsIgnoreCase(pet.getType())) {
                return false; // Type doesn't match
            }
        }

        // Check Size
        if (prefsMap.containsKey("size")) {
            if (!prefsMap.get("size").equalsIgnoreCase(pet.getSize())) {
                return false; // Size doesn't match
            }
        }

        // Check Age (Simple comparison: exact, <, >)
        if (prefsMap.containsKey("age")) {
            String agePref = prefsMap.get("age");
            try {
                if (agePref.startsWith("<")) {
                    int maxAge = Integer.parseInt(agePref.substring(1).trim());
                    if (pet.getAge() >= maxAge) return false;
                } else if (agePref.startsWith(">")) {
                    int minAge = Integer.parseInt(agePref.substring(1).trim());
                    if (pet.getAge() <= minAge) return false;
                } else {
                    int exactAge = Integer.parseInt(agePref.trim());
                    if (pet.getAge() != exactAge) return false;
                }
            } catch (NumberFormatException e) {
                System.err.println("Could not parse age preference: " + agePref);
                // Treat unparseable age pref as non-matching or ignore? Let's ignore for now.
                // return false;
            }
        }

        // If all checks passed (or weren't specified), it's a match
        return match;
    }
}