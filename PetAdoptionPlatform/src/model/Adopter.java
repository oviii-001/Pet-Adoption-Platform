package model;

public class Adopter {
    private int adopterId;
    private String name;
    private String contactInfo;
    private String preferences; // e.g., "type:Dog,size:Medium,age:<5"
    private String address;
    private String mobileNumber;
    private String notes;

    public Adopter(int adopterId, String name, String contactInfo, String preferences, String address, String mobileNumber, String notes) {
        this.adopterId = adopterId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.preferences = preferences;
        this.address = address;
        this.mobileNumber = mobileNumber;
        this.notes = notes;
    }

    // Getters
    public int getAdopterId() { return adopterId; }
    public String getName() { return name; }
    public String getContactInfo() { return contactInfo; }
    public String getPreferences() { return preferences; }
    public String getAddress() { return address; }
    public String getMobileNumber() { return mobileNumber; }
    public String getNotes() { return notes; }

    // Setters
    public void setAdopterId(int adopterId) { this.adopterId = adopterId; }
    public void setName(String name) { this.name = name; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void setPreferences(String preferences) { this.preferences = preferences; }
    public void setAddress(String address) { this.address = address; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return name + " (" + contactInfo + ")";
    }
}