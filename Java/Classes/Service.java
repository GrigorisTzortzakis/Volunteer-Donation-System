public class Service extends Entity {
    // Constructor
    public Service(String name, String description, int id) {
        super(name, description, id);
    }

    @Override
    public String getDetails() {
        return "Type: Service";
    }
}