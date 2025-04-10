package model;

public class Pet {
    private int petId;
    private String name;
    private String type; // e.g., "Dog", "Cat"
    private String size; // e.g., "Small", "Medium", "Large"
    private int age;
    private String description;
    private String status; // e.g., "available", "adopted"
    private String gender;
    private String breed;
    private String healthStatus;
    private String temperament;

    public Pet(int petId, String name, String type, String size, int age, String description, String status,
              String gender, String breed, String healthStatus, String temperament) {
        this.petId = petId;
        this.name = name;
        this.type = type;
        this.size = size;
        this.age = age;
        this.description = description;
        this.status = status;
        this.gender = gender;
        this.breed = breed;
        this.healthStatus = healthStatus;
        this.temperament = temperament;
    }

    // Getters
    public int getPetId() { return petId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getSize() { return size; }
    public int getAge() { return age; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getGender() { return gender; }
    public String getBreed() { return breed; }
    public String getHealthStatus() { return healthStatus; }
    public String getTemperament() { return temperament; }

    // Setters
    public void setPetId(int petId) { this.petId = petId; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setSize(String size) { this.size = size; }
    public void setAge(int age) { this.age = age; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBreed(String breed) { this.breed = breed; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
    public void setTemperament(String temperament) { this.temperament = temperament; }

    @Override
    public String toString() {
        return name + " (" + type + ")"; // Simple representation
    }
}