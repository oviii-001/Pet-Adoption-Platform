package model;

public class Adopter {
    private int adopterId;
    private String name;
    private String contactInfo;
    private String preferences; // e.g., "type:Dog,size:Medium,age:<5"

    public Adopter(int adopterId, String name, String contactInfo, String preferences) {
        this.adopterId = adopterId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.preferences = preferences;
    }

    // Getters
    public int getAdopterId() { return adopterId; }
    public String getName() { return name; }
    public String getContactInfo() { return contactInfo; }
    public String getPreferences() { return preferences; }

    // Setters
    public void setAdopterId(int adopterId) { this.adopterId = adopterId; }
    public void setName(String name) { this.name = name; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void setPreferences(String preferences) { this.preferences = preferences; }

    @Override
    public String toString() {
        return name + " (" + contactInfo + ")";
    }
}