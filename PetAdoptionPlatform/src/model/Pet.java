package model;

public class Pet {
    private int petId;
    private String name;
    private String type; // e.g., "Dog", "Cat"
    private String size; // e.g., "Small", "Medium", "Large"
    private int age;
    private String description;
    private String status; // e.g., "available", "adopted"

    public Pet(int petId, String name, String type, String size, int age, String description, String status) {
        this.petId = petId;
        this.name = name;
        this.type = type;
        this.size = size;
        this.age = age;
        this.description = description;
        this.status = status;
    }

    // Getters
    public int getPetId() { return petId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getSize() { return size; }
    public int getAge() { return age; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }

    // Setters (optional if mostly immutable after creation, but needed for editing)
    public void setPetId(int petId) { this.petId = petId; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setSize(String size) { this.size = size; }
    public void setAge(int age) { this.age = age; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return name + " (" + type + ")"; // Simple representation
    }
}