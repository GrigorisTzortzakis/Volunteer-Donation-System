public abstract class Entity {
    private String name;
    private String description;
    private int id;

    // Constructor
    public Entity(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    // Returns basic entity information
    public String getEntityInfo() {
        return "ID: " + id + ", Name: " + name + ", Description: " + description;
    }

    // Abstract method to be implemented by subclasses
    public abstract String getDetails();

    // Override toString to provide all information
    @Override
    public String toString() {
        return getEntityInfo() + "\n" + getDetails();
    }
}